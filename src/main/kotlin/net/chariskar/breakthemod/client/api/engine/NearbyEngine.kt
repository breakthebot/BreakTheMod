/*
 * This file is part of breakthemod.
 *
 * breakthemod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemod. If not, see <https://www.gnu.org/licenses/>.
 */

package net.chariskar.breakthemod.client.api.engine

import kotlinx.coroutines.*
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.minecraft.world.World
import kotlin.math.roundToInt
import kotlin.math.sqrt
import java.util.concurrent.CopyOnWriteArraySet

private object EngineScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    fun shutdown() = scope.cancel()
}

class NearbyEngine private constructor() {

    companion object {
        private const val UPDATE_INTERVAL_MS: Long = 500
        private const val DISTANCE_THRESHOLD: Double = 200.0
        private const val DIRECTION_STEP: Double = 45.0
        val DIRECTIONS = arrayOf("S", "SW", "W", "NW", "N", "NE", "E", "SE")
        private var instance: NearbyEngine? = null
        val scope: CoroutineScope get() = EngineScope.scope

        fun getInstance(): NearbyEngine = instance ?: synchronized(this) {
            if (instance == null) instance = NearbyEngine()
            instance!!
        }
    }

    data class PlayerInfo(val name: String, var position: Vec3d) {
        fun calculateDistance(other: Vec3d): Double {
            val dx = position.x - other.x
            val dy = position.y - other.y
            val dz = position.z - other.z
            return dx * dx + dy * dy + dz * dz
        }

        fun isUnderBlock(world: World): Boolean {
            val x = position.x.toInt()
            val z = position.z.toInt()
            val topY = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z)
            if (position.y.toInt() > topY) return false
            for (y in position.y.toInt() until topY) {
                if (!world.getBlockState(BlockPos(x, y, z)).isAir) return true
            }
            return false
        }

        fun shouldSkipSpecial(player: PlayerEntity): Boolean {
            val isInVehicle = player.hasVehicle()
            val isSneaking = player.isSneaking
            val inRiptide = player.isUsingRiptide
            val isInNether = player.world.registryKey.value.toString().contains("nether")
            return isInVehicle || isSneaking || inRiptide || isInNether
        }

        suspend fun shouldSkip(player: PlayerEntity, world: World): Boolean = withContext(Dispatchers.Default) {
            shouldSkipSpecial(player) || isUnderBlock(world)
        }


        fun directionFrom(player: PlayerEntity): String {
            val normalized = ((player.yaw + 180) % 360 + 360) % 360
            val index = (normalized / DIRECTION_STEP).roundToInt() % 8
            return DIRECTIONS[index]
        }

        fun distanceFrom(player: PlayerEntity): Int {
            return sqrt(calculateDistance(player.pos)).toInt()
        }

        override fun toString(): String {
            val client = MinecraftClient.getInstance()
            val player = client.player ?: return name
            val distance = distanceFrom(player)
            val direction = directionFrom(player)
            return "-$name (${position.x.toInt()}, ${position.z.toInt()}) direction: $direction, distance: $distance blocks"
        }
    }

    private val playerInfoList: MutableSet<PlayerInfo> = CopyOnWriteArraySet()
    private var lastUpdateTime: Long = 0

    init {
        scope.launch {
            while (isActive) {
                updatePlayersSafe()
                delay(UPDATE_INTERVAL_MS)
            }
        }
    }

    private suspend fun updatePlayersSafe() {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return
        val world = client.world ?: return

        val newPlayers = updateNearbyPlayers(player, world)
        playerInfoList.clear()
        playerInfoList.addAll(newPlayers)
    }

    suspend fun getPlayers(): Set<PlayerInfo> {
        return HashSet(playerInfoList)
    }

    suspend fun updateNearbyPlayers(self: PlayerEntity, world: World): Set<PlayerInfo> {
        val selfPos = self.pos
        val selfName = self.gameProfile.name
        val players = mutableSetOf<PlayerInfo>()

        for (other in world.players) {
            if (other == self || other.gameProfile.name == selfName) continue

            val info = PlayerInfo(other.gameProfile.name, other.pos)

            val distance = info.calculateDistance(selfPos)
            if (distance > DISTANCE_THRESHOLD * DISTANCE_THRESHOLD) continue
            if (info.shouldSkip(other, world)) continue
            players.add(info)
        }

        return players
    }
}

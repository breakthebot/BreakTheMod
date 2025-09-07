/*
 * This file is part of breakthemodRewrite.
 *
 * breakthemodRewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemodRewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemodRewrite. If not, see <https://www.gnu.org/licenses/>.
 */

package net.chariskar.breakthemod.client.api.engine

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.minecraft.world.World
import java.util.Objects
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.math.roundToInt
import kotlin.math.sqrt

class NearbyEngine {

    companion object {
        private const val UPDATE_INTERVAL_MS: Long = 1000
        private const val DISTANCE_THRESHOLD: Double = 200.0
        private const val DIRECTION_STEP: Double = 45.0
        public val DIRECTIONS: Array<String> = arrayOf<String>("S", "SW", "W", "NW", "N", "NE", "E", "SE")
        @OptIn(ExperimentalAtomicApi::class)
        private var lastUpdateTime: AtomicLong = AtomicLong(0)
        private var playerInfoList: MutableSet<PlayerInfo> = HashSet<PlayerInfo>()

        fun getDirectionFromYaw(yaw: Float): String {
            val normalized: Float = ((yaw + 180) % 360 + 360) % 360
            val index = (normalized / DIRECTION_STEP).roundToInt() % 8
            return DIRECTIONS[index]
        }
    }

    data class PlayerInfo(
        public val name: String,
        public var position: Vec3d
    ) {
        fun calculateDistance(other: Vec3d): Double {
            val dx: Double = position.x - other.x
            val dy: Double = position.y - other.y
            val dz: Double = position.z - other.z
            return dx * dx + dy * dy + dz * dz
        }

        fun isUnderBlock(world: World): Boolean {
            // capital to differentiate.
            val X: Int = position.x.toInt()
            val Z: Int = position.z.toInt()

            val topY: Int = world.getTopY(Heightmap.Type.WORLD_SURFACE, position.x.toInt(), position.z.toInt())
            if (position.y.toInt() > topY) { return false }
            for (y in position.y.toInt() until topY) {
                if (!world.getBlockState(BlockPos(X, y, Z)).isAir) {
                    return true;
                }
            }
            return false
        }

        fun shouldSkipSpecial(player: PlayerEntity): Boolean {
            val isInVechile: Boolean = player.hasVehicle()
            val isSneaking: Boolean = player.isSneaking
            val inRiptide: Boolean = player.isUsingRiptide
            val isInNether: Boolean = player.world.registryKey.value.toString().contains("nether")
            return isInVechile || isSneaking || inRiptide || isInNether
        }

        fun shouldSkip(player: PlayerEntity,world: World): Boolean {
            return shouldSkipSpecial(player) || isUnderBlock(world)
        }

        override fun toString(): String {
            val client: MinecraftClient = MinecraftClient.getInstance()
            val player: PlayerEntity = client.player!!
            val distance: Int = sqrt(calculateDistance(player.pos)).toInt()
            val direction: String = getDirectionFromYaw(player.yaw)
            return "$name (${position.x.toInt()}), (${position.z.toInt()}) direction: $direction, distance: $distance blocks"

        }

    }

    @OptIn(ExperimentalAtomicApi::class)
    @Synchronized
    public fun updateNearbyPlayers(self: PlayerEntity, world: World): Set<PlayerInfo> {
        val currentTime: Long = System.currentTimeMillis()

        if (currentTime - lastUpdateTime.load() < UPDATE_INTERVAL_MS) {
            return HashSet<PlayerInfo>(playerInfoList)
        }

        lastUpdateTime.exchange(currentTime)

        playerInfoList.clear()

        val selfPos: Vec3d = self.pos
        val selfName: String = self.name.toString()

        val players: MutableSet<PlayerInfo> = mutableSetOf()

        for (other in world.players) {
            if (other == self || Objects.equals(other.name, selfName)) continue

            val otherPl: PlayerInfo = PlayerInfo(
                other.name.toString(),
                other.pos
            )

            val distance: Double = otherPl.calculateDistance(selfPos)

            if (distance > DISTANCE_THRESHOLD * DISTANCE_THRESHOLD) continue

            if (!otherPl.shouldSkip(other, world)) {
                players.add(otherPl)
            }
        }
        return players
    }

}
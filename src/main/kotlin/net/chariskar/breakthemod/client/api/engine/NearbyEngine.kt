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
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.concurrent.CopyOnWriteArraySet

object NearbyEngine {
    private const val DISTANCE_THRESHOLD: Double = 200.0

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val playerInfoList: MutableSet<PlayerInfo> = CopyOnWriteArraySet()

    fun getPlayers(): Set<PlayerInfo> = HashSet(playerInfoList)

    /**
     * Checks if there are any players nearby.
     * @param self The player entity
     * @param world The client world
     * */
    fun updateNearbyPlayers(
        self: PlayerEntity,
        world: World
    ): Set<PlayerInfo> {
        val selfPos = Vec3d(self.x, self.y, self.z)
        val selfName = self.gameProfile.name
        val players = mutableSetOf<PlayerInfo>()

        for (other in world.players) {
            if (other === self) continue
            if (other.gameProfile.name == selfName) continue

            val info = PlayerInfo(
                other.gameProfile.name,
                Vec3d(other.x, other.y, other.z)
            )

            if (info.calculateDistance(selfPos) >
                DISTANCE_THRESHOLD * DISTANCE_THRESHOLD
            ) continue

            if (info.shouldSkip(other, world)) continue
            players.add(info)
        }

        return players
    }

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (!Config.config.radarEnabled) return@register

            val player = client.player ?: return@register
            val world = client.world ?: return@register

            val nearby = updateNearbyPlayers(player, world)

            playerInfoList.clear()
            playerInfoList.addAll(nearby)
        }
    }
}

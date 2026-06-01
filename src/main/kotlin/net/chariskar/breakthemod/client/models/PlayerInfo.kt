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

package net.chariskar.breakthemod.client.models

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Helper class for the NearbyEngine.
 * @param name The name of the player.
 * @param client The client instance.
 * @param position The position of the player.
 * */
data class PlayerInfo(
    val name: String,
    val client: MinecraftClient,
    var position: Vec3d,
) {
    val directions = arrayOf("S", "SW", "W", "NW", "N", "NE", "E", "SE")

    /**
     *  Calculates the distance between the player and the location provided
     *  @param other The position of the outer player.
     *  */
    fun calculateDistance(other: Vec3d): Double {
        val dx = position.x - other.x
        val dy = position.y - other.y
        val dz = position.z - other.z
        return dx * dx + dy * dy + dz * dz
    }

    /**
     * Climbs from the current pos until world max Y to check if there are any blocks above.
     * @param world The client world.
     * @param pos The block the player is standing on.
     * */
    fun isUnderBlock(world: World, pos: BlockPos): Boolean {
        val topY = world.dimension.logicalHeight
        for (y in pos.y + 1..topY) {
            val checkPos = BlockPos(pos.x, y, pos.z)
            val state = world.getBlockState(checkPos)
            if (!state.isAir) { return true }
        }
        return false
    }

    /**
     * Checks if the player is in any state that would prevent him from being visible.
     * @param player The player entity.
     *  */
    fun shouldSkipSpecial(player: PlayerEntity): Boolean {
        val isInVehicle = player.hasVehicle()
        val isSneaking = player.isSneaking
        val inRiptide = player.isUsingRiptide
        val isInvisible = player.isInvisible
        val isInNether = client.world
            ?.registryKey?.value.toString().contains("nether")
        return isInVehicle || isSneaking || inRiptide || isInNether || isInvisible
    }

    /**
     * Combination of [shouldSkipSpecial] and [isUnderBlock] to return a final result.
     * @param player The player.
     * @param world The client world.
     * */
    fun shouldSkip(
        player: PlayerEntity,
        world: World
    ): Boolean = shouldSkipSpecial(player) || isUnderBlock(world, player.blockPos)

    /**
     *  Calculates the direction to a player.
     *  @param player The player.
     *  */
    fun directionFrom(player: PlayerEntity): String {
        val dx = (position.x.toInt() - player.blockPos.x).toDouble()
        val dz = (position.z.toInt() - player.blockPos.z).toDouble()

        val angle = Math.toDegrees(atan2(-dx, dz))
        val index = (((angle + 360.0) % 360.0 + 22.5) / 45.0).toInt() % 8

        return directions[index]
    }

    fun distanceFrom(player: PlayerEntity): Int {
        return sqrt(calculateDistance(Vec3d(player.x, player.y, player.z))).toInt()
    }

    override fun toString(): String {
        val player = client.player ?: return name
        return "-$name (${position.x.toInt()}, ${position.z.toInt()}) direction: ${directionFrom(player)}, distance: ${distanceFrom(player)} blocks"
    }
}
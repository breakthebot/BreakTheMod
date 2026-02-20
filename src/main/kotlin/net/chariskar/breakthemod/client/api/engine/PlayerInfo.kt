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

import net.minecraft.block.ShapeContext
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import kotlin.math.roundToInt
import kotlin.math.sqrt


/**
 * Wrapper class containing most nearby check methods.
 * @param name The name of the player.
 * @param position The position of the player.
 * */
data class PlayerInfo(val name: String, var position: Vec3d) {
    private val directionStep: Double = 45.0
    val directions = arrayOf("S", "SW", "W", "NW", "N", "NE", "E", "SE")

    fun calculateDistance(other: Vec3d): Double {
        val dx = position.x - other.x
        val dy = position.y - other.y
        val dz = position.z - other.z
        return dx * dx + dy * dy + dz * dz
    }

    fun isUnderBlock(world: World): Boolean {
        val from = position
        val to = position.add(0.0, 256.0, 0.0)

        val result = world.raycast(
            RaycastContext(
                from,
                to,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                ShapeContext.absent()
            )
        )
        return result.type != HitResult.Type.MISS
    }

    fun shouldSkipSpecial(player: PlayerEntity): Boolean {
        val isInVehicle = player.hasVehicle()
        val isSneaking = player.isSneaking
        val inRiptide = player.isUsingRiptide
        val isInNether = MinecraftClient.getInstance().world?.registryKey?.value.toString().contains("nether")
        return isInVehicle || isSneaking || inRiptide || isInNether
    }

    fun shouldSkip(player: PlayerEntity, world: World): Boolean = shouldSkipSpecial(player) || isUnderBlock(world)

    fun directionFrom(player: PlayerEntity): String {
        val normalized = ((player.yaw + 180) % 360 + 360) % 360
        val index = (normalized / directionStep).roundToInt() % 8
        return directions[index]
    }

    fun distanceFrom(player: PlayerEntity): Int {
        return sqrt(calculateDistance(Vec3d(player.x, player.y, player.z))).toInt()
    }

    override fun toString(): String {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return name
        return "-$name (${position.x.toInt()}, ${position.z.toInt()}) direction: ${directionFrom(player)}, distance: ${distanceFrom(player)} blocks"
    }
}
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

package net.chariskar.breakthemod.client.utils

import io.github.oshai.kotlinlogging.KotlinLogging
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import xaero.common.minimap.waypoints.Waypoint
import xaero.hud.minimap.BuiltInHudModules
import xaero.hud.minimap.waypoint.WaypointColor
import xaero.hud.minimap.waypoint.WaypointPurpose

/**
 * Utility for using xaero minimap waypoints.
 * */
object WaypointUtil {

    val logger = KotlinLogging.logger { }

    val xaeroLoaded: Boolean
        get() = FabricLoader.getInstance()
            .isModLoaded("xaerominimap")

    /**
     * Create a new temporary waypoint to the destination specified.
     * @param target The target x and z coordinates.
     * @param name The name for the waypoint (also used for initials).
     * */
    fun createRouteWaypoint(target: Pair<Int, Int>, name: String) {
        if (!xaeroLoaded) return

        val client = Minecraft.getInstance()

        val session = BuiltInHudModules.MINIMAP.getCurrentSession()
        val world = session.getWorldManager().currentWorld

        val set = world.currentWaypointSet

        val y = client.player?.blockPosition()?.y!!

        val waypoint = Waypoint(
            target.first, y, target.second,
            name, getInitials(name),
            WaypointColor.PURPLE, WaypointPurpose.DESTINATION,
            true,
            false
        )

        waypoint.isTemporary = true

        set.add(waypoint, true)
        session.waypointSession.setChangedTime = System.currentTimeMillis()

        logger.info { "Set waypoint at coords (x: ${target.first}, y: $y, z: ${target.second}) with name $name. " }
    }

    fun getInitials(name: String) = name.split(" ").map { it[0] }.joinToString()
}

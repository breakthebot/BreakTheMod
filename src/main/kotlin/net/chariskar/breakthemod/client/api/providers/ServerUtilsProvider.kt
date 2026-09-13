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

package net.chariskar.breakthemod.client.api.providers

import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.Minecraft

/**
 * Provides the necessary utils to assess what multiplayer server we are playing on.
 * */
interface ServerUtilsProvider {

    /** Returns whether the current server is EarthMC. */
    fun isEarthMc(): Boolean {
        val server = Minecraft.getInstance().currentServer ?: return false
        return server.ip
            .substringBefore(',')
            .contains("earthmc", ignoreCase = true)
    }

    /** Returns whether the mod should be active on the current server. */
    fun isModEnabled(): Boolean = Breakthemod.debug || isEarthMc() || Config.config.enabledOnOtherServers
}

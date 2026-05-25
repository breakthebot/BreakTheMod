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

import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.MinecraftClient

/**
 * Provides the necessary utils to assess what multiplayer server we are playing on.
 * */
interface ServerUtilsProvider {

    /** Non biased EarthMc check.*/
    fun isEarthMc(): Boolean {
        val serverInfo = MinecraftClient.getInstance().currentServerEntry ?: return false
        return splitAddress(serverInfo.address).contains("earthmc")
    }

    fun isModEnabled(): Boolean = isEarthMc().or(Config.config.enabledOnOtherServers)

    fun replaceApiUrl() {
        val serverInfo = MinecraftClient.getInstance().currentServerEntry?.address ?: return
        if (
            splitAddress(serverInfo).contains("earthmc")
            &&
            Config.config.libraryConfig.apiUrl.contains("aurora")
        ) {
            Config.setApiUrl(
                "https://api.earthmc.net/v4"
            )
        }
    }

    private fun splitAddress(
        serverInfo: String
    ): String {
        return serverInfo.split(",".toRegex()).dropLastWhile {
            it.isEmpty()
        }.toTypedArray()[0].lowercase()
    }
}
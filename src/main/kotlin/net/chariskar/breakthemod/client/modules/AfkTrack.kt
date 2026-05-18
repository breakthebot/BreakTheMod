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

package net.chariskar.breakthemod.client.modules

import net.chariskar.breakthemod.client.api.BaseModule
import net.chariskar.breakthemod.client.utils.ServerUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.text.Text

object AfkTrack : BaseModule() {
    override val name = "AfkTrack"
    override val description: String = "Utility module for tracking afk status."
    var isAfk: Boolean = false

    override fun enable() {
        ClientReceiveMessageEvents.GAME.register(ClientReceiveMessageEvents.Game { message: Text?, _: Boolean ->
            if (!ServerUtils.isEarthMc()) return@Game
            val messageText = message?.string ?: return@Game

            if (messageText.equals("You are now AFK.", ignoreCase = true)) {
                isAfk = true
            }

            if (messageText.equals("You are no longer afk.", ignoreCase = true)) {
                isAfk = false
                sendMessage("The following shops run out of stock whilst you were afk:")
                ShopTracker.emptyShops.forEach { sendMessage(it.toString()) }
            }
        })

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPlayNetworkHandler?, _: MinecraftClient? ->
            isAfk = false
        })
    }

}
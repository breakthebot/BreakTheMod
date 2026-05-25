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

/// credit to https://github.com/Veyronity/Earthy/blob/master/client/fabric/src/main/java/au/lupine/earthy/fabric/module/ChatPreview.java

package net.chariskar.breakthemod.client.modules

import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.utils.ChatChannel
import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.text.Text
import java.util.regex.Pattern

/**
 * Chat preview logic handler.
 *
 * @property inPartyChat Party chat flag.
 * @property chatChannel The current channel the player is in.
 * */
object ChatPreview : BaseModule(
    "ChatPreview",
    "Chat preview logic handler"
) {

    var inPartyChat: Boolean = false
        private set
    var chatChannel: ChatChannel? = null
        private set

    override fun enable() {
        ClientReceiveMessageEvents.GAME.register(ClientReceiveMessageEvents.Game { message: Text?, _: Boolean ->
            if (!isEarthMc()) return@Game

            val string = message?.string ?: return@Game

            if (string.startsWith("You are currently in")) {
                chatChannel = parseCurrentChatChannel(string)
            }

            val youHaveJoined = "» You have joined the channel: "
            if (string.startsWith(youHaveJoined)) {
                val cut = string.removePrefix(youHaveJoined)
                chatChannel = ChatChannel.getOrDefault(cut.substring(0, cut.length - 1))
            }

            if (string == "(mcMMO-Chat) Your chat messages will now be automatically delivered to the Party chat channel.") {
                inPartyChat = true
            }

            if (string == "(mcMMO-Chat) Your chat messages will no longer be automatically delivered to specific chat channels." ||
                string == "You have left that party" ||
                string == "The party has been disbanded" ||
                string.startsWith("You were kicked from party") ||
                string == "Error: You are not in a party."
            ) {
                inPartyChat = false
            }

        })

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPlayNetworkHandler?, _: MinecraftClient? ->
            inPartyChat = false
        })
    }

    private fun parseCurrentChatChannel(message: String?): ChatChannel {
        val pattern = Pattern.compile("(\\w+) \\(write\\)")
        val matcher = pattern.matcher(message)

        if (matcher.find()) {
            val name: String = matcher.group(1) ?: return ChatChannel.GLOBAL

            return ChatChannel.getOrDefault(name)
        } else {
            return ChatChannel.GLOBAL
        }
    }
}
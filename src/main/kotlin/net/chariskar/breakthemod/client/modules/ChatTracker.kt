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

import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.models.ChatChannel
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3
import java.util.regex.Pattern

/**
 * @param coords The coords of the shop.
 * @param item The name of the item the shop sells.
 * */
data class ShopObject(
    val coords: Vec3,
    val item: String
) {
    override fun toString(): String {
        return "-Shop at ${coords.x}, ${coords.y}, ${coords.z} ($item)"
    }
}

/**
 * Combined module that all message operations take place.
 * @property emptyShops The empty shops of the player.
 * @property isAfk Afk status of the player.
 * @property inPartyChat If the player is in party chat.
 * @property chatChannel The channel the player is in.
 */
object ChatTracker : BaseModule(
    "ChatTracker",
    "Combined utility to get data from messages.",
    false
) {
    val emptyShops: MutableList<ShopObject> = mutableListOf()

    var isAfk: Boolean = false
        private set
    var inPartyChat: Boolean = false
        private set
    var chatChannel: ChatChannel? = null
        private set 

    val shopRegex = Regex(
        """at\s+(-?\d+)\s*,\s*(-?\d+)\s*,\s*(-?\d+).*?run out of\s+(.+?)!?$"""
    )

    override fun enable() {
        ClientReceiveMessageEvents.GAME.register(ClientReceiveMessageEvents.Game { message: Component?, _: Boolean ->
            if (!isEarthMc()) return@Game
            val message = message?.string ?: return@Game

            afkTrack(message)
            channelTrack(message)
            val shop = parseShopObject(message) ?: return@Game

            emptyShops.add(shop)
        })

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPacketListener?, _: Minecraft? ->
            isAfk = false
            inPartyChat = false
            emptyShops.clear()
        })
    }

    fun afkTrack(message: String) {
        if (message.equals("You are now AFK.", ignoreCase = true)) {
            isAfk = true
        }

        if (message.equals("You are no longer afk.", ignoreCase = true)) {
            if (emptyShops.isEmpty()) return
            isAfk = false
            sendMessage("The following shops run out of stock whilst you were afk:")
            emptyShops.forEach { sendMessage(it.toString()) }
            emptyShops.clear()
        }
    }

    fun channelTrack(message: String) {
        if (message.startsWith("You are currently in")) {
            chatChannel = parseCurrentChatChannel(message)
        }

        val youHaveJoined = "» You have joined the channel: "
        if (message.startsWith(youHaveJoined)) {
            val cut = message.removePrefix(youHaveJoined)
            chatChannel = ChatChannel.getOrDefault(cut.substring(0, cut.length - 1))
        }

        if (message == "(mcMMO-Chat) Your chat messages will now be automatically delivered to the Party chat channel.") {
            inPartyChat = true
        }

        if (message == "(mcMMO-Chat) Your chat messages will no longer be automatically delivered to specific chat channels." ||
            message == "You have left that party" ||
            message == "The party has been disbanded" ||
            message.startsWith("You were kicked from party") ||
            message == "Error: You are not in a party."
        ) {
            inPartyChat = false
        }
    }

    private fun parseCurrentChatChannel(message: String): ChatChannel {
        val pattern = Pattern.compile("(\\w+) \\(write\\)")
        val matcher = pattern.matcher(message)

        if (matcher.find()) {
            val name: String = matcher.group(1) ?: return ChatChannel.GLOBAL

            return ChatChannel.getOrDefault(name)
        } else {
            return ChatChannel.GLOBAL
        }
    }

    fun parseShopObject(string: String): ShopObject? {
        val match = shopRegex.find(string)
        val (x, y, z, item) = match?.destructured ?: return null

        return ShopObject(
            Vec3(
                x.toDouble(),
                y.toDouble(),
                z.toDouble()
            ),
            item
        )
    }

}
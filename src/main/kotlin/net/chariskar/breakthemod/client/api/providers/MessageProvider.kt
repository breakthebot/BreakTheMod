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

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor

private val client = Minecraft.getInstance()

val prefix: Component by lazy {
    val segments = listOf(
        "Break" to "#EAEAEA",
        "The" to "#4B56FF",
        "Mod"  to "#FF8C1A",
        ">> " to "#FFFFFF"
    )

    val prefix = Component.empty()

    for (segment in segments) {
        val text: String = segment.first
        val color = TextColor.fromRgb(segment.second.substring(1).toInt(16))

        for (c in text.toCharArray()) {
            prefix.append(Component.literal(c.toString()).withColor(color))
        }
    }
    prefix
}

/**
 * Provides functions for interacting with the user.
 * */
interface MessageProvider {
    /**
     * Helper utility for sending messages.
     *
     * @param message The message to be sent.
     */
    fun sendMessage(message: Component) {
        client.execute {
            client.player?.sendSystemMessage(prefix.copy().append(message))
        }
    }

    /**
     * Helper utility for sending messages.
     *
     * @param message The message to be sent.
     * @param colour The color to attach to the message.
     */
    fun sendMessage(
        message: Component,
        colour: TextColor
    ) {
        val chatMessage = Component.empty().apply {
            append(message)
            withColor(colour)
        }
        sendMessage(chatMessage)
    }

    fun sendMessage(message: String) = sendMessage(Component.literal(message))

    fun sendError() = sendMessage(Component.literal("Command has exited with an exception"), TextColor.RED)

    fun sendError(message: String) = sendMessage(Component.literal(message), TextColor.RED)

    fun sendWarning(message: String) = sendMessage(Component.literal(message), TextColor.YELLOW)
}
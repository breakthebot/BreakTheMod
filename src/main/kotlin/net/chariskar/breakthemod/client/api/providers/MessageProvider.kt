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

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting

private val client = MinecraftClient.getInstance()

val prefix: Text by lazy {
    val segments = listOf(
        "Break" to "#EAEAEA",
        "The" to "#4B56FF",
        "Mod"  to "#FF8C1A",
        ">> " to "#FFFFFF"
    )

    val prefix = Text.empty()

    for (segment in segments) {
        val text: String = segment.first
        val color = TextColor.fromRgb(segment.second.substring(1).toInt(16))

        for (c in text.toCharArray()) {
            prefix.append(Text.literal(c.toString()).setStyle(Style.EMPTY.withColor(color)))
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
    fun sendMessage(message: Text)  {
        client.execute {
            client.player?.sendMessage(prefix.copy().append(message), false)
        }
    }

    /**
     * Helper utility for sending messages.
     *
     * @param message The message to be sent.
     * @param colour The color to attach to the message.
     */
    fun sendMessage(
        message: Text,
        colour: Formatting
    ) {
        val chatMessage = Text.empty().apply {
            append(message)
            styled { Style.EMPTY.withColor(colour) }
        }
        sendMessage(chatMessage)
    }

    fun sendMessage(message: String) = sendMessage(Text.literal(message))

    fun sendError() = sendMessage(Text.literal("Command has exited with an exception"), Formatting.RED)

    fun sendError(message: String) = sendMessage(Text.literal(message), Formatting.RED)

    fun sendWarning(message: String) = sendMessage(Text.literal(message), Formatting.YELLOW)
}
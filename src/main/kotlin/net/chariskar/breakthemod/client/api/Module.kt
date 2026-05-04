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

package net.chariskar.breakthemod.client.api

import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Prefix
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @property name Module name.
 * @property description Description.
 * */
abstract class Module {
    var name: String = ""
    var description: String = ""

    val logger: Logger = LoggerFactory.getLogger("breakthemod")
    val client: MinecraftClient = MinecraftClient.getInstance()

    var enabled: Boolean = false

    fun launch() {
        try {
            enable()
            enabled = true
        }
        catch (e: Exception) {
            logger.error("Error encountered when enabling $name")
            if (Config.getDevMode()) { logger.error("Error ${e.message}") }
            enabled = false
        }
    }

    abstract fun disable()

    abstract fun enable()

    open fun getModuleDescription(): String = "$name: $description"

    /**
     * Helper utility for sending messages.
     *
     * @param message The message to be sent
     */
    fun sendMessage(message: Text)  {
        client.execute {
            client.player?.sendMessage(Prefix.prefix.copy().append(message), false)
        }
    }

    /**
     * Helper utility for sending messages.
     *
     * @param message The message to be sent
     * @param colour The color to attach to the message
     */
    fun sendMessage(
        message: Text,
        colour: Formatting
    ) {
        val chatMessage = Text.of(message).apply {
            Style.EMPTY.withColor(colour)
        }
        sendMessage(chatMessage)
    }

    fun sendError() = sendMessage(Text.literal("Command has exited with an exception"))

    fun sendError(message: String) = sendMessage(Text.literal(message), Formatting.RED)

    fun sendWarning(message: String) = sendMessage(Text.literal(message), Formatting.YELLOW)

}
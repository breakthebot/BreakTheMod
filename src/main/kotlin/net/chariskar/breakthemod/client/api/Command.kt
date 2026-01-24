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

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Prefix
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

private object CommandScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    fun shutdown() {
        scope.cancel()
    }
}

abstract class Command {
    val logger: Logger = LoggerFactory.getLogger("breakthemod")
    val fetch: Fetch = Fetch
    val client: MinecraftClient = MinecraftClient.getInstance()
    protected val scope = CommandScope.scope

    var name: String = ""
    var description: String = ""
    var usageSuffix: String = ""

    fun getUsage(): String { return "/" + name + (usageSuffix.ifEmpty { "" }) }

    /**
     * @param ctx the command context.
     */
    protected open fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        return 0;
    }

    /**
     * Internal method, executes command code.
     * @param ctx The Command context.
     * @return 0 if success, 1 if error
     * @throws CommandSyntaxException If invalid syntax
     */
    @Throws(CommandSyntaxException::class)
    protected fun run(ctx: CommandContext<FabricClientCommandSource>): Int {
        try {
            return execute(ctx)
        } catch (e: CommandSyntaxException) {
            throw e
        } catch (e: Exception) {
            sendError()
            logError("Unexpected error has occurred while running $name", e)
            return 0
        }
    }

    /**
     * Command register function.
     * @param dispatcher Command registration dispatcher
     * */
    open fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                    if (!getEnabled()) {return@Command 0}
                    return@Command run(context)
                })
        )
    }

    /**
     * Helper utility for sending messages.
     *
     * @param message The message to be sent
     */
    fun sendMessage(message: Text) {
        client.player?.sendMessage(Prefix.prefix.copy().append(message), false)
    }

    /**
     * Helper utility for sending messages.
     *
     * @param message The message to be sent
     * @param style The color to attach to the message
     */
    fun sendMessage(message: Text, style: Formatting) {
        if (client.player != null) {
            val prefix: Text = Prefix.prefix
            val chatMessage: Text = Text.empty().append(prefix).append(Text.empty().append(message).setStyle(Style.EMPTY.withColor(style)))
            sendMessage(chatMessage)
        }
    }

    fun sendError() {
        sendMessage(Text.literal("Command has exited with an exception"))
    }

    protected fun logError(message: String?, e: Exception) {
        logger.error("{}{}", message, e.message)
        if (Config.getDevMode()) {
            e.printStackTrace()
        }
    }
}
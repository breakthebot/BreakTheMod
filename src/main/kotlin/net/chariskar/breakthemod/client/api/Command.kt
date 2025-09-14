/*
 * This file is part of breakthemodRewrite.
 *
 * breakthemodRewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemodRewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemodRewrite. If not, see <https://www.gnu.org/licenses/>.
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
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
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
    val fetch: Fetch = Fetch.getInstance()
    val client: MinecraftClient = MinecraftClient.getInstance()
    protected val scope = CommandScope.scope

    companion object {
        fun getConnectedServerAddress(): String? {
            val client = MinecraftClient.getInstance() ?: return null
            val serverInfo = client.currentServerEntry ?: return null
            return serverInfo.address.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }

        fun getEnabled(): Boolean {
            val serverAddress = getConnectedServerAddress() ?: return true
            if (serverAddress.lowercase(Locale.getDefault()).contains("earthmc.net")) return true
            return Config.getEnabledServers()
        }
    }


    var name: String = ""
    var description: String = ""
    var usageSuffix: String = ""

    fun getUsage(): String { return "/" + name + (if (usageSuffix.isEmpty()) "" else usageSuffix) }

    /**
     * @param ctx the command context.
     */
    protected open fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        return 0;
    }

    /**
     *
     * @param ctx The Command context derives off the command source provided by the loader implementation of the class.
     * @return 0 if success, 1 if error
     * @throws CommandSyntaxException If invalid syntax
     */
    @Throws(CommandSyntaxException::class)
    protected open fun run(ctx: CommandContext<FabricClientCommandSource>): Int {
        try {
            return execute(ctx)
        } catch (e: CommandSyntaxException) {
            throw e
        } catch (e: Exception) {
            MinecraftClient.getInstance().execute(Runnable {
                if (MinecraftClient.getInstance().player != null) {
                    sendMessage(MinecraftClient.getInstance(), Text.empty().append("Unexpected error: " + e.message))
                }
            })
            logError("Unexpected error has occurred while running $name", e)
            return 0
        }
    }

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
     * @param client  The minecraft client instance
     * @param message The message to be sent
     */
    fun sendMessage(client: MinecraftClient, message: Text) {
        client.execute {
            if (client.player != null) {
                val prefix: Text = Prefix().prefix
                val chatMessage: Text? = Text.literal("").append(prefix).append(message)
                client.player!!.sendMessage(chatMessage, false)
            }
        }
    }

    fun sendError() {
        sendMessage(client, Text.literal("Command has exited with an exception"))
    }

    protected fun logError(message: String?, e: java.lang.Exception) {
        logger.error("{}{}", message, e.message)
        if (Config.getDevMode()) {
            e.printStackTrace()
        }
    }

}
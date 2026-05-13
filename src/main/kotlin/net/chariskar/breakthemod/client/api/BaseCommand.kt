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
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

/**
 * Base for commands.
 * @property name The command name.
 * @property description The command description.
 * @property usageSuffix The args that must be passed to the commands in a readable format (e.g. <name>).
 *  */
abstract class BaseCommand : Base() {
    abstract val usageSuffix: String

    private val handler = CoroutineExceptionHandler { _, e ->
        sendError()
        if (Config.getDevMode()) {
            logError("Unexpected error occurred while running $name", e as Exception)
        }
    }

    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)

    fun getUsage(): String { return "/$name $usageSuffix" }

    /**
     * @param ctx the command context.
     */
    protected open fun execute(
        ctx: CommandContext<FabricClientCommandSource>
    ): Int = 0

    /**
     * Internal method, executes command code.
     * @param ctx The Command context.
     * @return 0 if success, 1 if error.
     * @throws CommandSyntaxException If invalid syntax.
     */
    @Throws(CommandSyntaxException::class)
    protected fun run(
        ctx: CommandContext<FabricClientCommandSource>
    ): Int {
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
                    if (!getEnabled()) { return@Command 0 }
                    return@Command run(
                        context
                    )
                })
        )
    }

    /**
     * Command register function.
     * @param T the type parameter for the command.
     * @param dispatcher Command registration dispatcher.
     * @param argName The name of the argument to register.
     * @param argType The type of the argument to register.
     * @param suggestions The suggestions the command provide.
     * */
    open fun <T> register(
        dispatcher: CommandDispatcher<FabricClientCommandSource>,
        argName: String,
        argType: ArgumentType<T>,
        suggestions: SuggestionProvider<FabricClientCommandSource?>? = null
    ) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(
                    RequiredArgumentBuilder.argument<FabricClientCommandSource?, T>(argName, argType)
                        .apply {
                            if (suggestions != null) suggests(suggestions)
                        }
                        .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                            if (!getEnabled()) return@Command 0
                            return@Command run(context)
                        })
                )
        )
    }

}
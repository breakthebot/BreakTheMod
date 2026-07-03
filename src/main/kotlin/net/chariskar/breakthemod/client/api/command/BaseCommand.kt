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

package net.chariskar.breakthemod.client.api.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.chariskar.breakthemod.client.api.providers.LoggingProvider
import net.chariskar.breakthemod.client.api.providers.MessageProvider
import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.Minecraft
import java.util.Locale
import java.util.concurrent.CompletableFuture

/**
 * Base for commands.
 * @param name The command name.
 * @param description The command description.
 * @param usageSuffix The args that must be passed to the commands in a readable format (e.g. `<name>` ).
 * @property scope The async scope that the commands should use.
 * @property client Centralized client access for commands.
 *  */
abstract class BaseCommand(
    val name: String,
    val description: String,
    val usageSuffix: String = ""
) : MessageProvider, ServerUtilsProvider, LoggingProvider {

    private val handler = CoroutineExceptionHandler { _, e ->
        sendError()
        if (Config.config.dev) {
            logError("Unexpected error occurred while running $name", e as Exception)
        }
    }

    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)

    protected val client: Minecraft = Minecraft.getInstance()

    fun getUsage() = "/$name $usageSuffix"

    fun getCommandDescription() = "$name $usageSuffix: $description"

    /**
     * @param ctx the command conComponent.
     */
    protected abstract fun execute(
        ctx: CommandContext<FabricClientCommandSource>
    ): Int

    /**
     * Internal method, executes command code.
     * @param ctx The Command conComponent.
     * @return 1 if success else 0.
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
            return 1
        }
    }

    /**
     * Command register function.
     * @param dispatcher Command registration dispatcher
     * */
    open fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name).executes(
                Command { conComponent: CommandContext<FabricClientCommandSource> ->
                    return@Command if (!isModEnabled()) 0 else run(conComponent)
                }
            )
        )
    }

    /**
     * Command register function.
     * @param T The type of the argument.
     * @param dispatcher Command registration dispatcher.
     * @param argName The name of the argument to register.
     * @param argType The type of the argument to register.
     * @param suggestions The suggestions the argument provides.
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
                    RequiredArgumentBuilder.argument<FabricClientCommandSource, T>(argName, argType)
                        .apply {
                            if (suggestions != null) suggests(suggestions)
                            executes(Command { conComponent: CommandContext<FabricClientCommandSource> ->
                                return@Command if (!isModEnabled()) 0 else run(conComponent)
                            })
                        }
                )
        )
    }

    /**
     * Provides command suggestions with whatever list is passed to it.
     * */
    class CommandSuggestions(
        val allSuggestions: List<String>
    ) : SuggestionProvider<FabricClientCommandSource?> {

        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            conComponent: CommandContext<FabricClientCommandSource?>?,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            val input = builder.remaining.lowercase(Locale.getDefault())

            allSuggestions
                .filter { s -> s.startsWith(input) }
                .forEach(builder::suggest)

            return builder.buildFuture()
        }
    }
}
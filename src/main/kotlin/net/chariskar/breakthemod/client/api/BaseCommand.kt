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
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.chariskar.breakthemod.client.api.providers.MessageProvider
import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.Minecraft
import org.breakthebot.breakthelibrary.models.APIResult
import java.util.Locale
import java.util.concurrent.CompletableFuture

private object CommandScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}

/**
 * Base for commands.
 * @param name The command name.
 * @param description The command description.
 * @param usageSuffix The args that must be passed to the commands in a readable format (e.g. `<name>` ).
 * @property scope The async scope that the commands should use.
 * */
abstract class BaseCommand(
    val name: String,
    val description: String,
    val usageSuffix: String = "",
) : MessageProvider,
    ServerUtilsProvider {

    protected val logger = KotlinLogging.logger("breakthemod")

    private val handler = CoroutineExceptionHandler { _, e ->
        sendError("Unexpected error occurred ${e.message} while running $name")
        if (Config.config.dev) {
            logger.error(e) { "Unexpected error occurred." }
        }
    }

    val scope = CoroutineScope(CommandScope.scope.coroutineContext + handler)

    fun getUsage() = "/$name $usageSuffix"

    fun getCommandDescription() = "$name $usageSuffix: $description"

    /**
     * The checks that should be run before the command is run (it shouldn't exit the command just return a message to display to the user.).
     * @param ctx The command source.
     * @return A pair consisting of a boolean if the prerequisites are met and the message if they aren't.
     * */
    protected open fun preRequisites(ctx: CommandContext<FabricClientCommandSource>): Pair<Boolean, String> = Pair(true, "")

    /**
     * @param ctx the command Context.
     */
    protected abstract fun execute(
        ctx: CommandContext<FabricClientCommandSource>,
    ): Int

    /**
     * Internal method, executes command code also runs the [preRequisites] check.
     * @param ctx The command context.
     * @return If the command exits successfully 0, if not 1.
     * @throws CommandSyntaxException If invalid syntax.
     */
    @Throws(CommandSyntaxException::class)
    protected fun run(
        ctx: CommandContext<FabricClientCommandSource>,
    ): Int {
        try {
            val preRequisites = preRequisites(ctx)
            if (!preRequisites.first) {
                sendError(preRequisites.second)
                return 1
            }
            return execute(ctx)
        } catch (e: CommandSyntaxException) {
            throw e
        } catch (e: Exception) {
            sendError()
            logger.error(e) { "Unexpected error occurred." }
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
                Command { context: CommandContext<FabricClientCommandSource> ->
                    return@Command if (!isModEnabled()) 0 else run(context)
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
        suggestions: SuggestionProvider<FabricClientCommandSource?>,
    ) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(
                    RequiredArgumentBuilder.argument<FabricClientCommandSource, T>(argName, argType)
                        .apply {
                            suggests(suggestions)
                            executes(
                                Command { context: CommandContext<FabricClientCommandSource> ->
                                    return@Command if (!isModEnabled()) 0 else run(context)
                                }
                            )
                        }
                )
        )
    }

    /**
     * Provides autocomplete suggestions with the provided [suggestionsProvider] function.
     * @param suggestionsProvider The function that generates the list of recommendations.
     */
    class CommandSuggestions(
        val suggestionsProvider: () -> Collection<String>,
    ) : SuggestionProvider<FabricClientCommandSource?> {

        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource?>,
            builder: SuggestionsBuilder,
        ): CompletableFuture<Suggestions> {
            val input = builder.remaining.lowercase(Locale.getDefault())

            suggestionsProvider()
                .filter { it.startsWith(input, ignoreCase = true) }
                .forEach(builder::suggest)

            return builder.buildFuture()
        }
    }

    fun <T> APIResult<T>.logError(): APIResult<T> = when (this) {
        is APIResult.Error -> {
            if (!this.isSevere) return this
            logger.error { "Received unexpected response from the api with status code ${this.statusCode} and message ${this.message}." }
            this
        }

        is APIResult.Success -> this
    }

    companion object {
        @JvmStatic
        protected val client: Minecraft
            get() = Minecraft.getInstance()
    }
}

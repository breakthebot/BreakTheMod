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

package net.chariskar.breakthemod.debug.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import java.util.Locale
import java.util.concurrent.CompletableFuture

object UnloadModule : BaseCommand(
    "unloadModule",
    "Unload any module."
) {

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(
                    RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("name", StringArgumentType.string())
                        .suggests(ModuleSuggestions())
                        .executes(Command { conComponent: CommandContext<FabricClientCommandSource> ->
                            if (!isModEnabled()) return@Command 0
                            return@Command run(conComponent)
                        })
                )
        )
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name = ctx.getArgument("name", String::class.java)

        val module = Breakthemod.modules.firstOrNull { it.name == name }

        if (module == null || !module.enabled) {
            sendMessage(Component.literal("Module $name has not been loaded."))
            return 0
        }

        val clazz = Class.forName(module.javaClass.name)

        val instance = clazz.getField("INSTANCE").get(null)
        val method = clazz.getMethod("disable")

        method.invoke(instance)

        return 0
    }

    class ModuleSuggestions : SuggestionProvider<FabricClientCommandSource?> {

        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            conComponent: CommandContext<FabricClientCommandSource?>?,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            val input = builder.remaining.lowercase(Locale.getDefault())

            Breakthemod.modules
                .filter { s -> s.name.startsWith(input) && s.enabled }
                .map { it.name }
                .forEach(builder::suggest)

            return builder.buildFuture()
        }
    }
}
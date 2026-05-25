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

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import java.util.Locale
import java.util.concurrent.CompletableFuture

object LoadModule : BaseCommand(
    "loadModule",
    ""
) {

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register<String>(
            dispatcher,
            "name",
            StringArgumentType.string(),
            ModuleSuggestions()
        )
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name = ctx.getArgument("name", String::class.java)

        val module = Breakthemod.modules.firstOrNull { it.name == name }

        if (module?.enabled == true) {
            sendMessage(Text.literal("Module $name already enabled."))
            return 0
        }

        val clazz = Class.forName("net.chariskar.breakthemod.client.modules.${module?.javaClass?.name}")

        val instance = clazz.getField("INSTANCE").get(null)
        val method = clazz.getMethod("launch")

        method.invoke(instance)

        return 0
    }

    class ModuleSuggestions : SuggestionProvider<FabricClientCommandSource?> {

        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource?>?,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            val input = builder.remaining.lowercase(Locale.getDefault())

            Breakthemod.modules
                .filter { s -> s.name.startsWith(input) && !s.enabled }
                .map { it.name }
                .forEach(builder::suggest)

            return builder.buildFuture()
        }
    }

}
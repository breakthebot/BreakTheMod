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

package net.chariskar.breakthemod.client.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.Locale
import java.util.concurrent.CompletableFuture

object Calculate : BaseCommand() {

    override val name = "calculate"
    override val description = "Helps with the conversions of blocks & stacks."
    override val usageSuffix = "<type> <amount>"

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val type = ctx.getArgument("type", String::class.java)
        val amount = ctx.getArgument("amount", Int::class.java)
        return when ( type ) {
            "blocks" -> {
                val fullBlocks = amount / 9
                val remainder = amount % 9
                sendMessage("$amount gold ingots equal $fullBlocks blocks and $remainder ingots.")
                0
            }
            "stacks" -> {
                val fullStacks = amount / 64
                val remainder = amount % 64
                sendMessage("$amount blocks are $fullStacks stacks and $remainder blocks")
                0
            }
            else -> {
                sendMessage(Text.literal("$type is not blocks or stacks"), Formatting.RED)
                1
            }
        }
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("type", StringArgumentType.string())
                    .suggests(CommandSuggestions(mutableListOf("blocks", "stacks")))
                    .then(
                        RequiredArgumentBuilder.argument<FabricClientCommandSource?, Int>("amount", IntegerArgumentType.integer())
                            .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                                if (!getEnabled()) return@Command 0
                                return@Command run(context)
                            })
                    )

                )
        )
    }
}

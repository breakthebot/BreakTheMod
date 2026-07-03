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
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor

object Calculate : BaseCommand(
    "calculate",
    "Helps with the conversions of blocks & stacks.",
    "<type> <amount>"
) {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val type = ctx.getArgument("type", String::class.java)
        val amount = ctx.getArgument("amount", Int::class.java)
        return when ( type ) {
            "blocks" -> {
                val fullBlocks = amount / 9
                val remainder = amount % 9
                sendMessage("$amount gold ingots equal $fullBlocks blocks and $remainder ingots.")
                1
            }
            "stacks" -> {
                val fullStacks = amount / 64
                val remainder = amount % 64
                sendMessage("$amount blocks are $fullStacks stacks and $remainder blocks")
                1
            }
            else -> {
                sendMessage(Component.literal("$type is not blocks or stacks"), TextColor.RED)
                0
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
                            .executes(Command { conComponent: CommandContext<FabricClientCommandSource> ->
                                if (!isModEnabled()) return@Command 0
                                return@Command run(conComponent)
                            })
                    )

                )
        )
    }
}

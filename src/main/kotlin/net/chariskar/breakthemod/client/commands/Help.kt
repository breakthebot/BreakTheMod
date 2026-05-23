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
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object Help : BaseCommand() {
    override val name = "commands"
    override val description = "This very command."
    override val usageSuffix = "[name]"

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val commands = Breakthemod.commands
        val modules = Breakthemod.modules

        if (commands.isEmpty()) {
            sendError("No commands available!.")
            return 1
        }

        sendMessage(Text.literal("=== Available Commands ==="), Formatting.GOLD)

        for (cmd in commands) {
            sendMessage(
                Text.literal(
                    cmd.getUsage()
                ).setStyle(
                    Style.EMPTY
                    .withHoverEvent(
                        HoverEvent.ShowText(
                            Text.literal(cmd.getCommandDescription())
                        )
                    )
                ),
                Formatting.GRAY
            )
        }

        if (modules.isEmpty()) {
            sendMessage(Text.literal("No features available."))
            return 1
        }

        sendMessage(Text.literal("=== Available Features ==="), Formatting.GOLD)

        for (module in modules) {
            if (module.hidden) continue
            sendMessage(
                Text.literal(
                    module.getModuleDescription()
                ),
                Formatting.GRAY
            )
        }

        return 0
    }


    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("name", StringArgumentType.string())
                .suggests(CommandSuggestions(Breakthemod.commands.map { it.name }.toMutableList()))
                .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                    val name = context.getArgument("name", String::class.java)
                    val command = Breakthemod.commands.firstOrNull {
                        it.name == name
                    }
                    if (command == null) {
                        sendError("$name is not a recognised command.")
                        return@Command 0
                    }
                    sendMessage(command.getCommandDescription())
                    return@Command 1
                }))
                .executes(Command { ctx: CommandContext<FabricClientCommandSource> ->
                    return@Command execute(ctx)
                })
        )
    }
}
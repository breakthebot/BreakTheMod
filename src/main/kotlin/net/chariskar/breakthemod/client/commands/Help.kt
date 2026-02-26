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

import com.mojang.brigadier.context.CommandContext
import net.chariskar.breakthemod.client.api.BaseCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting


class Help : BaseCommand() {
    var commands: MutableList<BaseCommand>? = null

    init {
        name = "commands"
        description = "The help command"
        usageSuffix = ""
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        if (commands == null || commands!!.isEmpty()) {
            sendMessage(Text.literal("No commands available.").setStyle(Style.EMPTY.withColor(Formatting.RED)))
            return 1
        }

        sendMessage(Text.literal("=== Available Commands ===").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))

        for (cmd in commands) {
            var cmdNameText: MutableText = Text.literal("/" + cmd.name)
                .formatted(Formatting.GRAY)

            if (!cmd.usageSuffix.isEmpty()) {
                cmdNameText = cmdNameText.append(
                    Text.literal(" " + cmd.usageSuffix)
                )
            }

            val descText: Text? = Text.literal(" - " + cmd.description)
                .formatted(Formatting.WHITE)

            sendMessage(cmdNameText.append(descText))
        }
        return 0;
    }
}
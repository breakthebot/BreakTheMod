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
import net.chariskar.breakthemod.client.api.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class Help : BaseCommand() {
    var commands: List<BaseCommand>? = null
    var modules: List<Module>? = null

    init {
        name = "commands"
        description = "This very command."
        usageSuffix = ""
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        if (commands == null || commands!!.isEmpty()) {
            sendError("No commands available!.")
            return 1
        }

        sendMessage(Text.literal("=== Available Commands ==="), Formatting.GOLD)

        for (cmd in commands) {
            sendMessage(
                Text.literal(
                    cmd.getUsage()
                ),
                Formatting.GRAY
            )
        }

        if (modules == null || modules!!.isEmpty()) {
            sendMessage(Text.literal("No features available."))
            return 1
        }

        sendMessage(Text.literal("=== Available Features ==="), Formatting.GOLD)

        for (module in modules) {
            sendMessage(
                Text.literal(
                    module.getModuleDescription()
                ),
                Formatting.GRAY
            )
        }

        return 0
    }
}
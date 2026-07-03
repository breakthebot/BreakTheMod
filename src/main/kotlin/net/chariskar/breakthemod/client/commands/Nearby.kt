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
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.modules.NearbyEngine
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

object Nearby : BaseCommand(
    "nearby",
    "Shows all of the nearby players (legal)."
) {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val players = NearbyEngine.players

        val header = if (players.isEmpty()) {
            Component.literal("No players nearby").setStyle(
                Style.EMPTY.withColor(
                    TextColor.RED
                )
            )
        } else {
            Component.literal("Players nearby:\n").setStyle(
                Style.EMPTY.withColor(
                    TextColor.YELLOW
                )
            )
        }
        val playerComponent = Component.empty().setStyle(
            Style.EMPTY.withColor(TextColor.AQUA)
        )
        for (player in players) {
            playerComponent.append(player.toString() + "\n")
        }
        header.append(playerComponent)

        sendMessage(header)
        return 0
    }
}
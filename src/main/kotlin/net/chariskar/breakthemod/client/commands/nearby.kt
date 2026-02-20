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
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.api.engine.NearbyEngine
import net.chariskar.breakthemod.client.api.engine.PlayerInfo
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting


class nearby : BaseCommand() {

    init {
        name = "nearby"
        description = "Shows nearby people"

    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        scope.launch {
            val players: Set<PlayerInfo> = NearbyEngine.updateNearbyPlayers(client.player!!, client.world!!)

            var header: MutableText

            if (players.isEmpty()) {
                header = Text.literal("No players nearby").setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.RED)))
            } else {
                header = Text.literal("Players nearby:\n").setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.YELLOW)))
                val playerText: MutableText = Text.empty().setStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.AQUA)))

                for (player in players) {
                    playerText.append(player.toString() + "\n")
                }
                header.append(playerText)
            }

            sendMessage(header)
        }
        return 0
    }
}
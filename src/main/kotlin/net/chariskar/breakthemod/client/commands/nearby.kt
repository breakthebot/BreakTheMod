package net.chariskar.breakthemod.client.commands

import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.api.engine.NearbyEngine
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting


class nearby : Command() {

    init {
        name = "nearby"
        description = "Shows nearby people"

    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        scope.launch {
            val players: Set<NearbyEngine.PlayerInfo> = NearbyEngine.updateNearbyPlayers(client.player!!, client.world!!)

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
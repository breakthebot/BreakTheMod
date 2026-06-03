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
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.modules.Cache
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.getOrNull

object Townless : BaseCommand(
    "townless",
    "Shows all the online townless players"
) {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val onlinePlayers = client.networkHandler!!.playerUuids.toList()
        scope.launch {
            if (onlinePlayers.size <= 1) {
                sendMessage("No online players found.")
                return@launch
            }

            val own = TownyAPI.getPlayer(Breakthemod.username).getOrNull()
            val townName = own?.town?.name

            if (townName.isNullOrEmpty()) {
                sendMessage("You have to be in a town to access this command.")
                return@launch
            }

            Cache.updateCache()

            val townless = Cache.playerCache.values.filter { it.town == null }

            if (townless.isEmpty()) {
                sendMessage("No townless players found")
                return@launch
            }

            val message = Text.literal("Townless Users:\n")
                .setStyle(Style.EMPTY.withColor(Formatting.AQUA))

            for (user in townless) {
                val inviteMessage = "/msg $user " + Config.getTownlessMessage(townName)
                val userText = Text.literal(user.name).styled {
                    Style.EMPTY
                        .withColor(Formatting.AQUA)
                        .withClickEvent(ClickEvent.CopyToClipboard(inviteMessage))
                        .withHoverEvent(
                            HoverEvent.ShowText(
                                Text.literal("Click to copy message to clipboard.")
                            )
                        )
                }
                message.append(userText).append(Text.literal("\n"))
            }
            sendMessage(message)

        }

        return 0
    }


}
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
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import org.breakthebot.breakthelibrary.api.TownyAPI

object Townless : BaseCommand(
    "townless",
    "Shows all the online townless players"
) {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val onlinePlayers = client.connection?.onlinePlayers?.mapNotNull { it.profile.id.toString() }
        if (onlinePlayers.isNullOrEmpty()) {
            sendMessage("There are no online players.")
            return 0
        }
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

            val message = Component.literal("Townless Users:\n")
                .setStyle(Style.EMPTY.withColor(TextColor.AQUA))

            for (user in townless) {
                val inviteMessage = "/msg $user " + Config.getTownlessMessage(townName)
                val userComponent = Component.literal(user.name).setStyle(
                    Style.EMPTY
                        .withColor(TextColor.AQUA)
                        .withClickEvent(ClickEvent.CopyToClipboard(inviteMessage))
                        .withHoverEvent(
                            HoverEvent.ShowText(
                                Component.literal("Click to copy message to clipboard.")
                            )
                        )
                )

                message.append(userComponent).append(Component.literal("\n"))
            }
            sendMessage(message)

        }

        return 0
    }


}
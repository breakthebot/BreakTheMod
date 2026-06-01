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
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.getOrNull
import org.breakthebot.breakthelibrary.models.onError

object OnlineFriends : BaseCommand(
    "onlineFriends",
    "Displays your online friends",
) {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        scope.launch {
            val player = TownyAPI.getPlayer(Breakthemod.username).onError {
                sendError("Unable to reach the EarthMc API at this time.")
                return@launch
            }.getOrNull()

            val playerFriends = player?.friends?.mapNotNull { it.uuid }

            if (playerFriends?.isEmpty() == true) {
                sendError("You do not have any friends.")
                return@launch
            }

            val onlineFriends = playerFriends?.mapNotNull { uuid ->
                client.networkHandler!!.playerList.firstOrNull {
                    it.profile.id == uuid.toUUID()
                }?.profile?.name
            }

            if (onlineFriends.isNullOrEmpty()) {
                sendError("You have no online friends.")
                return@launch
            }

            var onlineFriendsText = Text.empty()

            for (i in onlineFriends.indices) {
                onlineFriendsText = onlineFriendsText.append(
                    Text.literal(onlineFriends[i]).setStyle(Style.EMPTY.withColor(Formatting.AQUA))
                )

                if (i < onlineFriends.size - 1) {
                    onlineFriendsText = onlineFriendsText.append(
                        Text.literal(", ").setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                    )
                }
            }


            val text = Text.empty().apply {
                append(onlineFriendsText)
                append(Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                append(
                    Text.literal(onlineFriends.size.toString())
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                )
                append(Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
            }

            sendMessage(text)
        }
        return 0
    }
}
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
import net.chariskar.breakthemod.client.api.Command
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlinx.coroutines.*
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class onlineFriends : Command() {
    init {
        name = "onlineFriends"
        description = "Shows the online friends of the user"
        usageSuffix = ""
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        scope.launch {
            val player =  fetch.getResident(client.player!!.gameProfile.name)

            var friendList = player!!.friends!!.map { p-> p.name }

            if (friendList.isEmpty()) {
                sendMessage(client, Text.literal("Selected user does not have any friends"))
                return@launch
            }

            val onlinePlayer = client.networkHandler!!.playerList.map { p-> p.profile.name }

            friendList = friendList.filter { p-> onlinePlayer.contains(p) }

            if (friendList.isEmpty()) {
                sendMessage(client, Text.literal("You do not have any online friends right now"))
                return@launch
            }

            val onlineFriendsText: MutableText = Text.empty()

            for (i in 0..<friendList.size) {
                onlineFriendsText.append(
                    Text.literal(friendList.get(i)).setStyle(Style.EMPTY.withColor(Formatting.AQUA))
                )

                if (i < friendList.size - 1) {
                    onlineFriendsText.append(
                        Text.literal(", ").setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                    )
                }
            }

            val message: Text = Text.literal("")
                .append(onlineFriendsText)
                .append(Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                .append(
                    Text.literal(java.lang.String.valueOf(friendList.size))
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                )
                .append(Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))

            sendMessage(client, message)
        }

        return 0
    }
}
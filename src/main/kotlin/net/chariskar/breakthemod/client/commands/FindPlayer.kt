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

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import org.breakthebot.breakthelibrary.api.MapAPI
import org.breakthebot.breakthelibrary.models.PlayerLocationInfo

object FindPlayer : BaseCommand(
    "findPlayer",
    "Show where a player is based on the map api.",
    "<name>"
) {
    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        scope.launch {
            val players = MapAPI.getVisiblePlayers()

            if (players.isNullOrEmpty()) {
                sendError("No visible players at this moment.")
                return@launch
            }

            val playerData = PlayerLocationInfo(name, 0.0, 0.0, false, null, false)

            for (player in players) {
                if (player.name.equals(name, ignoreCase = true)) {
                    playerData.found = true
                    playerData.x = player.x
                    playerData.z = player.z

                    val locationData = MapAPI.getLocation(listOf(Pair(player.x,player.z))).getOrNull()?.first()

                    if (locationData != null && !locationData.isWilderness) {
                        playerData.townName = locationData.town?.name
                    }

                    sendMessage(Component.literal(playerData.toString()), TextColor.AQUA)
                    return@launch
                }
                continue
            }
        }
        return 0
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register<String>(
            dispatcher,
            "name",
            StringArgumentType.string(),
            CommandSuggestions(
                Breakthemod.onlinePlayers
            )
        )
    }
}
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

package net.chariskar.breakthemod.client.commands.alliance

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.modules.Cache
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import org.breakthebot.breakthelibrary.api.TownyAPI
import java.net.URI

object Alliance : BaseCommand("alliance", "Displays the data for an alliance.", "<name>") {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name = ctx.getArgument("name", String::class.java)
        scope.launch {
            val alliance = TownyAPI.getAlliance(name)
                .onError {
                    if (it.statusCode == 404) {
                        sendError("No alliance with name $name found.")
                        return@launch
                    }
                    sendMessage("Received an unexpected response from the alliance api.")
                }
                .getOrNull()!!

            val text = Component.empty()

            text.append("Name: ${alliance.name} (${alliance.shortName})\n")
            text.append("Type: ${alliance.type} ")

            if (alliance.discord.isNotBlank()) {
                text.append(
                    Component.literal("\ndiscord")
                        .withColor(TextColor.BLUE)
                        .withStyle(Style.EMPTY.withClickEvent(ClickEvent.OpenUrl(URI(alliance.discord))))
                )
            }

            sendMessage(text)
        }
        return 0
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register<String>(
            dispatcher,
            "name",
            StringArgumentType.greedyString(),
            CommandSuggestions { Cache.alliances }
        )
    }
}

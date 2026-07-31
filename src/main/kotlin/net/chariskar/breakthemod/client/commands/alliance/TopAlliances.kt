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
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.AllianceFilter

object TopAlliances : BaseCommand("topalliances", "Displays the top alliances in the select categories..", "<filter>") {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val filter = ctx.getArgument("filter", String::class.java)
        scope.launch {
            val alliance = TownyAPI.getTopAlliances(AllianceFilter.valueOf(filter))
                .onError {
                    if (it.statusCode == 404) {
                        sendError("No alliance with name $name found.")
                        return@launch
                    }
                    sendMessage("Received an unexpected response from the alliance api.")
                }
                .getOrNull()!!
                .map { it.identifier.name }
                .take(5)

            val allianceText = alliance.toString().replace(",", "\n")

            sendMessage(allianceText)
        }
        return 0
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register<String>(
            dispatcher,
            "filter",
            StringArgumentType.string(),
            CommandSuggestions(AllianceFilter.entries.map { it.toString() })
        )
    }
}

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
import org.breakthebot.breakthelibrary.api.TownyAPI

object NationMembership : BaseCommand("nationMembership", "Displays all of the alliances a nation is in.", "<name>") {
    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name = ctx.getArgument("name", String::class.java)
        scope.launch {
            val membership = TownyAPI.getNationMembership(name)
                .onError { error(it) }
                .onSuccess { println(it) }
                .getOrNull()

            if (membership == null) {
                sendMessage("Got an empty response from the alliance api.")
                return@launch
            }

            val alliances = membership.map { it.name }

            sendMessage("$name is in ${alliances.joinToString(", ")}.")
        }
        return 1
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register(
            dispatcher,
            "name",
            StringArgumentType.greedyString(),
            CommandSuggestions { Cache.nationNameCache }
        )
    }
}

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
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.modules.Cache
import org.breakthebot.breakthelibrary.api.MapAPI
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.ApiResult
import org.breakthebot.breakthelibrary.models.Nation
import org.breakthebot.breakthelibrary.models.NearbyItem
import org.breakthebot.breakthelibrary.models.NearbyType
import org.breakthebot.breakthelibrary.models.Town

object GotoCommand : BaseCommand(
    "goto",
    "Shows you the nearest spawnable town of the town you selected.",
    "<name>"
) {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val townName = ctx.getArgument("name", String::class.java)

        scope.launch {

            val reqTown =  TownyAPI.getTown(townName).getOrElse {
                val message = when (it.statusCode) {
                    404 -> "$townName does not exist."
                    else -> "API returned unexpected message ${it.message}"
                }
                sendError(message)
                return@launch
            }

            if (
                reqTown.status.isPublic && reqTown.status.canOutsidersSpawn
            ) {
                sendMessage(Text.literal("You can do /t spawn ${reqTown.name}"), Formatting.AQUA)
                return@launch
            }

            if (reqTown.status.isCapital) {
                val nation = TownyAPI.getNation(reqTown.nation?.name!!).getOrElse {
                    val message = when (it.statusCode) {
                        503 -> "EarthMc API is unavailable."
                        else -> "API says this town is the capital of a nation that does not exist."
                    }
                    sendError(message)
                    return@launch
                }

                if (nation.status?.isPublic == true) {
                    sendMessage(Text.literal("Found suitable spawn in:\n ${reqTown.name} (${nation.name})"), Formatting.AQUA)
                    return@launch
                }
            }

            var radius = 500
            var attempts = 3
            val validTowns: MutableList<String> = mutableListOf()

            loop@ while (attempts-- > 0) {
                val resp = MapAPI.getNearby(NearbyItem.NearbyItemString(
                    target = townName,
                    searchType = NearbyType.TOWN,
                    targetType = NearbyType.TOWN,
                    radius = radius
                )
                ).onError { e ->
                    val message = when (e.statusCode) {
                        404 -> "$townName does not exist."
                        else -> "Api returned unexpected message ${e.message}"
                    }
                    sendError(message)
                    return@launch
                }.mapSuccess { it?.map { name } }.getOrNull()

                if (resp.isNullOrEmpty()) {
                    radius += 500
                    continue
                }

                // Non-null assertion cause being null would be an error by design and handled by onError
                val townDetails = TownyAPI.getTowns(resp).first()
                    .onError {
                    radius += 500
                    continue@loop
                }.getOrNull()!!

                for (town in townDetails) {
                    val status = town.status

                    if (status.isPublic && status.canOutsidersSpawn) {
                        validTowns.add(town.name)
                    } else if (status.isCapital) {
                        val nation = TownyAPI.getNation(town.nation?.name!!).getOrNull()
                        if (nation?.status?.isPublic == true) {
                            validTowns.add(town.name)
                        }
                    }
                }

                if (validTowns.isEmpty()) {
                    radius += 500
                    continue
                }

                val townText: MutableText = Text.empty()
                validTowns.forEach { t -> townText.append(Text.literal("$t\n")) }
                sendMessage(Text.literal("Found suitable spawn in:\n").append(townText), Formatting.AQUA)
                return@launch
            }

            sendError("Found no suitable spawn near $townName.")
        }

        return 1
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register<String>(dispatcher, "name", StringArgumentType.string(), CommandSuggestions(Cache.townCache))
    }
}

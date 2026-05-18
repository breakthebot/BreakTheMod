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
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

import net.chariskar.breakthemod.client.api.BaseCommand
import org.breakthebot.breakthelibrary.api.MapApi
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.Nation
import org.breakthebot.breakthelibrary.models.NearbyItem
import org.breakthebot.breakthelibrary.models.NearbyType
import org.breakthebot.breakthelibrary.models.Reference
import org.breakthebot.breakthelibrary.models.Town
import org.breakthebot.breakthelibrary.network.ApiResult
import org.breakthebot.breakthelibrary.network.getOrNull

object goto : BaseCommand() {
    
    override val name: String = "goto"
    override val usageSuffix: String = "Shows you the nearest spawnable town of the town you selected."
    override val description: String = ""

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val townName = ctx.getArgument("name", String::class.java)

        scope.launch {
            val reqTown = when (
                val reqTown = TownyAPI.getTown(townName)
            ) {
                is ApiResult.Success<Town> -> reqTown.data
                is ApiResult.Error -> {
                    val message = when (reqTown.statusCode) {
                        404 -> "$townName does not exist."
                        else -> "Api returned unexpected message ${reqTown.message}"
                    }
                    sendError(message)
                    return@launch
                }
            }

            if (
                reqTown.status?.isPublic == true &&
                reqTown.status?.canOutsidersSpawn == true
            ) {
                sendMessage(Text.literal("You can do /t spawn ${reqTown.name}"), Formatting.AQUA)
                return@launch
            }

            if (reqTown.status?.isCapital == true) {
                val nation = when (
                    val nation = TownyAPI.getNation(reqTown.nation?.name!!)
                ) {
                    is ApiResult.Success<Nation> -> nation.data
                    is ApiResult.Error -> {
                        val message = when (nation.statusCode) {
                            503 -> "Earthmc APIw is unavailable."
                            else -> "API says this town is the capital of a nation that does not exist."
                        }
                        sendError(message)
                        return@launch
                    }
                }

                if (nation.status?.isPublic == true) {
                    sendMessage(Text.literal("Found suitable spawn in:\n ${reqTown.name} (${nation.name})"), Formatting.AQUA)
                    return@launch
                }
            }

            var radius = 500
            var attempts = 3
            val validTowns: MutableList<String> = mutableListOf()

            while (attempts-- > 0) {
                val resp = when (
                    val resp = MapApi.getNearby(listOf(NearbyItem(
                        NearbyType.TOWN,
                        townName,
                        NearbyType.TOWN,
                        radius)
                    ))
                ) {
                    is ApiResult.Success<List<List<Reference>?>?> -> { resp.data }
                    is ApiResult.Error -> {
                        val message = when (resp.statusCode) {
                            404 -> "$townName does not exist."
                            else -> "Api returned unexpected message ${resp.message}"
                        }
                        sendError(message)
                        return@launch
                    }
                }?.first()

                val names = resp?.mapNotNull { it.name } ?: emptyList()
                if (names.isEmpty()) {
                    radius += 500
                    continue
                }

                val townDetails = when (val townDetails = TownyAPI.getTowns(names).first()) {
                    is ApiResult.Success<List<Town>> -> {
                        townDetails.data
                    }
                    else -> {
                        radius += 500
                        continue
                    }
                }

                for (town in townDetails) {
                    val status = town.status

                    if (status?.isPublic == true && status.canOutsidersSpawn == true) {
                        validTowns.add(town.name)
                    } else if (status?.isCapital == true) {
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

        return 0
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register<String>(dispatcher, "name", StringArgumentType.string(), null)
    }
}

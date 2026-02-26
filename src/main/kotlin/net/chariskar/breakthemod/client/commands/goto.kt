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

import com.mojang.brigadier.Command as command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.breakthebot.breakthelibrary.api.NationAPI
import org.breakthebot.breakthelibrary.api.NearbyAPI
import org.breakthebot.breakthelibrary.api.TownAPI
import org.breakthebot.breakthelibrary.models.NearbyItem
import org.breakthebot.breakthelibrary.models.NearbyType

class goto: BaseCommand() {
    init {
        name = "goto"
        description = "Shows you the nearest spawnable town of the town you selected."
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val townName = ctx.getArgument("name", String::class.java)

        scope.launch {
            try {
                val reqTown = TownAPI.getTown(townName)

                if (reqTown == null) {
                    sendMessage(Text.literal("$townName isn't a real town."), Formatting.RED)
                    return@launch
                }

                if (reqTown.status?.isPublic == true && reqTown.status?.canOutsidersSpawn == true) {
                    sendMessage(Text.literal("You can do /t spawn ${reqTown.name}"), Formatting.AQUA)
                    return@launch
                }

                if (reqTown.status?.isCapital == true) {
                    val nation = NationAPI.getNation(reqTown.nation?.name!!)

                    if (nation?.status?.isPublic == true) {
                        sendMessage(Text.literal("Found suitable spawn in:\n ${reqTown.name} (${nation.name})"), Formatting.AQUA)
                        return@launch
                    }
                }

                var radius = 500
                var attempts = 3
                val validTowns: MutableList<String> = mutableListOf()

                while (attempts-- > 0) {

                    val resp = NearbyAPI.get(NearbyItem(
                        NearbyType.TOWN,
                        townName,
                        NearbyType.TOWN,
                        radius
                    ))

                    val names: List<String> = resp?.mapNotNull { it.name } ?: emptyList()
                    if (names.isEmpty()) {
                        radius += 500
                        continue
                    }

                    val townDetails = TownAPI.getTowns(names)

                    if (townDetails.isNullOrEmpty()) {
                        radius += 500
                        continue
                    }

                    for (town in townDetails) {
                        val status = town.status
                        if (status?.isPublic == true && status.canOutsidersSpawn == true) {
                            validTowns.add(town.name)
                        } else if (status?.isCapital == true) {
                            val nation = NationAPI.getNation(town.nation?.name!!)
                            if (nation?.status?.isPublic == true) {
                                validTowns.add(town.name)
                            }
                        }
                    }

                    if (validTowns.isNotEmpty()) {
                        val townText: MutableText = Text.empty()
                        validTowns.forEach { t -> townText.append(Text.literal("$t\n")) }

                        sendMessage(Text.literal("Found suitable spawn in:\n").append(townText), Formatting.AQUA)
                        return@launch
                    }

                    radius += 500
                }

                sendMessage(Text.literal("No suitable spawns found near $townName."), Formatting.RED)

            } catch (e: Exception) {
                logger.error("Unexpected error occurred while fetching location", e)
                sendMessage(Text.literal("Unexpected error occurred. See logs."), Formatting.RED)
            }
        }

        return 1
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(
                    RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("name", StringArgumentType.string())
                        .executes(command { context: CommandContext<FabricClientCommandSource> ->
                            if (!getEnabled()) return@command 0
                            return@command run(context)
                        })
                )
        )
    }
}

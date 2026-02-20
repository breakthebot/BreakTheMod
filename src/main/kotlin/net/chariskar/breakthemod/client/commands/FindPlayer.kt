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

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.breakthebot.breakthelibrary.api.LocationAPI
import org.breakthebot.breakthelibrary.models.PlayerLocationInfo
import java.util.Locale
import java.util.concurrent.CompletableFuture

class FindPlayer : BaseCommand() {
    init {
        name = "findPlayer"
        description = "Tells you where a player is based on the map api."
        usageSuffix = "<name>"
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        scope.launch {
            val players = LocationAPI.getVisiblePlayers()

            if (players.isNullOrEmpty()) {
                sendMessage(Text.literal("Received empty player list from map."), Formatting.RED)
                return@launch
            }
            val playerData = PlayerLocationInfo(name, 0.0, 0.0, false, null, false)

            for (player in players) {
                if (player.name.equals(name, ignoreCase = true)) {
                    playerData.found = true
                    playerData.x = player.x
                    playerData.z = player.z

                    val locationData = LocationAPI.getLocation(listOf(
                        Pair(player.x,player.z)
                    ))?.first()

                    if (locationData != null && locationData.isWilderness == false) {
                        playerData.townName = locationData.town?.name
                    }
                    sendMessage(Text.literal(playerData.toString()), Formatting.AQUA)
                    return@launch

                }
                continue
            }
        }
        return 0
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(
                    RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("name", StringArgumentType.string())
                        .suggests(FindPlayerSuggestion())
                        .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                            if (!getEnabled()) return@Command 0
                            return@Command run(context)
                        })
                )
        )
    }

    class FindPlayerSuggestion : SuggestionProvider<FabricClientCommandSource?> {

        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource?>?,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            val input = builder.remaining.lowercase(Locale.getDefault())

            val players = MinecraftClient.getInstance().world?.players?.map { it.gameProfile.name }
            players?.stream()
                ?.filter { s -> s?.startsWith(input) == true }
                ?.forEach(builder::suggest)

            return builder.buildFuture()
        }
    }
}
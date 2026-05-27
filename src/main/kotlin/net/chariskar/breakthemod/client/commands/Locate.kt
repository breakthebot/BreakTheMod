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
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.modules.Cache
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.getOrNull
import java.net.URI
import java.util.Locale
import java.util.concurrent.CompletableFuture

object Locate : BaseCommand(
    "locate",
    "Gives tou the coordinates of a town/nation",
    "<type> <name>"
) {
    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val type: String = ctx.getArgument("type", String::class.java)
        val name: String = ctx.getArgument("name", String::class.java)

        scope.launch {
            val coords = when (type) {
                "town" -> {
                    val town = TownyAPI.getTown(name).getOrNull()
                    town?.coordinates?.spawn
                }
                "nation" -> {
                    val nation = TownyAPI.getNation(name).getOrNull()
                    nation?.coordinates?.spawn
                }
                else -> {
                    sendError("$type is not town or nation")
                    return@launch
                }
            }
            if (coords == null) {
                sendError("No $type named $name found.")
                return@launch
            }
            sendMessage(
                Text.literal("$name is located ").append(getMapText(coords.x!!,coords.z!!)).append(" (x: ${coords.x?.toInt()}, z: ${coords.z?.toInt()})")
            )
        }
        return 0
    }

    private fun getMapText(x: Float, z: Float): Text {
        val mapUrl = "${Config.getMapUrl()}?world=minecraft_overworld&zoom=5&x=$x&z=$z"
        return Text.literal("here").styled {
            Style.EMPTY
                .withColor(Formatting.BLUE)
                .withClickEvent (
                    ClickEvent.OpenUrl(URI.create(mapUrl))
                )
        }
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(
                    RequiredArgumentBuilder.argument<FabricClientCommandSource, String>(
                        "type",
                        StringArgumentType.word()
                    )
                        .suggests { _, builder ->
                            builder.suggest("town")
                            builder.suggest("nation")
                            builder.buildFuture()
                        }
                        .then(
                            RequiredArgumentBuilder.argument<FabricClientCommandSource, String>(
                                "name",
                                StringArgumentType.string()
                            )
                                .suggests(NameSuggestions())
                                .executes { context ->
                                    if (!isModEnabled()) return@executes 0
                                    run(context)
                                }
                        )
                )
        )
    }


    class NameSuggestions : SuggestionProvider<FabricClientCommandSource> {

        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource>,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {

            val type = StringArgumentType.getString(context, "type")

            val input = builder.remaining.lowercase(Locale.getDefault())

            val allSuggestions = when (type) {
                "town" -> Cache.townCache
                "nation" -> Cache.nationCache
                else -> emptyList()
            }

            allSuggestions
                .filter { it.lowercase(Locale.getDefault()).startsWith(input) }
                .forEach(builder::suggest)

            return builder.buildFuture()
        }
    }
}
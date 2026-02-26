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
import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.breakthebot.breakthelibrary.api.NationAPI
import org.breakthebot.breakthelibrary.api.TownAPI
import java.net.URI
import java.util.*
import java.util.concurrent.CompletableFuture
import com.mojang.brigadier.Command as command


class Locate : BaseCommand() {

    init {
        name = "locate"
        description = "Gives you the coordinates of a town/nation."
        usageSuffix = "<type> <name>"
    }
    /**
     * trying a more object-oriented style.
     * */
    data class LocationRequest(
        val type: String,
        val name: String
    ) {
        suspend fun get(): Pair<Float, Float>? {
            when (type) {
                "town" ->  {
                    val town = TownAPI.getTown(name) ?: return null
                    return Pair(town.coordinates?.spawn?.x!!, town.coordinates!!.spawn!!.z!!)
                }
                "nation" -> {
                    val nation = NationAPI.getNation(name) ?: return null
                    return Pair(nation.coordinates?.spawn?.x!!, nation.coordinates?.spawn?.z!!)
                }
            }
            return null
        }
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val type: String = ctx.getArgument("type", String::class.java)

        val name: String = ctx.getArgument("name", String::class.java)

        scope.launch {
            val coords = LocationRequest(type, name).get()
            if (coords == null) {sendMessage(Text.literal("$name isn't a town or a nation"), Formatting.RED); return@launch}

            val link = Text.literal("click here") .setStyle(
                Style.EMPTY
                    .withColor(Formatting.BLUE)
                    .withClickEvent(
                        ClickEvent.OpenUrl(URI("${Config.getMapUrl()}?world=minecraft_overworld&zoom=3&x=${coords.first}&z=${coords.second}"))
                    )

            )

            sendMessage(Text.literal("$name is located at x: ${coords.first}, z: ${coords.second} ").append(link))
        }

        return 0
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("type", StringArgumentType.string())
                    .suggests(LocateSuggestion())
                    .then(
                            RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("name", StringArgumentType.string())
                                .executes(command { context: CommandContext<FabricClientCommandSource> ->
                                    if (!getEnabled()) return@command 0
                                    return@command run(context)
                                })
                        )

                )
        )
    }

    class LocateSuggestion : SuggestionProvider<FabricClientCommandSource?> {
        val allSuggestions: MutableList<String> = mutableListOf("town", "nation")

        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource?>?,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            val input = builder.remaining.lowercase(Locale.getDefault())

            allSuggestions.stream()
                .filter { s -> s?.startsWith(input) == true }
                .forEach(builder::suggest)

            return builder.buildFuture()
        }
    }
}
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
import kotlinx.serialization.Serializable
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.api.Fetch
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.net.URI
import java.util.*
import java.util.concurrent.CompletableFuture
import com.mojang.brigadier.Command as command


class locate : Command() {

    init {
        name = "locate"
        description = "Gives you the coordinates of a town/nation."
        usageSuffix = "<type> <name>"
    }

    @Serializable
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
                    val town = Fetch.getTown(name) ?: return null
                    return Pair(town.coordinates?.spawn?.x!!, town.coordinates.spawn.z!!) // we dont need NPE cause every town HAS to have a spawn so we can just tell kotlin to fuck off
                }
                "nation" -> {
                    val nation = Fetch.getNation(name) ?: return null
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
                        ClickEvent.OpenUrl(URI("${Config.getMapUrl()}?world=minecraft_overworld&zoom=3&x=${coords?.first}&z=${coords?.second}"))
                    )

            )

            sendMessage(Text.literal("$name is located at x: ${coords?.first}, z: ${coords?.second} ").append(link))
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
        val allSuggestions: MutableList<String> = mutableListOf<String>("town", "nation")

        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource?>?,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            val input = builder.getRemaining().lowercase(Locale.getDefault())

            allSuggestions.stream()
                .filter({ s -> s?.startsWith(input) == true })
                .forEach(builder::suggest)

            return builder.buildFuture()
        }
    }
}
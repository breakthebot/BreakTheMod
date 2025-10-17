package net.chariskar.breakthemod.client.commands

import com.mojang.brigadier.Command as command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.api.Fetch
import net.chariskar.breakthemod.client.api.types.Nation
import net.chariskar.breakthemod.client.api.types.Reference
import net.chariskar.breakthemod.client.api.types.Town
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class goto: Command() {
    init {
        name = "goto"
        description = "Shows you the nearest spawnable town of the town you selected."
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val townName = ctx.getArgument("name", String::class.java)

        scope.launch {
            try {
                val reqTown: Town? = fetch.getTown(townName)
                if (reqTown == null) {
                    sendMessage(Text.literal("$townName isn't a real town."), Formatting.RED)
                    return@launch
                }

                if (reqTown.status?.isPublic == true && reqTown.status.canOutsidersSpawn == true) {
                    sendMessage(Text.literal("You can do /t spawn ${reqTown.name}"), Formatting.AQUA)
                    return@launch
                }

                if (reqTown.status?.isCapital == true) {
                    val nation = fetch.getNation(reqTown.nation?.uuid.toString())
                    if (nation?.status?.isPublic == true) {
                        sendMessage(Text.literal("Found suitable spawn in:\n ${reqTown.name} (${nation.name})"), Formatting.AQUA)
                        return@launch
                    }
                }

                var radius = 500
                var attempts = 3
                val validTowns: MutableList<String> = mutableListOf()

                while (attempts-- > 0) {
                    val query = buildJsonObject {
                        put("target_type", "TOWN")
                        put("target", townName)
                        put("search_type", "TOWN")
                        put("radius", radius)
                    }

                    val body = buildJsonObject {
                        putJsonArray("query") { add(query) }
                    }

                    val resp: List<Reference>? = Fetch.PostRequest<List<Reference>>(
                        Fetch.ItemTypes.NEARBY.url,
                        body.toString()
                    )

                    val names: List<String> = resp?.mapNotNull { it.name } ?: emptyList()
                    if (names.isEmpty()) {
                        radius += 500
                        continue
                    }

                    val townDetails: List<Town?>? = fetch.getTowns(names)
                    if (townDetails.isNullOrEmpty()) {
                        radius += 500
                        continue
                    }

                    for (town in townDetails) {
                        if (town == null) continue
                        val status = town.status
                        if (status?.isPublic == true && status.canOutsidersSpawn == true) {
                            validTowns.add(town.name)
                        } else if (status?.isCapital == true) {
                            val nation = fetch.getNation(town.nation?.uuid.toString())
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

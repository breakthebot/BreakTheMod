package net.chariskar.breakthemod.client.commands

import com.mojang.brigadier.Command as command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.api.Fetch
import net.chariskar.breakthemod.client.api.types.Location
import net.chariskar.breakthemod.client.api.types.PlayerLocationInfo
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class findPlayer : Command() {
    init {
        name = "findPlayer"
        description = "Tells you where a player is based on the map api."
        usageSuffix = "<name>"
    }


    @Serializable
    data class Payload(
        val query: List<List<Double>>
    )

    @Serializable
    data class ApiResponse(
        val max: Int? = null,
        val players: MutableList<Location>? = null
    )

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        scope.launch {
            val players: MutableList<Location>? = Fetch.getRequest<ApiResponse>(Config.getMapUrl() + "tiles/players.json")!!.players
            if (players.isNullOrEmpty()) {
                sendMessage(client, Text.literal("Received empty player list from map."), Formatting.RED)
                return@launch
            }
            val playerData = PlayerLocationInfo(name, 0.0, 0.0, false,null, false)

            for (player in players) {
                if (player.name.equals(name, ignoreCase = true)) {
                    playerData.found = true
                    playerData.x = player.location?.x!!
                    playerData.z = player.location.z!!

                    val coords = listOf(player.location.x, player.location.z)
                    val payload = Payload(listOf(coords))
                    val locationData: Location? = Fetch.PostRequest<Location>(Fetch.ItemTypes.LOCATION.url, Json.encodeToString(payload))
                    if (locationData != null && locationData.isWilderness == false) {
                        playerData.townName = locationData.town?.name
                    }
                    sendMessage(client, Text.literal(playerData.toString()), Formatting.AQUA)
                    return@launch


                } else continue

            }
        }
        return 0
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
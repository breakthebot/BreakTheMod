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
import net.chariskar.breakthemod.client.objects.Location
import net.chariskar.breakthemod.client.objects.PlayerLocationInfo
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
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
    data class MapResponse(
        val name: String? = null,
        val x: Double,
        val z: Double
    )

    @Serializable
    data class ApiResponse(
        val max: Int? = null,
        val players: MutableList<MapResponse>? = null
    )

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        scope.launch {
            val players: MutableList<MapResponse>? = Fetch.getRequest<ApiResponse>(Config.getMapUrl() + "tiles/players.json")!!.players

            if (players.isNullOrEmpty()) {
                sendMessage(Text.literal("Received empty player list from map."), Formatting.RED)
                return@launch
            }
            val playerData = PlayerLocationInfo(name, 0.0, 0.0, false,null, false)

            for (player in players) {
                if (player.name.equals(name, ignoreCase = true)) {
                    playerData.found = true
                    playerData.x = player.x
                    playerData.z = player.z

                    val coords = listOf(player.x, player.z)
                    val payload = Payload(listOf(coords))
                    val payloadJson = Json.encodeToString(payload)

                    val locationData: Location? = Fetch.postRequest<List<Location>>(Fetch.Items.LOCATION.url, Json.encodeToString(payload))?.first()

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
                        .executes(command { context: CommandContext<FabricClientCommandSource> ->
                            if (!getEnabled()) return@command 0
                            return@command run(context)
                        })
                )
        )
    }
}
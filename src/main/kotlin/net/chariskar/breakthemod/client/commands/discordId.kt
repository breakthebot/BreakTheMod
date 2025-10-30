package net.chariskar.breakthemod.client.commands

import com.mojang.brigadier.Command as command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.api.Fetch
import net.chariskar.breakthemod.client.api.Fetch.Companion.json
import net.chariskar.breakthemod.client.utils.serialization.SerializableUUID
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.net.URI
import java.util.*


class discordId : Command() {

    init {
        name = "discordLinked"
        description = "It tells you the discord username of a linked player."
        usageSuffix = "<name>"
    }


    private fun formatUUID(raw: String): String {
        val clean = raw.trim().replace("\"", "")
        return clean.replaceFirst(
            "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
            "$1-$2-$3-$4-$5"
        )
    }

    private fun isValidUUID(uuid: String): Boolean {
        return uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$".toRegex())
    }

    @Serializable
    data class discordPayload(
        val type: String,
        val target: SerializableUUID
    )


    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        scope.launch {
            val mojangResponse = Fetch.getRequest<String>("https://api.mojang.com/users/profiles/minecraft/$name")
            val rawUuid = mojangResponse?.split(",")[0]?.split(":")[1]

            if (rawUuid == null) {
                sendMessage(Text.literal("Unable to obtain uuid for $name."))
                return@launch
            }
            val formattedUuid = formatUUID(rawUuid)

            if (!isValidUUID(formattedUuid)) {
                sendMessage(Text.literal("Received invalid uuid for $name"))
                return@launch
            }

            val uuid = UUID.fromString(formattedUuid)

            val payload = buildJsonObject {
                put(
                    "query",
                    JsonArray(listOf(json.encodeToJsonElement(discordPayload(
                        "minecraft",
                        SerializableUUID(uuid)
                    ))))
                )
            }

            val emcResp = Fetch.PostRequest<String>(Fetch.ItemTypes.DISCORD.url, payload.toString())
            val userId = emcResp?.split(",")[0]?.split(":")[1]?.replace("\"", "")?.trim()

            val result: Text = Text.literal("Click Here")
                .setStyle(
                    Style.EMPTY
                        .withColor(Formatting.BLUE)
                        .withClickEvent(
                            ClickEvent.OpenUrl(URI("https://discord.com/users/$userId"))
                        )

                )
            sendMessage(result)
            return@launch
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
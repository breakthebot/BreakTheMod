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
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.breakthebot.breakthelibrary.api.DiscordAPI
import org.breakthebot.breakthelibrary.models.DiscordPayloadMinecraft
import org.breakthebot.breakthelibrary.network.Fetch
import org.breakthebot.breakthelibrary.utils.SerializableUUID
import java.net.URI
import java.util.*


class discordId : BaseCommand() {

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

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        scope.launch {
            val mojangResponse = Fetch.getRequest<String?>("https://api.mojang.com/users/profiles/minecraft/$name")
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

            val discord = DiscordAPI.getDiscord(listOf(DiscordPayloadMinecraft( target = SerializableUUID(uuid) )))?.first()

            val result: Text = Text.literal("Click Here")
                .setStyle(
                    Style.EMPTY
                        .withColor(Formatting.BLUE)
                        .withClickEvent(
                            ClickEvent.OpenUrl(URI("https://discord.com/users/${discord?.id}"))
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
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
import java.net.URI


class DiscordId : BaseCommand() {

    init {
        name = "discordLinked"
        description = "It tells you the discord username of a linked player."
        usageSuffix = "<name>"
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        scope.launch {
            val discord = DiscordAPI.getDiscord(listOf(name))?.first()
            val result: Text = if (discord != null) {
                Text.literal("Click Here")
                    .apply {
                        Style.EMPTY
                            .withColor(Formatting.BLUE)
                            .withClickEvent(
                                ClickEvent.OpenUrl(URI("https://discord.com/users/${discord}"))
                            )
                    }
            } else Text.of("No player found.")

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
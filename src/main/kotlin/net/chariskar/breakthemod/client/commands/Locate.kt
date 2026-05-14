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

object Locate : BaseCommand() {

    override val name = "locate"
    override val description = "Gives you the coordinates of a town/nation."
    override val usageSuffix = "<type> <name>"

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val type: String = ctx.getArgument("type", String::class.java)
        val name: String = ctx.getArgument("name", String::class.java)

        scope.launch {
            val coords = when (type) {
                "town" -> {
                    val town = TownAPI.getTown(name)
                    town?.coordinates?.spawn
                }
                "nation" -> {
                    val nation = NationAPI.getNation(name)
                    nation?.coordinates?.spawn
                }
                else -> null
            }
            if (coords == null) {
                sendError("No $type named $name found.")
                return@launch
            }
            sendMessage(Text.literal("$name is located at x: ${coords.x}, z: ${coords.z} ").append(getMapText(
                coords.x!!,
                coords.z!!
            )))
        }
        return 0
    }


    private fun getMapText(x: Float, z: Float): Text {
        val mapUrl = "${Config.getMapUrl()}?world=minecraft_overworld&zoom=5&x=$x&z=$z"
        return Text.literal("Click here").styled {
            Style.EMPTY
                .withColor(Formatting.BLUE)
                .withClickEvent (
                    ClickEvent.OpenUrl(URI.create(mapUrl))
                )
        }
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name).apply {
                then(RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("type", StringArgumentType.string())
                    .suggests(CommandSuggestions(mutableListOf("town", "nation")))
                )
                then(RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("name", StringArgumentType.string())
                    .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                        if (!getEnabled()) return@Command 0
                        return@Command run(context)
                    })
                )
            }
        )
    }
}

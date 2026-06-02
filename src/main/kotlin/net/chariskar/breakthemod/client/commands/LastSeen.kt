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
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.modules.Cache
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.getOrNull
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object LastSeen : BaseCommand(
    "lastSeen",
    "Shows the last time a user was online",
    "<name>"
) {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        scope.launch {
            val player = TownyAPI.getPlayer(name).getOrNull()
            if (player == null) {
                sendError("Unable to find player $name.")
                return@launch
            }
            val timestamps = player.timestamps?.lastOnline!!

            val message = if (player.status?.isOnline == true) {
                Text.literal("${player.name} has been online right now, for ${timestamps.days} days, ${timestamps.hours} hours and ${timestamps.minutes} minutes.").setStyle(
                    Style.EMPTY.withColor(
                        Formatting.AQUA
                    )
                )
            } else {
                Text.literal("${player.name} was last online ${timestamps.days} days, ${timestamps.hours} hours and ${timestamps.minutes} minutes.").setStyle(
                    Style.EMPTY.withColor(
                        Formatting.RED
                    )
                )
            }
            sendMessage(message)

            return@launch
        }
        return 0
    }

    override fun register(
        dispatcher: CommandDispatcher<FabricClientCommandSource>
    ) {
        super.register<String>(
            dispatcher,
            "name",
            StringArgumentType.string(),
            CommandSuggestions(Cache.playerCache.map { name }.toMutableList())
        )
    }
}
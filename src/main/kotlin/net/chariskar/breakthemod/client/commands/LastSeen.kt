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
import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.modules.Cache
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import org.breakthebot.breakthelibrary.api.TownyAPI
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

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

            val lastOnline = (Clock.System.now().toEpochMilliseconds() - player.timestamps.lastOnline).milliseconds

            val message = if (player.status.isOnline) {
                Component.literal("${player.name} has been online right now, for ${lastOnline.inWholeDays} days, ${lastOnline.inWholeHours % 24} hours and ${lastOnline.inWholeMinutes % 60} minutes.")
                    .withColor(TextColor.AQUA)
            } else {
                Component.literal("${player.name} was last online ${lastOnline.inWholeDays} days, ${lastOnline.inWholeHours % 24} hours and ${lastOnline.inWholeMinutes % 60} minutes.")
                    .withColor(TextColor.RED)
            }
            sendMessage(message)

            return@launch
        }
        return 0
    }

    override fun preRequisites(ctx: CommandContext<FabricClientCommandSource>): Pair<Boolean, String> {
        val name: String = ctx.getArgument("name", String::class.java)
        if (Cache.playerExists(name)) return super.preRequisites(ctx)
        return Pair(false, "Player does not exist.")
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register<String>(
            dispatcher,
            "name",
            StringArgumentType.string(),
            CommandSuggestions { Cache.playerNameCache }
        )
    }
}

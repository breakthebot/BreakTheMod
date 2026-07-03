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

package net.chariskar.breakthemod.debug.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.modules.Cache
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component

object CacheDebug : BaseCommand(
    "cacheDebug",
    "Retrieve users from the cache."
) {

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name: String = ctx.getArgument("name", String::class.java)
        sendMessage(
            Component.literal("Cache data ${Cache.getPlayer(name).toString()}")
        )
        return 0
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register<String>(
            dispatcher,
            "name",
            StringArgumentType.string(),
            CommandSuggestions(
                Cache.playerCache.values.map { name }.toMutableList()
            )
        )
    }
}
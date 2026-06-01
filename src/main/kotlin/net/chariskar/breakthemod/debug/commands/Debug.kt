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

import com.mojang.brigadier.context.CommandContext
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.modules.Cache
import net.chariskar.breakthemod.client.modules.NearbyEngine
import net.chariskar.breakthemod.client.widgets.NearbyWidget
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

object Debug : BaseCommand(
    "debugInfo",
    "Debug overview of breakthemod."
) {
    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        if (!Breakthemod.debug) return 0
        sendMessage(
            Text.literal("Version: ${Breakthemod.version}")
        )
        sendMessage(
            Text.literal("players" + MinecraftClient.getInstance().world?.players)
        )
        sendMessage(
            Text.literal("Nearby engine state: Running(${NearbyWidget.config.enabled}), Players(${NearbyEngine.getPlayers()})")
        )
        sendMessage("Loaded commands: ${Breakthemod.commands.size}.")
        sendMessage("Load modules: ${Breakthemod.modules.size}.")
        sendMessage(
            Text.literal("Server status: isEmc(${isEarthMc()}), enabled(${isModEnabled()})")
        )
        sendMessage(
            Text.literal("Cache size: ${Cache.playerCache.keys.size}")
        )
        return 0
    }
}
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

import com.mojang.brigadier.context.CommandContext
import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.api.engine.NearbyEngine
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.ServerUtils
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

class Debug : BaseCommand() {
    init {
        name = "btmdbg"
        description = ""
        usageSuffix = ""
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        if (!Config.getDbg()) return 0
        sendMessage(
            Text.literal("players" + MinecraftClient.getInstance().world?.players)
        )
        sendMessage(
            Text.literal("Nearby engine state: Running(${NearbyEngine.engineRunning}), Players(${NearbyEngine.getPlayers()})")
        )
        sendMessage(
            Text.literal("Server status: isEmc(${ServerUtils.isEarthMc()}), enabled(${ServerUtils.getEnabled()})")
        )
        sendMessage(
            Text.literal("")
        )

        return 0
    }
}
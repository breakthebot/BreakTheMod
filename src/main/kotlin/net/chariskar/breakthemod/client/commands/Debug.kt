package net.chariskar.breakthemod.client.commands

import com.mojang.brigadier.context.CommandContext
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.api.engine.NearbyEngine
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.ServerUtils
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

class Debug : Command() {
    init {
        name = "btmdbg"
        description = ""
        usageSuffix = ""
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        if (!Config.getDbg()) return 1
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
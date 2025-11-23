package net.chariskar.breakthemod

import com.mojang.brigadier.CommandDispatcher
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.commands.discordId
import net.chariskar.breakthemod.client.commands.findPlayer
import net.chariskar.breakthemod.client.commands.goto
import net.chariskar.breakthemod.client.commands.help
import net.chariskar.breakthemod.client.commands.lastSeen
import net.chariskar.breakthemod.client.commands.locate
import net.chariskar.breakthemod.client.commands.nearby
import net.chariskar.breakthemod.client.commands.onlineStaff
import net.chariskar.breakthemod.client.commands.townless
import net.chariskar.breakthemod.client.hooks.nearby.Hud
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.util.Identifier


class Breakthemod : ClientModInitializer {
    val nearbyLayer: Identifier = Identifier.of("breakthemod", "nearby_layer")

    private fun loadCommands(commands: MutableList<Command>) {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource>, _: CommandRegistryAccess ->
            for (command in commands) {
                command.register(dispatcher)
            }
        })
    }

    override fun onInitializeClient() {
        val helpCmd = help()
        val commandList: MutableList<Command> = mutableListOf(
            nearby(),
            onlineStaff(),
            townless(),
            goto(),
            findPlayer(),
            lastSeen(),
            discordId(),
            locate(),
            helpCmd
        )
        helpCmd.commands = commandList
        loadCommands(commandList)

        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, nearbyLayer) { context, _ -> Hud.render(context)}
    }
}

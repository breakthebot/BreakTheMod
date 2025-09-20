package net.chariskar.breakthemod

import com.mojang.brigadier.CommandDispatcher
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.commands.help
import net.chariskar.breakthemod.client.commands.nearby
import net.chariskar.breakthemod.client.commands.onlineFriends
import net.chariskar.breakthemod.client.commands.onlineStaff
import net.chariskar.breakthemod.client.commands.townless
import net.chariskar.breakthemod.client.hooks.nearby.Hud
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.util.Identifier


class Breakthemod : ModInitializer {
    val NEARBY_LAYER: Identifier = Identifier.of("breakthemod", "nearby_layer")

    override fun onInitialize() {
        val helpCmd = help()

        val commandList: MutableList<Command> = mutableListOf(
            nearby(),
            onlineStaff(),
            onlineFriends(),
            townless(),
            helpCmd
        )

        helpCmd.commands = commandList

        loadCommands(commandList)
        val renderer: Hud = Hud()

        if (SharedConstants.getGameVersion().name <= "1.21.5") {
            HudLayerRegistrationCallback.EVENT.register(HudLayerRegistrationCallback { layeredDrawer ->
                layeredDrawer!!.attachLayerBefore(
                    IdentifiedLayer.CHAT,
                    NEARBY_LAYER
                ) { drawContext, tickCounter ->
                    renderer.renderOverlay(drawContext, tickCounter)
                }
            })
        }



    }

    private fun loadCommands(commands: MutableList<Command>) {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource>, phase: CommandRegistryAccess ->
            for (command in commands) {
                command.register(dispatcher)
            }
        })
    }
}

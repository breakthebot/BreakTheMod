package net.chariskar.breakthemod

import com.mojang.brigadier.CommandDispatcher
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.commands.help
import net.chariskar.breakthemod.client.commands.nearby
import net.chariskar.breakthemod.client.commands.onlineFriends
import net.chariskar.breakthemod.client.commands.onlineStaff
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess


class Breakthemod : ModInitializer {

    override fun onInitialize() {
        val helpCmd = help()

        val commandList: MutableList<Command> = mutableListOf(
            nearby(),
            onlineStaff(),
            onlineFriends(),
            helpCmd
        )

        helpCmd.commands = commandList

        loadCommands(commandList)
    }

    private fun loadCommands(commands: MutableList<Command>) {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource>, phase: CommandRegistryAccess ->
            for (command in commands) {
                command.register(dispatcher)
            }
        })
    }
}

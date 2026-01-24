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
package net.chariskar.breakthemod

import com.mojang.brigadier.CommandDispatcher
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.api.engine.NearbyEngine
import net.chariskar.breakthemod.client.commands.Debug
import net.chariskar.breakthemod.client.commands.discordId
import net.chariskar.breakthemod.client.commands.findPlayer
import net.chariskar.breakthemod.client.commands.goto
import net.chariskar.breakthemod.client.commands.help
import net.chariskar.breakthemod.client.commands.lastSeen
import net.chariskar.breakthemod.client.commands.locate
import net.chariskar.breakthemod.client.commands.nearby
import net.chariskar.breakthemod.client.commands.onlineStaff
import net.chariskar.breakthemod.client.commands.townless
import net.chariskar.breakthemod.client.hooks.PlayerEvents
import net.chariskar.breakthemod.client.hooks.nearby.Hud
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Breakthemod : ClientModInitializer {
    val nearbyLayer: Identifier = Identifier.of("breakthemod", "nearby_layer")
    val logger: Logger = LoggerFactory.getLogger("breakthemod")

    private fun loadCommands(commands: MutableList<Command>) {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource>, _: CommandRegistryAccess ->
            for (command in commands) {
                command.register(dispatcher)
            }
        })
    }

    override fun onInitializeClient() {
        Config.loadConfig()
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
            Debug(),
            helpCmd
        )
        helpCmd.commands = commandList
        loadCommands(commandList)
        PlayerEvents.onServerJoin()
        PlayerEvents.onServerLeave()
        NearbyEngine.register()
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, nearbyLayer) { context, _ -> Hud.render(context)}
    }
}

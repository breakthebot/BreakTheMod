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

import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.api.Module
import net.chariskar.breakthemod.client.modules.NearbyEngine
import net.chariskar.breakthemod.client.commands.DiscordId
import net.chariskar.breakthemod.client.commands.FindPlayer
import net.chariskar.breakthemod.client.commands.goto
import net.chariskar.breakthemod.client.commands.Help
import net.chariskar.breakthemod.client.commands.LastSeen
import net.chariskar.breakthemod.client.commands.Locate
import net.chariskar.breakthemod.client.commands.Nearby
import net.chariskar.breakthemod.client.commands.OnlineStaff
import net.chariskar.breakthemod.client.commands.Townless
import net.chariskar.breakthemod.client.commands.Shop
import net.chariskar.breakthemod.client.commands.Calculate

import net.chariskar.breakthemod.client.hooks.Hud
import net.chariskar.breakthemod.client.utils.Config

import net.chariskar.breakthemod.client.modules.AutoHUD
import net.chariskar.breakthemod.client.modules.Cache
import net.chariskar.breakthemod.client.modules.ChatPreview
import net.chariskar.breakthemod.client.modules.ShopTracker

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.util.Identifier
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class Breakthemod : ClientModInitializer {

    val nearbyLayer: Identifier = Identifier.of("breakthemod", "nearby_layer")
    val logger: Logger = LoggerFactory.getLogger("breakthemod")

    companion object {
        const val VERSION: String = "1.5.2-BETA"
        val modules: MutableList<Module> = mutableListOf()
        val commands: MutableList<BaseCommand> = mutableListOf()
    }


    private fun loadCommands(commands: MutableList<BaseCommand>) {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource>, _: CommandRegistryAccess ->
            commands.forEach { it.register(dispatcher) }
        })
    }

    private fun loadModules(modules: MutableList<Module>) { modules.forEach { it.launch() } }

    /**
     * Load debugging modules.
     * */
    private fun loadDebug(): Boolean {
        try {
            val clazz = Class.forName("net.chariskar.breakthemod.debug.DebugLoader")
            val instance = clazz.getDeclaredConstructor().newInstance()
            val method = clazz.getMethod("loadDebug")
            method.invoke(instance)
            logger.info("Loaded debugging modules successfully.")
            return true
        } catch (e: Exception) {
            logger.error("Unexpected error loading debug commands", e)
            logger.info("Debugging module not present.")
        }
        return false
    }

    override fun onInitializeClient() {
        val file = File(
            MinecraftClient.getInstance()?.runDirectory,
            "config/breakthemod_config.json"
        ).apply {
            parentFile?.mkdirs()
            if (!exists()) {
                writeText("{}")
            }
        }
        Config.setFile(file)

        Config.loadConfig()

        val helpCmd = Help()
        commands.addAll(
            listOf(
                Nearby(),
                OnlineStaff(),
                Townless(),
                goto(),
                FindPlayer(),
                LastSeen(),
                DiscordId(),
                Locate(),
                Calculate(),
                Shop(),
                helpCmd
            )
        )
        helpCmd.commands = commands

        modules.addAll(
            listOf(
                AutoHUD,
                ChatPreview,
                Cache,
                NearbyEngine,
                ShopTracker
            )
        )

        helpCmd.modules = modules

        loadModules(modules)
        loadCommands(commands)

        Config.config.debug = loadDebug()

        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, nearbyLayer) { context, _ -> Hud.render(context) }
    }
}

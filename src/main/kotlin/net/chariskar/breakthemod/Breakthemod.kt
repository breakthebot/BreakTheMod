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

import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.modules.NearbyEngine
import net.chariskar.breakthemod.client.commands.DiscordId
import net.chariskar.breakthemod.client.commands.FindPlayer
import net.chariskar.breakthemod.client.commands.GotoCommand
import net.chariskar.breakthemod.client.commands.Help
import net.chariskar.breakthemod.client.commands.LastSeen
import net.chariskar.breakthemod.client.commands.Locate
import net.chariskar.breakthemod.client.commands.Nearby
import net.chariskar.breakthemod.client.commands.OnlineStaff
import net.chariskar.breakthemod.client.commands.Townless
import net.chariskar.breakthemod.client.commands.Calculate

import net.chariskar.breakthemod.client.utils.Config

import net.chariskar.breakthemod.client.modules.AutoHUD
import net.chariskar.breakthemod.client.modules.Cache
import net.chariskar.breakthemod.client.modules.ChatTracker

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess
import com.mojang.brigadier.CommandDispatcher
import net.chariskar.breakthemod.client.api.widget.BaseWidget
import net.chariskar.breakthemod.client.widgets.NearbyTowns
import net.chariskar.breakthemod.client.widgets.NearbyWidget
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class Breakthemod : ClientModInitializer {

    /**
     * All global variables in the mod.
     * @property debug Mods debug parameter.
     * @property version The version string.
     * @property logger Centralized mod logger.
     * @property modules All registered breakthemod modules.
     * @property commands All the registered breakthemod commands.
     * @property widgets All the registered widgets.
     * @property notifications All notifications from the mod.
     * */
    companion object {
        var debug: Boolean = false
            private set

        val version: String by lazy { "1.6.0-ALPHA${if (debug) "-DEBUG" else ""}" }
        val logger: Logger = LoggerFactory.getLogger("breakthemod")

        private val _modules: MutableList<BaseModule> = mutableListOf()
        private val _commands: MutableList<BaseCommand> = mutableListOf()
        private val _widgets: MutableList<BaseWidget> = mutableListOf()

        val modules: List<BaseModule>
            get() = _modules
        val commands: List<BaseCommand>
            get() = _commands
        val widgets: List<BaseWidget>
            get() = _widgets

        val notifications: MutableList<String> = mutableListOf()
    }


    private fun loadCommands() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource>, _: CommandRegistryAccess ->
            commands.forEach { it.register(dispatcher) }
        })
    }

    private fun loadModules() { modules.forEach { it.register() } }

    private fun registerWidgets() { widgets.forEach { it.register() } }

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
        } catch (_: ClassNotFoundException) {
            logger.info("Debug module not loaded.")
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

        _commands.addAll(
            listOf(
                Nearby,
                OnlineStaff,
                Townless,
                GotoCommand,
                FindPlayer,
                LastSeen,
                DiscordId,
                Locate,
                Calculate,
                Help
            )
        )

        _modules.addAll(
            listOf(
                AutoHUD,
                Cache,
                NearbyEngine,
                ChatTracker
            )
        )

        _widgets.addAll(
            listOf(
                NearbyWidget,
                NearbyTowns
            )
        )

        loadModules()
        loadCommands()
        registerWidgets()

        debug = loadDebug()

        if (version.contains("BETA")) {
            notifications.add("You are running a beta version of breakthemod, unexpected behaviour and glitches may occur.")
        }

        if (version.contains("ALPHA")) {
            notifications.add("You are running a alpha version of breakthemod, be careful.")
        }

    }
}

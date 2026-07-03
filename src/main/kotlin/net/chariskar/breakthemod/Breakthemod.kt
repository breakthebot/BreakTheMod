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
import net.chariskar.breakthemod.client.api.Notification
import net.chariskar.breakthemod.client.api.NotificationTypes
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.api.widget.BaseWidget
import net.chariskar.breakthemod.client.api.widget.WidgetManager
import net.chariskar.breakthemod.client.commands.Calculate
import net.chariskar.breakthemod.client.commands.DiscordId
import net.chariskar.breakthemod.client.commands.FindPlayer
import net.chariskar.breakthemod.client.commands.GotoCommand
import net.chariskar.breakthemod.client.commands.Help
import net.chariskar.breakthemod.client.commands.LastSeen
import net.chariskar.breakthemod.client.commands.Locate
import net.chariskar.breakthemod.client.commands.Nearby
import net.chariskar.breakthemod.client.commands.OnlineStaff
import net.chariskar.breakthemod.client.commands.Townless
import net.chariskar.breakthemod.client.modules.ActionTracker
import net.chariskar.breakthemod.client.modules.Cache
import net.chariskar.breakthemod.client.modules.ChatTracker
import net.chariskar.breakthemod.client.modules.LoginActions
import net.chariskar.breakthemod.client.modules.NearbyEngine
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.widgets.FishingTimeWidget
import net.chariskar.breakthemod.client.widgets.FishingWidget
import net.chariskar.breakthemod.client.widgets.MiningWidget
import net.chariskar.breakthemod.client.widgets.NearbyPlayers
import net.chariskar.breakthemod.client.widgets.NearbyTowns
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.Minecraft
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class Breakthemod : ClientModInitializer {

    /**
     * Global mod registry.
     * @property debug Mod debug parameter.
     * @property version The version string.
     * @property logger Centralized mod logger.
     * @property modules All registered breakthemod modules.
     * @property commands All the registered breakthemod commands.
     * @property widgets All the registered widgets.
     * @property notifications All notifications from the mod.
     * @property username The username of the client.
     * */
    companion object {
        var debug: Boolean = false
            private set

        val version: String by lazy { "1.6.0-ALPHA${if (debug) "-DEBUG" else ""}" }
        val logger: Logger = LoggerFactory.getLogger("breakthemod")

        val modules: List<BaseModule>
            field: MutableList<BaseModule> = mutableListOf()
        val commands: List<BaseCommand>
            field: MutableList<BaseCommand> = mutableListOf()
        val widgets: List<BaseWidget>
            field: MutableList<BaseWidget> = mutableListOf()

        val notifications: MutableList<Notification> = mutableListOf()

        val username: String
            get() = Minecraft.getInstance().user.name

        val onlinePlayers: List<String>
            get() = Minecraft.getInstance()
                .connection
                ?.onlinePlayers
                ?.mapNotNull { it.profile.name.toString() }
                .orEmpty()
    }

    private fun loadCommands() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource>, _ ->
            commands.forEach { it.register(dispatcher) }
        })
    }

    private fun loadModules() { modules.forEach { it.register() } }

    private fun registerWidgets() { widgets.forEach { it.register() } }

    /**
     * Load debugging modules.
     * @return Returns true if debug module is loaded, false if not found.
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
            Minecraft.getInstance().gameDirectory,
            "config/breakthemod_config.json"
        ).apply {
            parentFile?.mkdirs()
            if (!exists()) {
                writeText("{}")
            }
        }
        Config.setFile(file)

        Config.loadConfig()

        WidgetManager.changeMode(Config.config.widgetMode)

        commands.addAll(
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
                Help,
            )
        )

        modules.addAll(
            listOf(
                LoginActions,
                Cache,
                NearbyEngine,
                ChatTracker,
                ActionTracker
            )
        )

        widgets.addAll(
            listOf(
                NearbyPlayers,
                NearbyTowns,
                MiningWidget,
                FishingWidget,
                FishingTimeWidget
            )
        )

        WidgetManager.registerKeyBind()

        loadModules()
        loadCommands()
        registerWidgets()

        debug = loadDebug()

        if (version.contains("ALPHA")) {
            val alphaNotification = Notification(
                "Alpha",
                "You are running a alpha version of breakthemod, this is not a finished build, so expect glitches and instability.",
                NotificationTypes.UsingAlpha
            )
            notifications.add(alphaNotification)
        }

        if (version.contains("BETA")) {
            val betaNotification = Notification(
                "Beta",
                "You are running a beta version of breakthemod, unexpected behaviour and glitches may occur, please report any issues.",
                NotificationTypes.UsingBeta
            )
            notifications.add(betaNotification)
        }

    }
}

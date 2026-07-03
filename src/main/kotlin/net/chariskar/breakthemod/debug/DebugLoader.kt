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

package net.chariskar.breakthemod.debug

import com.mojang.brigadier.CommandDispatcher
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.Notification
import net.chariskar.breakthemod.client.api.NotificationTypes
import net.chariskar.breakthemod.debug.commands.CacheDebug
import net.chariskar.breakthemod.debug.commands.Debug
import net.chariskar.breakthemod.debug.commands.GetConfig
import net.chariskar.breakthemod.debug.commands.GetNotifications
import net.chariskar.breakthemod.debug.commands.LoadModule
import net.chariskar.breakthemod.debug.commands.UnloadModule
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DebugLoader {
    val logger: Logger = LoggerFactory.getLogger("breakthemod")

    fun loadDebugCommands() {
        val commands = listOf(
            Debug,
            CacheDebug,
            LoadModule,
            UnloadModule,
            GetConfig,
            GetNotifications
        )
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource>, _ ->
            commands.forEach { it.register(dispatcher) }
        })
        val debugNotification = Notification(
            "debug",
            "Debug tools loaded",
            NotificationTypes.UsingDebug
        )
        Breakthemod.notifications.add(debugNotification)
        logger.warn("Debugging tools active be warned.")
    }

    fun loadDebug() {
        loadDebugCommands()
    }
}

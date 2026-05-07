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
import net.chariskar.breakthemod.debug.commands.CacheDebug
import net.chariskar.breakthemod.debug.commands.Debug
import net.chariskar.breakthemod.debug.commands.LoadModule
import net.chariskar.breakthemod.debug.commands.UnloadModule
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess


class DebugLoader {
    fun loadDebugCommands() {
        val commands = listOf(
            Debug,
            CacheDebug,
            LoadModule,
            UnloadModule
        )
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource>, _: CommandRegistryAccess ->
            commands.forEach { it.register(dispatcher) }
        })
    }
}

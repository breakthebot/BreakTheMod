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

package net.chariskar.breakthemod.debug.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.BaseCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component

object LoadModule : BaseCommand(
    "loadModule",
    "Load any module."
) {

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        super.register<String>(
            dispatcher,
            "name",
            StringArgumentType.string(),
            CommandSuggestions { Breakthemod.modules.filter { !it.enabled }.map { it.name } }
        )
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name = ctx.getArgument("name", String::class.java)

        val module = Breakthemod.modules.firstOrNull { it.name == name }

        if (module?.enabled == true) {
            sendMessage(Component.literal("Module $name already enabled."))
            return 0
        }

        val clazz = Class.forName(module?.javaClass?.name)

        val instance = clazz.getField("INSTANCE").get(null)
        val method = clazz.getMethod("launch")

        method.invoke(instance)

        return 0
    }
}

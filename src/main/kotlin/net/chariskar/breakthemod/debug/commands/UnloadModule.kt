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

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.BaseCommand
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text

object UnloadModule : BaseCommand() {
    init {
        name = "unloadModule"
        description = "Unload a module at will"
        usageSuffix = "<name>"
    }


    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(
                    RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("name", StringArgumentType.string())
                        .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                            if (!getEnabled()) return@Command 0
                            return@Command run(context)
                        })
                )
        )
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        val name = ctx.getArgument("name", String::class.java)

        val module = Breakthemod.modules.firstOrNull() { it.name == name }

        if (module == null || !module.enabled) {
            sendMessage(Text.literal("Module $name has not been loaded."))
            return 0
        }

        val clazz = Class.forName("net.chariskar.breakthemod.client.modules.$name")

        val instance = clazz.getField("INSTANCE").get(null)
        val method = clazz.getMethod("disable")

        method.invoke(instance)

        return 0
    }
}
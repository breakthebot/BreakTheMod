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

package net.chariskar.breakthemod.client.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import org.breakthebot.breakthelibrary.api.ServerAPI
import org.breakthebot.breakthelibrary.api.TownyAPI

object OnlineStaff : BaseCommand(
    "onlinestaff",
    "Shows online staff",
    "[api]"
) {
    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int = 1

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("api", StringArgumentType.string())
                    .executes(Command { conComponent: CommandContext<FabricClientCommandSource> ->
                        if (!isModEnabled()) {return@Command 0}
                        val arg: String = conComponent.getArgument("api", String::class.java)
                        return@Command exec(arg == "api")
                    }))
                .executes(Command { _: CommandContext<FabricClientCommandSource> ->
                    if (!isModEnabled()) {return@Command 0}
                    return@Command exec(null)
                })
        )
    }

    suspend fun onlineStaff(api: Boolean): Component {
        val staff = ServerAPI.getStaffList()
            .mapSuccess { it.toList() }
            .getOrElse {
                return Component.literal("Received empty staff list.")
                    .setStyle(Style.EMPTY.withColor(TextColor.RED))

            }

        if (staff.isEmpty()) return Component.literal("Received invalid staff list.")
            .setStyle(Style.EMPTY.withColor(TextColor.RED))

        var onlineStaffComponent = Component.empty()

        val staffNames: List<String> = if (api) {
            TownyAPI.getPlayers( staff.map { v->v.toString() } )
                .first()
                .getOrNull()
                ?.filter { r -> r.status.isOnline }
                ?.map { r -> r.name }!!
        } else {
            staff.mapNotNull { uuid ->
                client.connection!!.onlinePlayers.firstOrNull {
                    it.profile.id == uuid
                }?.profile?.name
            }
        }

        for (i in staffNames.indices) {
            onlineStaffComponent = onlineStaffComponent.append(
                Component.literal(staffNames[i]).setStyle(Style.EMPTY.withColor(TextColor.AQUA))
            )

            if (i < staffNames.size - 1) {
                onlineStaffComponent = onlineStaffComponent.append(
                    Component.literal(", ").setStyle(Style.EMPTY.withColor(TextColor.WHITE))
                )
            }
        }

        return Component.empty().apply {
           if (staffNames.isNotEmpty()) {
               append(onlineStaffComponent)
               append(Component.literal(" [").setStyle(Style.EMPTY.withColor(TextColor.GRAY)))
               append(
                   Component.literal(staffNames.size.toString())
                       .setStyle(Style.EMPTY.withColor(TextColor.WHITE))
               )
               append(Component.literal("]").setStyle(Style.EMPTY.withColor(TextColor.GRAY)))
           } else {
               append("No online staff").style = Style.EMPTY.withColor(TextColor.AQUA)
           }
        }
    }

    fun exec(api: Boolean?): Int {
        scope.launch {
            val staff = onlineStaff(api ?: false)
            sendMessage(staff) 
        }
        return 0
    }
}
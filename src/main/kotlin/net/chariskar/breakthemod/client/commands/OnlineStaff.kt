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
import net.chariskar.breakthemod.client.utils.ServerUtils.getEnabled
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.chariskar.breakthemod.client.api.BaseCommand
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.network.getOrNull

object OnlineStaff : BaseCommand() {
    override val name = "onlinestaff"
    override val description = "Shows online staff"
    override val usageSuffix = "[api]"

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("api", StringArgumentType.string())
                    .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                        if (!getEnabled()) {return@Command 0}
                        val arg: String = context.getArgument("api", String::class.java)
                        return@Command exec(arg == "api")
                    }))
                .executes(Command { _: CommandContext<FabricClientCommandSource> ->
                    if (!getEnabled()) {return@Command 0}
                    return@Command exec(null)
                })
        )
    }


    suspend fun onlineStaff(api: Boolean): Text {
        val staff = TownyAPI.getStaff()

        if (staff.isNullOrEmpty()) return Text.literal("Received invalid staff list.").setStyle(Style.EMPTY.withColor(Formatting.RED))

        var onlineStaffText: MutableText = Text.empty()

        val staffNames: List<String> = if (api) {
            TownyAPI.getPlayers( staff.map { v->v.toString() } )
                .first()
                ?.getOrNull()
                ?.filter { r -> r.status!!.isOnline == true }
                ?.map { r -> r.name }!!
        } else {
            staff.mapNotNull { uuid ->
                client.networkHandler!!.playerList.firstOrNull {
                    it.profile.id == uuid.toUUID()
                }?.profile?.name
            }
        }

        for (i in staffNames.indices) {
            onlineStaffText = onlineStaffText.append(
                Text.literal(staffNames[i]).setStyle(Style.EMPTY.withColor(Formatting.AQUA))
            )

            if (i < staffNames.size - 1) {
                onlineStaffText = onlineStaffText.append(
                    Text.literal(", ").setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                )
            }
        }

        return Text.empty().apply {
           if (staffNames.isNotEmpty()) {
                append(onlineStaffText)
                append(Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                append(
                    Text.literal(staffNames.size.toString())
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                )
                append(Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
           } else {
               append("No online staff").style = Style.EMPTY.withColor(Formatting.AQUA)
           }
        }
    }

    fun exec(api: Boolean?): Int {
        scope.launch {
            val staff = onlineStaff(api ?: false)
            client.execute { sendMessage(staff) }
        }
        return 0
    }
}
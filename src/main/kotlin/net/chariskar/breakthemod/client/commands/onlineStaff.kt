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
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.chariskar.breakthemod.client.api.Fetch
import net.chariskar.breakthemod.client.api.types.Nation
import net.chariskar.breakthemod.client.api.types.Resident
import net.chariskar.breakthemod.client.utils.SerializableUUID
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*
import net.chariskar.breakthemod.client.api.Command as command


class onlineStaff : command() {
    init {
        name = "onlinestaff"
        description = "Shows online staff"
        usageSuffix = "<api>"
    }

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>("api", StringArgumentType.string())
                    .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                        if (!getEnabled()) {return@Command 0}
                        val arg: String = context.getArgument("api", String::class.java)

                        if (arg == "api") return@Command execAPI(context)
                        return@Command execNormal(context)
                    }))
                .executes(Command { context: CommandContext<FabricClientCommandSource> ->
                    if (!getEnabled()) {return@Command 0}

                    return@Command execNormal(context)
                })
        )
    }
    @Serializable
    data class StaffList(
        @Contextual val owner: List<SerializableUUID>,
        @Contextual val admin: List<SerializableUUID>,
        @Contextual val developer: List<SerializableUUID>,
        @Contextual val staffmanager: List<SerializableUUID>,
        @Contextual val moderator: List<SerializableUUID>,
        @Contextual val helper: List<SerializableUUID>
    ) {
        fun allStaff(): List<SerializableUUID> {
            return (owner + admin + staffmanager + moderator + helper + developer).distinct()
        }
    }

    fun execNormal(ctx: CommandContext<FabricClientCommandSource>): Int {
        scope.launch {
            val staff: StaffList = Fetch.getRequest<StaffList>(Fetch.ItemTypes.STAFF.url)!!
            val uuids: List<UUID> = staff.allStaff().map { uid-> uid.toUUID() }

            val onlinePlayers: List<UUID> = client.networkHandler!!.playerList.stream().map { pl -> pl.profile.id }.toList()
            val onlineStaff: MutableList<UUID> = mutableListOf()
            onlinePlayers.forEach { pl ->
                if (pl in uuids) onlineStaff.add(pl)
            }
            val staffNames: List<String> = onlineStaff.mapNotNull { uuid ->
                client.networkHandler!!.playerList.firstOrNull {
                    it.profile.id == uuid
                }?.profile?.name
            }
            var onlineStaffText: MutableText = Text.empty()

            for (i in 0..<staffNames.size) {
                onlineStaffText = onlineStaffText.append(
                    Text.literal(staffNames.get(i)).setStyle(Style.EMPTY.withColor(Formatting.AQUA))
                )

                if (i < staffNames.size - 1) {
                    onlineStaffText = onlineStaffText.append(
                        Text.literal(", ").setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                    )
                }
            }

            var message: Text
            if (staffNames.isNotEmpty()) {
                message = Text.literal("")
                    .append(onlineStaffText)
                    .append(Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                    .append(
                        Text.literal(java.lang.String.valueOf(staffNames.size))
                            .setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                    )
                    .append(Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
            } else {
                message = Text.empty()
                    .append("No online staff").setStyle(Style.EMPTY.withColor(Formatting.AQUA))
            }
            sendMessage(client, message)
        }

        return 0
    }

    fun execAPI(ctx: CommandContext<FabricClientCommandSource>): Int {
        scope.launch {
            val staff: StaffList = Fetch.getRequest<StaffList>(Fetch.ItemTypes.STAFF.url)!!

            val staffNames: List<String> = fetch.getObjects<Resident>(Fetch.ItemTypes.PLAYER, staff.allStaff().map { v->v.toUUID() }.toString() )!!
                .filter { r -> r.status!!.isOnline == true }
                .map { r -> r.name }


            var onlineStaffText: MutableText = Text.empty()

            for (i in 0..<staffNames.size) {
                onlineStaffText = onlineStaffText.append(
                    Text.literal(staffNames[i]).setStyle(Style.EMPTY.withColor(Formatting.AQUA))
                )

                if (i < staffNames.size - 1) {
                    onlineStaffText = onlineStaffText.append(
                        Text.literal(", ").setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                    )
                }
            }
            var message: Text

            if (staffNames.isNotEmpty()) {
                message = Text.literal("")
                    .append(onlineStaffText)
                    .append(Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                    .append(
                        Text.literal(java.lang.String.valueOf(staffNames.size))
                            .setStyle(Style.EMPTY.withColor(Formatting.WHITE))
                    )
                    .append(Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
            } else {
                message = Text.empty()
                    .append("No online staff").setStyle(Style.EMPTY.withColor(Formatting.AQUA))
            }
            sendMessage(client, message)
        }


        return 0
    }
}
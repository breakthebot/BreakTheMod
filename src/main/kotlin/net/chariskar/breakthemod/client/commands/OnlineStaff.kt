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
import net.chariskar.breakthemod.client.utils.ServerAPI
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import org.breakthebot.breakthelibrary.api.TownyAPI
import java.util.UUID
import kotlin.uuid.toJavaUuid

fun getRoleColor(role: String): Int = when (role.lowercase()) {
    "owner" -> 0xd7342a
    "admin" -> 0x3498db
    "developer" -> 0x55ffff
    "moderator" -> 0x1f8b4c
    "helper" -> 0x1abc9c
    else -> 0x0000
}

object OnlineStaff : BaseCommand(
    "onlinestaff",
    "Shows online staff",
    "[api]"
) {
    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int = 1

    override fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)
                .then(
                    RequiredArgumentBuilder.argument<FabricClientCommandSource?, String>(
                        "api",
                        StringArgumentType.string()
                    )
                        .executes(
                            Command { component: CommandContext<FabricClientCommandSource> ->
                                if (!isModEnabled()) {
                                    return@Command 0
                                }
                                val arg: String = component.getArgument("api", String::class.java)
                                return@Command exec(arg == "api")
                            }
                        )
                )
                .executes(
                    Command { _: CommandContext<FabricClientCommandSource> ->
                        if (!isModEnabled()) {
                            return@Command 0
                        }
                        return@Command exec(null)
                    }
                )
        )
    }

    suspend fun onlineStaff(api: Boolean): Component {
        val onlineStaffComponent = Component.literal("Online Staff: \n")

        val staffNames: Map<String, List<String>> = if (api) {
            val staff = ServerAPI.getStaff()

            val staffUuids = staff
                .values
                .flatten()
                .map { it.toString() }

            val staffMap = mutableMapOf<UUID, String>()

            val data = TownyAPI.getPlayers(staffUuids)
                .first()
                .mapSuccess {
                    it.map { r ->
                        if (r.status.isOnline) {
                            staffMap[r.uuid.toJavaUuid()] = r.name
                        }
                    }
                }
                .getOrNull()

            if (data == null) {
                return Component.literal("Unexpected error occurred when fetching the staff names from the API.")
                    .setStyle(
                        Style.EMPTY.withColor(TextColor.RED)
                    )
            }

            staff.mapValues {
                it.value.mapNotNull { u -> staffMap[u] }
            }
        } else {
            ServerAPI.getStaff()
                .mapValues { (_, playerIds) ->
                    playerIds.mapNotNull { id ->
                        client.connection!!.onlinePlayers
                            .firstOrNull { player -> player.profile.id == id }
                            ?.profile
                            ?.name
                    }
                }
                .filterValues { it.isNotEmpty() }
        }

        staffNames.forEach { (rank, staff) ->
            if (staff.isEmpty()) return@forEach

            val color = getRoleColor(rank)
            val role = rank.replaceFirstChar { it.uppercaseChar() }

            onlineStaffComponent.append(
                Component.literal("$role: ")
                    .withColor(color)
                    .append(
                        Component.literal(staff.joinToString(", "))
                            .withColor(TextColor.GRAY)
                            .append(
                                Component.literal(" [${staff.size}]\n")
                                    .withColor(TextColor.AQUA)
                            )
                    )
            )
        }

        if (staffNames.values.flatten().isEmpty()) {
            return Component.literal("No staff online at the moment.").setStyle(
                Style.EMPTY.withColor(TextColor.RED)
            )
        }

        return onlineStaffComponent
    }

    fun exec(api: Boolean?): Int {
        scope.launch {
            val staff = onlineStaff(api ?: false)
            sendMessage(staff)
        }
        return 0
    }
}

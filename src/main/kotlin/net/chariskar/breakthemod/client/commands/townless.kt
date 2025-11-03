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

import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.Command
import net.chariskar.breakthemod.client.api.Fetch
import net.chariskar.breakthemod.client.api.types.Resident
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*


class townless : Command() {
    val batchSize: Int = 100

    init {
        name = "townless"
        description = "Shows all townless player"
        usageSuffix = ""
    }

    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        scope.launch {
            val onlinePlayers = client.networkHandler!!.playerUuids
            if (onlinePlayers.size == 1) {
                sendMessage(Text.literal("No online players found"))
                return@launch
            }
            val townless: MutableList<String> = mutableListOf()
            val batch: MutableList<UUID> = mutableListOf()

            for (p in onlinePlayers) {
                batch.add(p)
                if (batch.size == batchSize) {
                    val players: List<Resident?>? = fetch.getResidents(batch.map { u -> u.toString() })
                    if (players.isNullOrEmpty()) {
                        logger.warn("Received empty batch on townless")
                    }
                    players?.forEach { p ->
                        if (p?.status?.hasTown == false) townless.add(p.name)
                    }
                    batch.clear()
                }
            }

            if (batch.isNotEmpty()) {
                val players: List<Resident?>? = fetch.getResidents(batch.map { it.toString() })
                if (players.isNullOrEmpty()) {
                    logger.warn("Received empty batch on townless")
                } else {
                    players.forEach { resident ->
                        if (resident?.status?.hasTown == false) {
                            townless.add(resident.name)
                        }
                    }
                }
            }

            val message = Text.literal("Townless Users:\n").setStyle(Style.EMPTY.withColor(Formatting.AQUA))
            val own: Resident = Fetch.getInstance().getResident(client.session.username)!!
            if (own.town?.name.isNullOrEmpty()) return@launch

            for (user in townless) {
                val inviteMessage = "/msg $user " + Config.getTownlessMessage(own.town?.name!!)

                val userText: Text = Text.literal(user)
                    .setStyle(
                        Style.EMPTY
                            .withColor(Formatting.AQUA)
                            .withClickEvent(ClickEvent.CopyToClipboard(inviteMessage))
                            .withHoverEvent(HoverEvent.ShowText(Text.literal("Click to copy message to clipboard.")))
                    )

                message.append(userText).append(Text.literal("\n"))
            }
            sendMessage(message)
        }
        return 0
    }


}
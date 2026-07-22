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

import com.mojang.brigadier.context.CommandContext
import net.chariskar.breakthemod.client.api.command.BaseCommand
import net.chariskar.breakthemod.client.modules.NotificationManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object GetNotifications : BaseCommand(
    "getNotifications",
    "Display the notifications"
) {
    override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
        if (NotificationManager.notifications.isEmpty()) {
            sendMessage("No notifications.")
            return 1
        }
        NotificationManager.notifications.forEach {
            sendMessage(it.toString())
        }

        return 1
    }
}

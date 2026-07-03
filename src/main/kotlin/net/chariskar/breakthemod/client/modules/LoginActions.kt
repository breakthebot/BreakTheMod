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

package net.chariskar.breakthemod.client.modules

import kotlinx.coroutines.runBlocking
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.models.AutoHudType
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Schedule
import net.chariskar.breakthemod.client.utils.Scheduler
import net.chariskar.breakthemod.client.utils.UpdateUtility
import net.chariskar.breakthemod.client.utils.save
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import kotlin.time.Duration.Companion.seconds

/**
 * Executes actions on login.
 * */
object LoginActions : BaseModule(
    "LoginActions",
    "Executes all login actions.",
) {

    override fun enable() {
        ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _: ClientPacketListener?, _: PacketSender?, client: Minecraft ->
            val task = Schedule(
                "LoginActions",
                {
                    if (!isEarthMc()) return@Schedule
                    autoHud()
                    checkUpdates()
                    sendNotifications()
                },
                10.seconds
            )

            Scheduler.schedule(task)
        })
    }

    /** Enables the hud on join.*/
    fun autoHud() {
        val hud = Config.features.hudType

        if (hud == AutoHudType.None) { return }

        val command = when(hud) {
            AutoHudType.PermHud -> "plot perm hud"
            AutoHudType.MapHud -> "towny map hud"
        }

        client.connection?.sendCommand(command)
    }

    /** Check for any available breakthemod updates. */
    fun checkUpdates() {
        runBlocking { UpdateUtility.checkVersion() }
    }

    /** Send the notifications if they weren't already sent.*/
    fun sendNotifications() {
        val filteredNotifications = Breakthemod.notifications.filter { Config.notifications[it.name] ?: false }
        if (filteredNotifications.isEmpty()) return

        sendMessage("Notifications: ")

        filteredNotifications
            .forEach {
                Config.notifications[it.name] = true
                sendMessage(it.toString())
            }

        Config.save()
    }


}
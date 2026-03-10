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

/// credits to https://github.com/Veyronity/Earthy/blob/master/client/fabric/src/main/java/au/lupine/earthy/fabric/module/AutoHUD.java

package net.chariskar.breakthemod.client.modules

import net.chariskar.breakthemod.client.api.Module
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Config.AutoHudType
import net.chariskar.breakthemod.client.utils.ServerUtils
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


object AutoHUD : Module() {
    override fun disable() {}

    override fun enable() {
        ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _: ClientPlayNetworkHandler?, _: PacketSender?, client: MinecraftClient ->
            val scheduler = Executors.newSingleThreadScheduledExecutor()
            scheduler.schedule( {
                if (!ServerUtils.isEarthMc()) { return@schedule }

                val hud: AutoHudType = Config.getHud()

                if (hud == AutoHudType.None) { return@schedule }

                val command: String = when(hud) {
                    AutoHudType.PermHud -> "plot perm hud"
                    AutoHudType.MapHud -> "towny map hud"
                    else -> ""
                }

                client.networkHandler?.sendChatCommand(command)
            }, 6L, TimeUnit.SECONDS)
        })
    }

}
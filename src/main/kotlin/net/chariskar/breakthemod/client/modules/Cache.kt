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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.chariskar.breakthemod.client.api.Module
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Scheduler
import net.chariskar.breakthemod.client.utils.ServerUtils
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.breakthebot.breakthelibrary.api.PlayerAPI
import org.breakthebot.breakthelibrary.models.Resident
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/// Credit to https://github.com/Veyronity/Earthy/blob/master/client/fabric/src/main/java/au/lupine/earthy/fabric/module/Cache.java

object Cache : Module() {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val cachedPlayers: MutableList<Resident> = CopyOnWriteArrayList()


    override fun disable() {}

    override fun enable() {
        ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _: ClientPlayNetworkHandler?, _: PacketSender?, _: MinecraftClient? ->
            Scheduler.schedule({
                if (!ServerUtils.isEarthMc() || !Config.getNameTag()) { return@schedule }
                updatePlayers()
            }, 6L, TimeUnit.SECONDS)
        })

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPlayNetworkHandler?, _: MinecraftClient? ->
            cachedPlayers.clear()
        })
    }

    private fun updatePlayers() {
        if (!ServerUtils.isEarthMc() || !Config.getNameTag()) return

        client.execute {
            val players = client.networkHandler?.playerList?.map { it.profile.name } ?: return@execute

            scope.launch {
                val apiPlayers = PlayerAPI.getPlayers(players)

                if (apiPlayers.isNullOrEmpty()) {
                    cachedPlayers.clear()
                } else {
                    cachedPlayers.clear()
                    cachedPlayers.addAll(apiPlayers)
                }
            }
        }
    }
}
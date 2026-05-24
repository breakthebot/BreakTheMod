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
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Scheduler
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.Resident
import org.breakthebot.breakthelibrary.network.getOrNull
import org.breakthebot.breakthelibrary.network.onError
import org.breakthebot.breakthelibrary.network.onSuccess
import java.util.Hashtable

import java.util.concurrent.TimeUnit

/**
 * Cache update handler for PlayerNametagInfo feature.
 *
 * @property scope The execution scope for the cache update..
 * @property playerCache The player currently cached in memory.
 * @property townCache A list of every town from /towns.
 * @property nationCache A list of every nation from /nations.
 *  */
object Cache : BaseModule() {

    override val name = "Cache"
    override val description = "Cache handler for the PlayerNametagInfo feature."
    override val hidden: Boolean = false

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val username: String = MinecraftClient.getInstance().session.username

    val playerCache: Hashtable<String, Resident> = Hashtable()
    // keep a cache of all towns and nations for /locate, a full object cache is not needed yet.
    // spare some ram.
    val townCache: MutableList<String> = mutableListOf()
    val nationCache: MutableList<String> = mutableListOf()

    override fun enable() {
        ClientPlayConnectionEvents.JOIN.register { _: ClientPlayNetworkHandler?, _: PacketSender?, _: MinecraftClient? ->
            replaceApiUrl()
            runTask()
            Scheduler.schedule(
                { runTask() },
                10L,
                TimeUnit.MINUTES
            )
        }

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPlayNetworkHandler?, _: MinecraftClient? ->
            playerCache.clear()
        })
    }

    private fun updatePlayers() {
        if (!isEarthMc() || !Config.getNameTag()) return
        playerCache.clear()

        val players = client.networkHandler!!.playerUuids.toList().map { it.toString() }

        scope.launch {
            val apiPlayers = TownyAPI.getPlayers(players)
                .flatMap { it
                    .onError { e-> handleCacheError("playerCache", e.message) }
                    .getOrNull()
                    .orEmpty()
                }

            apiPlayers.forEach { it ->
                playerCache[it.name] = it
            }
        }
    }

    fun updateCache() {
        townCache.clear()
        nationCache.clear()

        scope.launch {
            TownyAPI.getAllTowns()
                .onSuccess { townCache.addAll(it.map { name }) }
                .onError {
                    handleCacheError("townCache", it.message)
                }
            TownyAPI.getAllNations()
                .onSuccess { nationCache.addAll(it.map { name }) }
                .onError {
                    handleCacheError("nationCache", it.message)
                }
        }
    }

    private fun handleCacheError(origin: String, message: String) {
        Breakthemod.logger.error("Unexpected error occurred while updating $origin cache.", message)
    }

    fun runTask() {
        if (!isEarthMc() || !isModEnabled()) return
        updateCache()
        updatePlayers()
    }

    fun getPlayer(
        name: String
    ): Resident?  = playerCache[name]
}

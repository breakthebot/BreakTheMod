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

import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.modules.Cache.nationCache
import net.chariskar.breakthemod.client.modules.Cache.playerCache
import net.chariskar.breakthemod.client.modules.Cache.scope
import net.chariskar.breakthemod.client.modules.Cache.townCache
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.widgets.NearbyTowns
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.breakthebot.breakthelibrary.api.MapApi
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.*
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Cache update handler for the mod.
 *
 * @property scope The execution scope for the cache update.
 * @property playerCache The player currently cached in memory.
 * @property townCache A list of every town from /towns.
 * @property nationCache A list of every nation from /nations.
 *  */
object Cache : BaseModule(
    "Cache",
    "Cache handler for the mod."
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)


    private val _playerCache: Hashtable<String, Resident> = Hashtable()

    val playerCache: Hashtable<String, Resident>
        get() = _playerCache

    // keep a cache of all towns and nations for /locate, a full object cache is not needed yet.
    // spare some ram.
    private val _townCache: MutableList<String> = mutableListOf()

    private val _nationCache: MutableList<String> = mutableListOf()

    private val _nearbyTowns: MutableList<Town> = mutableListOf()

    val townCache: List<String>
        get() = _townCache

    val nationCache: List<String>
        get() = _nationCache

    val nearbyTowns: List<Town>
        get() = _nearbyTowns

    override fun enable() {
        if (enabled) return

        ClientPlayConnectionEvents.JOIN.register(
            ClientPlayConnectionEvents.Join { _: ClientPlayNetworkHandler?, _: PacketSender?, _: MinecraftClient ->
            enabled = true
        })

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPlayNetworkHandler?, _: MinecraftClient? ->
            enabled = false
        })

        scope.launch {
            while (true) {
                if (!enabled) return@launch
                 delay(10.minutes)
                runTask()
            }
        }

        scope.launch {
            while (true) {
                if (!enabled) return@launch
                delay(30.seconds)
                updateNearbyTowns()
            }
        }
    }
    
    private suspend fun updatePlayers() {
        playerCache.clear()

        val players = client.networkHandler!!.playerUuids.toList().map { it.toString() }

        val apiPlayers = TownyAPI.getPlayers(players)
            .flatMap { it
                .onError { e-> handleCacheError("playerCache", e.message) }
                .getOrNull()
                .orEmpty()
            }

        apiPlayers.forEach {
            playerCache[it.name] = it
        }

    }

    /**
     * Updates [Cache.nearbyTowns].
     *
     * World loaded required.
     * */
    suspend fun updateNearbyTowns() {
        if (!isEarthMc() || !Config.features.cacheEnabled || !NearbyTowns.config.enabled) return

        _nearbyTowns.clear()
        val player = MinecraftClient.getInstance().player ?: return

        val coords = buildJsonArray {
            add(JsonPrimitive(player.x.toInt()))
            add(JsonPrimitive(player.z.toInt()))
        }

        val body = NearbyItem(
            targetType = NearbyType.COORDINATE,
            searchType = NearbyType.TOWN,
            radius = 500,
            target = coords
        )

        val resp = MapApi.getNearby(body)
            .getOrNull()
            ?.take(3)
            ?.mapNotNull { it.name }

        if (resp.isNullOrEmpty()) return

        val towns = TownyAPI.getTowns(resp)
            .first()
            .onError {
                handleCacheError("nearbyTownCache", it.message)
            }
            .getOrNull() ?: listOf()

        _nearbyTowns.addAll(towns)
    }

    /**
     * Update town and nation caches.
     * */
    suspend fun updateCache() {
        _townCache.clear()
        _nationCache.clear()

        TownyAPI.getAllTowns()
            .onSuccess { _townCache.addAll(it.map { name }) }
            .onError {
                handleCacheError("townCache", it.message)
            }
        TownyAPI.getAllNations()
            .onSuccess { _nationCache.addAll(it.map { name }) }
            .onError {
                handleCacheError("nationCache", it.message)
            }
    }

    private fun handleCacheError(origin: String, message: String) {
        Breakthemod.logger.error("Unexpected error occurred while updating $origin cache.", message)
    }

    suspend fun runTask() {
        if (!isEarthMc() || !isModEnabled() || !Config.features.cacheEnabled) return
        updateCache()
        updatePlayers()
    }

    fun getPlayer(
        name: String
    ): Resident?  = playerCache[name]
}

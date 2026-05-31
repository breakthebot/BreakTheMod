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
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.widgets.NearbyTowns
import net.minecraft.client.MinecraftClient
import org.breakthebot.breakthelibrary.api.MapApi
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.*
import java.util.*
import kotlin.time.Duration.Companion.minutes

/**
 * Cache update handler for the mod.
 *
 * @property scope The execution scope for the cache update.
 * @property playerCache The player currently cached in memory.
 * @property townCache A list of every town from /towns.
 * @property nationCache A list of every nation from /nations.
 *  */
// TODO: make caches return an immutable copy.
object Cache : BaseModule(
    "Cache",
    "Cache handler for the mod."
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val username: String
        get() = MinecraftClient.getInstance().session.username

    val playerCache: Hashtable<String, Resident> = Hashtable()
    // keep a cache of all towns and nations for /locate, a full object cache is not needed yet.
    // spare some ram.
    val townCache: MutableList<String> = mutableListOf()

    val nationCache: MutableList<String> = mutableListOf()

    val nearbyTowns: MutableList<Town> = mutableListOf()

    var cacheRunning: Boolean = false
        private set

    override fun enable() {
        if (cacheRunning) return
        cacheRunning = true

        scope.launch {
            while (true) {
                delay(10.minutes)
                runTask()
            }
        }

        scope.launch {
            while (true) {
                delay(1.minutes)
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

        nearbyTowns.clear()
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

        val resp = MapApi.getNearby(
            body
        ).getOrNull()
            ?.take(3)
            ?.mapNotNull { it.name }

        if (resp.isNullOrEmpty()) return

        val towns = TownyAPI.getTowns(resp)
            .first()
            .onError {
                handleCacheError("nearbyTownCache", it.message)
            }
            .getOrNull() ?: listOf()

        nearbyTowns.addAll(towns)

    }

    /**
     * Update town and nation caches.
     * */
    suspend fun updateCache() {
        townCache.clear()
        nationCache.clear()

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

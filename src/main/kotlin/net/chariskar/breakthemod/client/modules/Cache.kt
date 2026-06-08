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
import net.chariskar.breakthemod.client.utils.Schedule
import net.chariskar.breakthemod.client.utils.Scheduler
import net.chariskar.breakthemod.client.widgets.NearbyTowns
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.breakthebot.breakthelibrary.api.MapApi
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.*
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Cache update handler for the mod.
 *
 * @property playerCache The player currently cached in memory.
 * @property townCache A list of every town from /towns.
 * @property nationCache A list of every nation from /nations.
 *  */
object Cache : BaseModule(
    "Cache",
    "Cache handler for the mod."
) {

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
            Scheduler.scheduleRepeating(
                Schedule(
                    "playerCacheUpdate",
                    {
                        if (!enabled) return@Schedule
                        runTask()
                    },
                    10.minutes
                )
            )
            Scheduler.scheduleRepeating(
                Schedule(
                    "nearbyTownCache",
                    {
                        if (!enabled) return@Schedule
                        updateNearbyTowns()
                    },
                    30.seconds
                )
            )
        })

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPlayNetworkHandler?, _: MinecraftClient? ->
            enabled = false
            Scheduler.cancel("playerCacheUpdate")
            Scheduler.cancel("nearbyTownCache")
        })

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
        if (!isEarthMc() || !Config.features.cacheEnabled) return
        updateCache()
        updatePlayers()
    }

    fun getPlayer(
        name: String
    ): Resident?  = playerCache[name]
}

fun Resident.getTownyText(): Text {
    if (town == null || status == null) return Text.literal("Nomad").formatted(Formatting.DARK_AQUA)
    val text = Text.empty()

    if (status?.isMayor == true) {
        val colour = if (status?.isKing == true) Formatting.GOLD else Formatting.DARK_AQUA
        text.append(Text.literal("\uD83D\uDC51 ").formatted(colour))
    }
    text.append(Text.of("[").copy().formatted(Formatting.GRAY))
    if (status?.hasNation == true) {
        text.append(Text.literal(nation?.name).formatted(Formatting.GOLD))
        text.append(Text.literal("|").formatted(Formatting.GRAY))
    }
    text.append(Text.literal(town?.name).formatted(Formatting.DARK_AQUA))
    text.append(Text.literal("]").formatted(Formatting.GRAY))
    return text
}
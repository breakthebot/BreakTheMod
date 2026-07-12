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

import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Schedule
import net.chariskar.breakthemod.client.utils.Scheduler
import net.chariskar.breakthemod.client.widgets.NearbyTowns
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import org.breakthebot.breakthelibrary.api.MapAPI
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.NearbyItem
import org.breakthebot.breakthelibrary.models.NearbyType
import org.breakthebot.breakthelibrary.models.Resident
import org.breakthebot.breakthelibrary.models.Town
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Cache update handler for the mod.
 *
 * @property playerCache The players currently cached in memory.
 * @property townCache A list of every town from /towns.
 * @property nationCache A list of every nation from /nations.
 *  */
object Cache : BaseModule(
    "Cache",
    "Cache handler for the mod."
) {
    private val _playerCache: HashMap<String, Resident> = HashMap()

    val playerCache: HashMap<String, Resident>
        get() = _playerCache

    // keep a cache of all towns and nations for /locate, a full object cache is not needed yet.
    // spare some ram.
    val townCache: List<String>
        field: MutableList<String> = mutableListOf()

    val nationCache: List<String>
        field: MutableList<String> = mutableListOf()

    val nearbyTowns: List<Town>
        field: MutableList<Town> = mutableListOf()

    override fun enable() {
        if (enabled) return

        ClientPlayConnectionEvents.JOIN.register(
            ClientPlayConnectionEvents.Join { _: ClientPacketListener?, _: PacketSender?, _: Minecraft ->
            enabled = true
            Scheduler.scheduleRepeating(
                Schedule(
                    "playerCacheUpdate",
                    {
                        logInfo("Cache started.")
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

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _: ClientPacketListener?, _: Minecraft? ->
            enabled = false
            Scheduler.cancel("playerCacheUpdate")
            Scheduler.cancel("nearbyTownCache")
        })

    }
    
    private suspend fun updatePlayers() {
        playerCache.clear()

        val players = client.connection!!
            .onlinePlayers
            .map { it.profile.id.toString() }

        val apiPlayers = TownyAPI.getPlayers(players)
            .flatMap { it
                .logError()
                .getOrNull()
                .orEmpty()
            }

        apiPlayers.forEach {
            playerCache[it.name] = it
        }
        logDebug("Finished updating players.")
    }

    /**
     * Updates [Cache.nearbyTowns].
     *
     * World required.
     * */
    suspend fun updateNearbyTowns() {
        if (!isEarthMc() || !Config.features.cacheEnabled || !NearbyTowns.config.enabled) return

        nearbyTowns.clear()
        val player = Minecraft.getInstance().player ?: return

        val body = NearbyItem.NearbyItemCoordinates(
            targetType = NearbyType.COORDINATE,
            searchType = NearbyType.TOWN,
            radius = 500,
            target = listOf(player.x.toInt(), player.z.toInt())
        )

        val resp = MapAPI.getNearby(body)
            .logError()
            .getOrNull()
            ?.take(3)
            ?.map { it.name }

        if (resp.isNullOrEmpty()) return

        val towns = TownyAPI.getTowns(resp)
            .first()
            .logError()
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
            .onSuccess { townCache.addAll(it.map { t -> t.name }) }
            .logError()

        TownyAPI.getAllNations()
            .onSuccess { nationCache.addAll(it.map { n -> n.name }) }
            .logError()

        logDebug("Name cache finished.")
    }

    suspend fun runTask() {
        if (!isEarthMc() || !Config.features.cacheEnabled || Breakthemod.debug) return
        updateCache()
        updatePlayers()
    }

    fun getPlayer(
        name: String
    ): Resident?  = playerCache[name]
}

fun Resident.getTownyComponent(): Component {
    if (town == null) return Component.literal("Nomad").withColor(TextColor.DARK_AQUA)
    val text = Component.empty()

    if (status.isMayor) {
        val colour = if (status.isKing) TextColor.GOLD else TextColor.DARK_AQUA
        text.append(Component.literal("\uD83D\uDC51 ").withColor(colour))
    }
    text.append(Component.literal("[").withColor(TextColor.GRAY))
    if (status.hasNation) {
        text.append(Component.literal(nation?.name!!).withColor(TextColor.GOLD))
        text.append(Component.literal("|").withColor(TextColor.GRAY))
    }

    text.append(Component.literal(town?.name!!).withColor(TextColor.DARK_AQUA))
    text.append(Component.literal("]").withColor(TextColor.GRAY))
    return text
}
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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import org.breakthebot.breakthelibrary.api.TownyAPI
import org.breakthebot.breakthelibrary.models.Resident
import kotlin.time.Duration.Companion.minutes

/**
 * Cache update handler for the mod.
 *
 * @property playerCache The players currently cached in memory.
 * @property townNameCache A list of every town from /towns.
 * @property nationNameCache A list of every nation from /nations.
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
    val townNameCache: List<String>
        field: MutableList<String> = mutableListOf()

    val nationNameCache: List<String>
        field: MutableList<String> = mutableListOf()

    val alliances: Set<String>
        field: MutableSet<String> = mutableSetOf()

    override fun enable() {
        if (enabled) return

        ClientPlayConnectionEvents.JOIN.register(
            ClientPlayConnectionEvents.Join { _: ClientPacketListener?, _: PacketSender?, _: Minecraft ->
                enabled = true
                Scheduler.scheduleRepeating(
                    Schedule(
                        "playerCacheUpdate",
                        {
                            info("Cache started.")
                            if (!enabled) return@Schedule
                            runTask()
                        },
                        3.minutes
                    )
                )
            }
        )

        ClientPlayConnectionEvents.DISCONNECT.register(
            ClientPlayConnectionEvents.Disconnect { _: ClientPacketListener?, _: Minecraft? ->
                enabled = false
                Scheduler.cancel("playerCacheUpdate")
            }
        )
    }

    private suspend fun updatePlayers() {
        playerCache.clear()

        val players = client.connection!!
            .onlinePlayers
            .map { it.profile.id.toString() }

        val apiPlayers = TownyAPI.getPlayers(players)
            .flatMap {
                it
                    .error()
                    .getOrNull()
                    .orEmpty()
            }

        apiPlayers.forEach {
            playerCache[it.name] = it
        }
        debug("Finished updating players.")
    }

    /**
     * Update town and nation caches.
     * */
    suspend fun updateCache() {
        townNameCache.clear()
        nationNameCache.clear()

        TownyAPI.getAllTowns()
            .onSuccess { townNameCache.addAll(it.map { t -> t.name }) }
            .error()

        TownyAPI.getAllNations()
            .onSuccess { nationNameCache.addAll(it.map { n -> n.name }) }
            .error()

        TownyAPI.getAllAlliances()
            .onSuccess { alliances.addAll(it.keys) }
            .error()

        debug("Name cache finished.")
    }

    suspend fun runTask() {
        if (!isModEnabled() || !Config.features.cacheEnabled || !Breakthemod.debug) return
        updateCache()
        updatePlayers()
    }

    fun getPlayer(
        name: String,
    ): Resident? = playerCache[name]
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

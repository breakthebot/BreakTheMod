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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.module.BaseModule
import net.chariskar.breakthemod.client.utils.Config
import org.breakthebot.breakthelibrary.api.ServerAPI
import java.io.File
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

@Serializable
data class StaffCacheFile(
    val staff: Map<String, List<Uuid>>,
    val timestamp: Instant,
)

object StaffCacheHandler : BaseModule("StaffCacheHandler", "Handles the staff file caching.", true) {

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    val staffFile = File(Breakthemod.modConfig.toString(), "cache/staffcache.json")

    override fun enable() {
        runBlocking { storeStaff(ServerAPI.getStaff()) }
    }

    fun storeStaff(staff: Map<String, List<UUID>>): Map<String, List<UUID>> {
        debug("Updating staff cache file.")
        staffFile.writeText(
            json.encodeToString<StaffCacheFile>(
                StaffCacheFile(
                    staff.mapValues { it.value.map { u -> u.toKotlinUuid() } },
                    Clock.System.now()
                )
            )
        )
        return staff
    }

    suspend fun getStaff(): Map<String, List<UUID>> {
        if (!Config.features.staffListCache) return ServerAPI.getStaff()

        val text = staffFile.readText()

        if (text.isEmpty() || text == "{}") {
            return storeStaff(ServerAPI.getStaff())
        }

        val parsed = runCatching {
            json.decodeFromString<StaffCacheFile>(text)
        }.getOrNull()

        if (parsed == null) {
            return storeStaff(ServerAPI.getStaff())
        }

        return parsed.staff.mapValues {
            it.value.map { v -> UUID.fromString(v.toString()) }
        }
    }
}

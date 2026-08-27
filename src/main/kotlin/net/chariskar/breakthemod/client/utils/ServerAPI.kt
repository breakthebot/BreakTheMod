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

package net.chariskar.breakthemod.client.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.providers.LoggingProvider
import org.breakthebot.breakthelibrary.api.APIClient
import org.breakthebot.breakthelibrary.api.BaseServerAPI
import java.io.File
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

@Serializable
data class StaffCacheFile(
    val staff: Map<String, List<Uuid>>,
    val timestamp: Instant,
)

object ServerAPI : BaseServerAPI(APIClient) {

    val staffFile = File(Breakthemod.modConfig.toString(), "cache/staffcache.json")
    val logger = LoggingProvider("ServerAPI")

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    val staffCacheFileLifetime = 7.days

    fun storeStaff(staff: Map<String, List<UUID>>): Map<String, List<UUID>> {
        logger.debug("Updating staff cache file.")
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

    override suspend fun getStaff(): Map<String, List<UUID>> = withContext(Dispatchers.IO) {
        if (!staffFile.exists()) {
            logger.debug("Staff cache file does not exist, will be created on next request.")

            staffFile.parentFile?.mkdirs()
            staffFile.createNewFile()
            staffFile.writeText("{}")
        }

        val fileContent = staffFile.readText()

        if (fileContent.isEmpty() || fileContent.length <= 2) {
            return@withContext storeStaff(super.getStaff())
        }

        if (!Config.features.staffListCache) {
            return@withContext storeStaff(super.getStaff())
        }

        runCatching {
            val cached = json.decodeFromString<StaffCacheFile>(fileContent)

            if (Clock.System.now() - cached.timestamp > staffCacheFileLifetime) {
                throw Exception("Outdated cache file.")
            }

            cached.staff.mapValues { (_, uuids) ->
                uuids.map { UUID.fromString(it.toString()) }
            }
        }.getOrElse {
            val staff = super.getStaff()
            storeStaff(staff)
            staff
        }
    }
}

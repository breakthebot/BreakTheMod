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
package net.chariskar.breakthemod.client.api

import com.google.gson.Gson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.coroutines.*
import net.chariskar.breakthemod.client.api.types.Nation
import net.chariskar.breakthemod.client.api.types.Resident
import net.chariskar.breakthemod.client.api.types.Town
import net.chariskar.breakthemod.client.utils.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class Fetch private constructor() {
    val gson: Gson = Gson()

    companion object {
        val logger: Logger = LoggerFactory.getLogger("breakthemod")
        val gson: Gson = Gson()

        val urls = mapOf(
            "towns" to "${Config.getApiUrl()}/towns",
            "nations" to "${Config.getApiUrl()}/nations",
            "players" to "${Config.getApiUrl()}/players",
            "nearby" to "${Config.getApiUrl()}/nearby",
            "discord" to "${Config.getApiUrl()}/discord",
            "staff" to Config.getStaffUrl()
        )

        /**
         * Send a get request.
         * @generic T the type to infer response to.
         * @param url the url to send the request to.
         */
        suspend inline fun <reified T> getRequest(url: String): T? {
            try {
                val client = HttpClient.newHttpClient()
                val request = HttpRequest.newBuilder()
                    .uri(URI(formatUrl(url)))
                    .header("Content-Type", "application/json")
                    .build()

                val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                return Json.decodeFromString<T>(response.body())
            } catch (e: Exception) {
                logError("Unable to send get request to target", e)
                return null
            }
        }
        /**
         * Sends a post request.
         * @generic T the type to infer the response into
         * @param url the url to send the request to
         * @param body The body to attach to the url
         */
        suspend inline fun <reified T> postRequest(url: String, body: String): T? {
             try {
                 val uuids = body.removePrefix("[").removeSuffix("]").split(",").map { it.trim().removeSurrounding("\"") }

                 val jsonBody = buildJsonObject {
                     put("query", JsonArray(uuids.map { JsonPrimitive(it) }))
                 }


                 val client = HttpClient.newHttpClient()
                 val request = HttpRequest.newBuilder()
                     .uri(URI(formatUrl(url)))
                     .header("Content-Type", "application/json")
                     .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                     .build()

                 val response = client.send(request, HttpResponse.BodyHandlers.ofString())

                 return Json.decodeFromString<T>(response.body())

             } catch (e: Exception) {
                 logError("Unable to send request to target", e)
                 return null
             }
        }

        fun logError(message: String?, e: java.lang.Exception) {
            logger.error("{}{}", message, e.message)
            if (Config.getDevMode()) {
                e.printStackTrace()
            }
        }

        fun formatUrl(url: String): String {
            if (url.isEmpty()) return url

            val protocolEnd = url.indexOf("://")
            if (protocolEnd == -1) return url

            val protocol = url.take(protocolEnd + 3)
            var rest = url.substring(protocolEnd + 3)

            rest = rest.replace("/{2,}".toRegex(), "/")

            if (rest.endsWith("/") && rest.length > 1) {
                rest = rest.dropLast(1)
            }

            return protocol + rest
        }

        @Volatile
        private var INSTANCE: Fetch? = null

        fun getInstance(): Fetch {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Fetch().also { INSTANCE = it }
            }
        }
    }

    /**
     * List of items that can be looked up from the api, mapped to the url of each.
     */
    enum class ItemTypes(val url: String) {
        TOWN(urls["towns"]!!),
        NATION(urls["nations"]!!),
        PLAYER(urls["players"]!!),
        NEARBY(urls["nearby"]!!),
        DISCORD(urls["discord"]!!),
        STAFF(urls["staff"]!!)
    }

    /**
     * Sends a post request to the api.
     * @generic T the type to infer the response into
     * @param item The type of the item to fetch.
     * @param body The body to send
     */
    suspend inline fun <reified T : Any> getObjects(item: ItemTypes, body: String): List<T>? {
        return try {
            val url: String = item.url
            postRequest<List<T>>(url, body)
        } catch (e: Exception) {
            logError("Unable to fetch item", e)
            null
        }
    }

    suspend fun getResidents(residents: List<String>): List<Resident?>? {
        return getObjects(Fetch.ItemTypes.PLAYER, residents.toString())
    }

    suspend fun getResident(resident: String): Resident? {
        val resident = getResidents(arrayListOf(resident))
        if (resident.isNullOrEmpty()) return null
        return resident[0]
    }

    suspend fun getTowns(towns: List<String>): List<Town?>? {
        return getObjects(Fetch.ItemTypes.TOWN, towns.toString())
    }

    suspend fun getTown(town: String): Town? {
        val town = getTowns(arrayListOf(town))
        if (town.isNullOrEmpty()) return null
        return town[0]
    }

    suspend fun getNations(nations: List<String>): List<Nation?>? {
        return getObjects(Fetch.ItemTypes.NATION, nations.toString())
    }

    suspend fun getNation(nation: String): Nation? {
        val nation = getNations(arrayListOf(nation))
        if (nation.isNullOrEmpty()) return null
        return nation[0]
    }

    /**
     * Sends a get request to the api.
     * @generic T the type to infer the response into
     * @param item The type of the item to fetch.
     */
    suspend inline fun <reified T : Any> getAll(item: ItemTypes): List<T>? {
        return try {
            val url: String = item.url
            getRequest<List<T>?>(url)
        } catch (e: Exception) {
            logError("Unable to fetch item", e)
            null
        }
    }


}
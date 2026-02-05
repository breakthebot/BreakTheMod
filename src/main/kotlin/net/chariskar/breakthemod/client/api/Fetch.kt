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

import kotlinx.coroutines.future.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import net.chariskar.breakthemod.client.objects.Nation
import net.chariskar.breakthemod.client.objects.Resident
import net.chariskar.breakthemod.client.objects.Town
import net.chariskar.breakthemod.client.utils.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


object Fetch {
    val logger: Logger = LoggerFactory.getLogger("breakthemod")

    val json: Json = Json {
        ignoreUnknownKeys = true
    }

    val client: HttpClient = HttpClient.newHttpClient()

    val urls = mapOf(
        "towns" to "${Config.getApiUrl()}/towns",
        "nations" to "${Config.getApiUrl()}/nations",
        "players" to "${Config.getApiUrl()}/players",
        "nearby" to "${Config.getApiUrl()}/nearby",
        "discord" to "${Config.getApiUrl()}/discord",
        "location" to "${Config.getApiUrl()}/location",
        "staff" to Config.getStaffUrl()
    )

    /**
     * Send a get request.
     * @generic T the type to infer response to.
     * @param url the url to send the request to.
     */
    suspend inline fun <reified T> getRequest(url: String): T? {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI(formatUrl(url)))
                .header("Content-Type", "application/json")
                .build()
            val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()
            val body = response.body()
            if (T::class == String::class) {
                body as T
            } else {
                json.decodeFromString<T>(body)
            }
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
            val request = HttpRequest.newBuilder()
                .uri(URI(formatUrl(url)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build()

            val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()
            var body = response.body()
            if (body.startsWith("[[") && body.endsWith("]]")) {
                body = body.substring(1, body.length - 1)
            }
            return if (T::class == String::class) {
                body as T
            } else {
                json.decodeFromString<T>(body)
            }
        } catch (e: Exception) {
            logError("Unable to send request to target", e)
            return null
        }
    }

    /**
     * Sends a post request.
     * only to be used for uuids/names.
     * @generic T the type to infer the response into
     * @param url the url to send the request to
     * @param body The body to attach to the url
     */
    suspend inline fun <reified T> getUUIDs(url: String, body: String): T? {
        try {
            val uuids = body.removePrefix("[").removeSuffix("]").split(",").map { it.trim().removeSurrounding("\"") }
            val jsonBody = buildJsonObject {
                put("query", JsonArray(uuids.map { JsonPrimitive(it) }))
            }
            return postRequest<T>(url, jsonBody.toString())
        } catch (e: Exception) {
            logError("Unable to send request to target", e)
            return null
        }
    }

    /**
     * Sends a get request to the api.
     * @generic T the type to infer the response into
     * @param item The type of the item to fetch.
     */
    @Suppress("unused")
    suspend inline fun <reified T : Any> getAll(item: ItemTypes): List<T>? {
        return try {
            val url: String = item.url
            getRequest<List<T>?>(url)
        } catch (e: Exception) {
            logError("Unable to fetch item", e)
            null
        }
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
            getUUIDs<List<T>>(url, body)
        } catch (e: Exception) {
            logError("Unable to fetch item", e)
            null
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
        STAFF(urls["staff"]!!),
        LOCATION(urls["location"]!!)
    }
    
    suspend fun getResidents(residents: List<String>): List<Resident?>? = getObjects(ItemTypes.PLAYER, residents.toString())

    suspend fun getResident(resident: String): Resident? = getResidents(arrayListOf(resident))?.get(0)

    suspend fun getTowns(towns: List<String>): List<Town?>? = getObjects(ItemTypes.TOWN, towns.toString())

    suspend fun getTown(town: String): Town? = getTowns(arrayListOf(town))?.get(0)

    suspend fun getNations(nations: List<String>): List<Nation?>? = getObjects(ItemTypes.NATION, nations.toString())

    suspend fun getNation(nation: String): Nation? = getNations(arrayListOf(nation))?.get(0)


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
}
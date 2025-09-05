/*
 * This file is part of breakthemodRewrite.
 *
 * breakthemodRewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemodRewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemodRewrite. If not, see <https://www.gnu.org/licenses/>.
 */

package net.chariskar.breakthemod.client.api

import kotlinx.serialization.json.Json
import net.chariskar.breakthemod.client.api.types.town
import net.chariskar.breakthemod.client.utils.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class Fetch {
    companion object {
        val logger: Logger = LoggerFactory.getLogger("breakthemod")

        val urls = mapOf(
            "towns" to "${Config.getApiUrl()}/towns",
            "nations" to "${Config.getApiUrl()}/nations",
            "players" to "${Config.getApiUrl()}/players",
            "nearby" to "${Config.getApiUrl()}/nearby",
            "discord" to "${Config.getApiUrl()}/discord"
        )

        public data class ItemType<T>(
            val url: String,
            val type: T
        )

        /**
         * List of items that can be looked up from the api, mapped to the url of each.
         */
        public enum class ItemTypes(
            TOWN: ItemType<town> = ItemType(urls["towns"]!!, town())
        )

        /**
         * Send a get request.
         * @generic T the type to infer response to.
         * @param url the url to send the request to.
         */
        inline fun <reified T> getRequest(url: String): T? {
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
        inline fun <reified T> postRequest(url: String, body: String): T? {
             try {
                 val client = HttpClient.newHttpClient()
                 val request = HttpRequest.newBuilder()
                     .uri(URI(formatUrl(url)))
                     .header("Content-Type", "application/json")
                     .POST(HttpRequest.BodyPublishers.ofString(body))
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

            val protocol = url.substring(0, protocolEnd + 3)
            var rest = url.substring(protocolEnd + 3)

            rest = rest.replace("/{2,}".toRegex(), "/")

            if (rest.endsWith("/") && rest.length > 1) {
                rest = rest.dropLast(1)
            }

            return protocol + rest
        }

    }

    /**
     * Sends a post request.
     * @generic T the type to infer the response into
     * @param itemType The type of the item to fetch.
     */
    inline fun <reified T> getObject(itemType: ItemTypes): List<T?>? {
        try {

        } catch (e: Exception) {
            logError("Unable to fetch item", e)
            return null
        }
    }
}
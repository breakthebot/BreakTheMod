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

import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlinx.serialization.json.Json
import org.breakthebot.breakthelibrary.utils.Urls

object Config {
    var config: ConfigData = ConfigData()



    private val json = Json { encodeDefaults = true; prettyPrint = true }
    lateinit var configFile: File
    val logger: Logger = LoggerFactory.getLogger("breakthemod")

    fun setFile(file: File) {
        configFile = file
    }

    fun loadConfig() {
        if (MinecraftClient.getInstance() == null) return

        if (!configFile.exists()) { saveConfig(null) }

        val fileContent = configFile.readText()
        if (fileContent.isEmpty() || fileContent.length <= 2) {
            saveConfig(null)
        }
        try {
            config = Json.decodeFromString<ConfigData>(fileContent)
        } catch (e: Exception) {
            logger.error("Unable to parse config file regenerating, ${e.message}")
            saveConfig(null)
        }
    }

    fun saveConfig(data: ConfigData?) {
        val data = data ?: ConfigData()
        try {
            val encoded= json.encodeToString<ConfigData>(data)
            configFile.writeText(encoded)
        } catch (e: Exception) {
            logger.error("Unable to write new config, ${e.message}")
        }
    }

    /**
     * Small function to have the proper URL.
     */
    fun formatURL(url: String): String {
        val withProtocol = if (!url.startsWith("https://")) "https://$url" else url
        return if (!withProtocol.endsWith("/")) "$withProtocol/" else withProtocol
    }

    fun getMapUrl(): String = formatURL(config.urls.mapUrl)

    fun getDevMode(): Boolean = config.dev

    fun getRadar(): Boolean = config.radarEnabled

    fun getWidget(): Widget = config.widget

    fun getEnabledServers(): Boolean = config.enabledOnOtherServers

    fun getTownlessMessage(townName: String): String = config.townlessMessage.replace("TOWN", townName)

    fun getDbg() = config.debug

    fun getHud() = config.hudType

    fun getNameTag() = config.nametagInfo && config.cacheEnabled

    fun getCache() = config.cacheEnabled

    fun setTownlessMessage(message: String): Boolean {
        if (!message.contains("TOWN")) return false
        config.townlessMessage = message
        return true
    }

    fun setApiUrl(apiUrl: String) {
        val oldUrls = config.urls
        config.urls = Urls(
            apiUrl,
            oldUrls.mapUrl,
            oldUrls.staffUrl
        )
    }

    fun setMapUrl(mapUrl: String) {
        val oldUrls = config.urls
        config.urls = Urls(
            oldUrls.apiUrl,
            mapUrl,
            oldUrls.staffUrl
        )
    }

    fun setStaffUrl(staffUrl: String) {
        val oldUrls = config.urls
        config.urls = Urls(
            oldUrls.apiUrl,
            oldUrls.mapUrl,
            staffUrl
        )
    }


}
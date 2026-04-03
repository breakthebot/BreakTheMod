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

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlinx.serialization.json.Json
import org.breakthebot.breakthelibrary.utils.Urls

object Config {
    var config: ConfigData = ConfigData()

    @Serializable
    data class ConfigData(
        var dev: Boolean = false,
        var enabledOnOtherServers: Boolean = true,
        var radarEnabled: Boolean = true,
        var debug: Boolean = false,
        var xaerosRdr: Boolean = false,
        var widget: Widget = Widget(),
        var hudType: AutoHudType = AutoHudType.None,
        var nametagInfo: Boolean = true,
        var cacheEnabled: Boolean = true,
        var townlessMessage: String = "Hi! I see you're new here, wanna join my Town? I can help you out! Get Free enchanted Armor, Pickaxe, Diamonds, Iron, wood, food, stone, house, and ability to teleport! Type /t join TOWN",
        var options: Boolean = false,
        @Contextual
        var urls: Urls = Urls()
    )

    @Serializable
    enum class AutoHudType {
        None,
        MapHud,
        PermHud
    }

    @Serializable
    data class Widget(
        var customX: Int = 0,
        var customY: Int = 0,
        var entryHeight: Int = 15,
        var margin: Int = 10,
        var widgetPosition: WidgetPosition = WidgetPosition.TOP_LEFT
    )

    private val json = Json { encodeDefaults = true; prettyPrint = true }
    val configFile: File = File(MinecraftClient.getInstance()?.runDirectory, "config/breakthemod_config.json")
    val logger: Logger = LoggerFactory.getLogger("breakthemod")

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

    fun getXaerosRdr() = config.xaerosRdr

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

    @Serializable
    enum class WidgetPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        CUSTOM
    }
}
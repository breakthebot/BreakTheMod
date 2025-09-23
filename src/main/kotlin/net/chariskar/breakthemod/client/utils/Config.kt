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

package net.chariskar.breakthemod.client.utils

import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlinx.serialization.json.Json

class Config private constructor(){
    var config: ConfigData? = null

    @Serializable
    data class ConfigData(
        var dev: Boolean = false,
        var enabledOnOtherServers: Boolean = true,
        var radarEnabled: Boolean = true,
        var mapUrl: String = "https://map.earthmc.net/",
        var apiUrl: String = "https://api.earthmc.net/v3/aurora",
        var staffRepoUrl: String = "https://raw.githubusercontent.com/jwkerr/staff/master/staff.json",
        var customX: Int = 0,
        var customY: Int = 0,
        var widgetPosition: WidgetPosition = WidgetPosition.TOP_LEFT,
        var townlessMessage: String = "Hi! I see you're new here, wanna join my Town? I can help you out! Get Free enchanted Armor, Pickaxe, Diamonds, Iron, wood, food, stone, house, and ability to teleport! Type /t join TOWN"
    )

    data class Widget(
        var customX: Int = 0,
        var customY: Int = 0,
        var widgetPosition: WidgetPosition = WidgetPosition.TOP_LEFT
    )

    // static methods/properties go here.
    companion object {
        private val json = Json { encodeDefaults = true }

        val configFile: File = File(MinecraftClient.getInstance().runDirectory, "config/breakthemod_config.json")
        val logger: Logger = LoggerFactory.getLogger("breakthemod")


        private var instance: Config? = null

        fun getInstance(): Config = instance ?: synchronized(this) {
            if (instance == null) {
                instance = Config().apply {
                    config = loadConfig()
                }
            }
            instance!!
        }


        fun loadConfig(): ConfigData {
            if (configFile.exists()) {
                val fileContent = configFile.readText()

                if (fileContent.isEmpty() || fileContent.length <= 2) {
                    saveConfig(null)
                    return ConfigData()
                }
                try {
                    return Json.decodeFromString<ConfigData>(fileContent)
                } catch (e: Exception) {
                    logger.error("Unable to parse config file regenerating, ${e.message}")
                    saveConfig(null)
                }

            } else {
                saveConfig(null)
            }
            return ConfigData()
        }


        fun saveConfig(data: ConfigData?) {
            val data = data ?: ConfigData()
            try {
                val encoded: String = json.encodeToString<ConfigData>(data)
                configFile.writeText(encoded )

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


        fun getMapUrl(): String = formatURL(getInstance().config?.mapUrl ?: ConfigData().mapUrl)

        fun getApiUrl(): String = formatURL(getInstance().config?.apiUrl ?: ConfigData().apiUrl)

        fun getStaffUrl(): String = formatURL(getInstance().config?.staffRepoUrl ?: ConfigData().staffRepoUrl)

        fun getDevMode(): Boolean = getInstance().config?.dev ?: false

        fun getRadar(): Boolean = getInstance().config?.radarEnabled ?: true

        fun getEnabledServers(): Boolean = getInstance().config?.enabledOnOtherServers ?: true

        fun getWidgetPos(): Widget = Widget(
            getInstance().config?.customX ?: 0,
            getInstance().config?.customY ?: 0,
            getInstance().config?.widgetPosition ?: WidgetPosition.TOP_LEFT
        )

        fun getTownlessMessage(townName: String): String {
            return getInstance().config?.townlessMessage?.replace("TOWN", townName)!!
        }

        fun setTownlessMessage(message: String): Boolean {
            if (!message.contains("TOWN")) return false
            getInstance().config?.townlessMessage = message
            return true
        }
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
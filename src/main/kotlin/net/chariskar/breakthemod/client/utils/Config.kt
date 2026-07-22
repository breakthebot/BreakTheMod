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

import kotlinx.serialization.json.Json
import net.chariskar.breakthemod.client.models.ConfigData
import net.chariskar.breakthemod.client.models.WidgetConfig
import org.breakthebot.breakthelibrary.utils.ConfigHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import org.breakthebot.breakthelibrary.utils.Config as LConfig

/**
 * Config handler.
 *
 * @property configFile The config file.
 * @property json The JSON parser for the config.
 * @property features Lazy access to config.features.
 * @property libraryConfig Lazy access config.libraryConfig.
 * @property widgets Lazy access to config.widgets.
 * */
object Config {
    lateinit var configFile: File

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    var config: ConfigData = ConfigData()

    val widgets
        get() = config.widgets

    val features
        get() = config.features

    val notifications
        get() = config.notifications

    val libraryConfig
        get() = config.libraryConfig

    val logger: Logger = LoggerFactory.getLogger("breakthemod")

    fun setFile(file: File) {
        configFile = file
    }

    fun loadConfig() {
        if (!configFile.exists()) {
            saveConfig(null)
        }

        val fileContent = configFile.readText()
        if (fileContent.isEmpty() || fileContent.length <= 2) {
            saveConfig(null)
        }
        try {
            config = json.decodeFromString<ConfigData>(fileContent)
            ConfigHandler.setup(libraryConfig)
        } catch (e: Exception) {
            logger.error("Encountered an exception when trying to parse the config: ${e.message}")
            logger.warn("Regenerating config.")
            saveConfig(null)
        }
    }

    fun saveConfig(data: ConfigData?) {
        val data = data ?: ConfigData()
        try {
            val encoded = json.encodeToString<ConfigData>(data)
            configFile.writeText(encoded)
        } catch (e: Exception) {
            logger.error("Unable to write new config, ${e.message}")
        }
    }

    /**
     * Update the config of the specified widget.
     * @param name The name of the widget the config is being updated for.
     * @param newConfig The updated widget config.
     * */
    fun saveWidgetConfig(
        name: String,
        newConfig: WidgetConfig,
    ) {
        widgets[name] = newConfig
        saveConfig(config)
    }

    /**
     * Get the config of a widget.
     * @param name The name of the widget.
     * */
    fun getWidgetConfig(
        name: String,
    ): WidgetConfig? = widgets[name]

    /**
     * Small function to have the proper URL.
     */
    fun formatURL(url: String): String {
        val withProtocol = if (!url.startsWith("https://")) "https://$url" else url
        return if (!withProtocol.endsWith("/")) "$withProtocol/" else withProtocol
    }

    fun getMapUrl() = formatURL(config.libraryConfig.mapUrl)

    fun getTownlessMessage(townName: String) = config.townlessMessage.replace("TOWN", townName)

    fun getNameTag() = config.features.nameTagInfo && config.features.cacheEnabled

    fun setTownlessMessage(message: String): Boolean {
        if (!message.contains("TOWN")) return false
        config.townlessMessage = message
        return true
    }

    fun setApiUrl(apiUrl: String) {
        val oldUrls = config.libraryConfig
        config.libraryConfig = LConfig(
            apiUrl,
            oldUrls.mapUrl,
            oldUrls.staffUrl
        )
        ConfigHandler.setup(config.libraryConfig)
    }

    fun setMapUrl(mapUrl: String) {
        val oldUrls = config.libraryConfig
        config.libraryConfig = LConfig(
            oldUrls.apiUrl,
            mapUrl,
            oldUrls.staffUrl
        )
        ConfigHandler.setup(config.libraryConfig)
    }

    fun setStaffUrl(staffUrl: String) {
        val oldUrls = config.libraryConfig
        config.libraryConfig = LConfig(
            oldUrls.apiUrl,
            oldUrls.mapUrl,
            staffUrl
        )
        ConfigHandler.setup(config.libraryConfig)
    }
}

fun Config.save() {
    Config.saveConfig(this.config)
}

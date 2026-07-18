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

package net.chariskar.breakthemod.client.models

import kotlinx.serialization.Serializable
import net.chariskar.breakthemod.client.api.widget.WidgetModes
import net.chariskar.breakthemod.client.utils.Config
import org.breakthebot.breakthelibrary.utils.Config as LConfig

@Serializable
data class ConfigData(
    var dev: Boolean = false,
    var enabledOnOtherServers: Boolean = true,
    var townlessMessage: String = "Hi! I see you're new here, wanna join my Town? I can help you out! Get Free enchanted Armor, Pickaxe, Diamonds, Iron, wood, food, stone, house, and ability to teleport! Type /t join TOWN",
    var options: Boolean = false,
    var features: Features = Features(),
    var widgets: MutableMap<String, WidgetConfig> = mutableMapOf(),
    var libraryConfig: LConfig = LConfig(
        "https://api.earthmc.net/v4",
        "https://map.earthmc.net",
        "https://api.earthmc.net/v4"
    ),
    var widgetMode: WidgetModes = WidgetModes.General,
    val notifications: MutableMap<String, Boolean> = mutableMapOf()
) {
    override fun toString(): String {
        return Config.json.encodeToString(this)
    }
}

@Serializable
enum class AutoHudType {
    None,
    MapHud,
    PermHud
}

@Serializable
data class Features(
    var hudType: AutoHudType = AutoHudType.None,

    var nameTagInfo: Boolean = true,
    var cacheEnabled: Boolean = true,
    var experienceComponent: Boolean = true,
    var widgetDataLife: Int = 5
)

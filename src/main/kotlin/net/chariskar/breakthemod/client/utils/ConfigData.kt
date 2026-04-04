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
import org.breakthebot.breakthelibrary.utils.Urls

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

@Serializable
enum class WidgetPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_RIGHT,
    BOTTOM_LEFT,
    CUSTOM
}
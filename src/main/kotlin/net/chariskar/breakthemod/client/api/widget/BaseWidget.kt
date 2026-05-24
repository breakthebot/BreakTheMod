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

package net.chariskar.breakthemod.client.api.widget

import kotlinx.serialization.Serializable
import net.chariskar.breakthemod.client.api.providers.LoggingProvider
import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

@Serializable
enum class WidgetPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    CUSTOM
}

@Serializable
enum class WidgetCategories {
    General,
    Mining,
    Fishing
}

@Serializable
data class Coords(
    var x: Int,
    var y: Int
)

/**
 * Data class for representing the configuration of a widget.
 * @property name The name of the widget.
 * @property coords The coords of the widget.
 * @property position The position of the widget.
 * @property category The category of the widget.
 * @property enabled The configured status of the widget.
 *  */
@Serializable
abstract class WidgetConfig {
    abstract val name: String
    abstract val category: WidgetCategories
    abstract var enabled: Boolean
    abstract var coords: Coords
    abstract var position: WidgetPosition
}

/**
 * Represents one of the renderable widgets in breakthemod.
 * @param name The name of the widget.
 * @param identifier The identifier of the widget.
 * */
abstract class BaseWidget(
    val name: String,
    val identifier: Identifier,
) : ServerUtilsProvider, LoggingProvider {
    protected val client: MinecraftClient = MinecraftClient.getInstance()

    inline fun <reified T : WidgetConfig> getConfig(): T? = Config.getWidgetConfig<T>(name)

    /** Registers the element after VanillaHudElements.CHAT.*/
    open fun register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, identifier) { context, _ -> render(context) }
    }

    /** Render entry point.
     * @param drawContext The draw context to use.
     * */
    abstract fun render(drawContext: DrawContext)
}
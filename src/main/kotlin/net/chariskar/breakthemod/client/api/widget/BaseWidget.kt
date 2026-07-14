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

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder
import net.chariskar.breakthemod.client.api.providers.LoggingProvider
import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider
import net.chariskar.breakthemod.client.models.WidgetConfig
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier

enum class WidgetPosition {
    TOP_LEFT,
    TOP_RIGHT,
    TOP_MIDDLE,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    MIDDLE_LEFT,
    MIDDLE_RIGHT
}

/**
 * Calculate the position of the widget.
 *
 * @param height The height of the widget.
 * @param width The width of the widget.
 * @param margin The margin that should be kept from the widget to the screen.
 * @param screenWidth The scaled width of the screen.
 * @param screenHeight The scaled height of the screen.
 * @return A [Coords] object that the widget should be drawn to.
 */
fun WidgetPosition.getPos(
    margin: Int,
    height: Int,
    width: Int,
    screenWidth: Int,
    screenHeight: Int
): Coords {
    return when (this) {
        WidgetPosition.TOP_LEFT -> {
            Coords(margin, margin)
        }
        WidgetPosition.TOP_RIGHT -> {
            Coords(
                screenWidth - width - margin,
                margin
            )
        }
        WidgetPosition.TOP_MIDDLE -> {
            Coords(
                (screenWidth - width) / 2,
                margin
            )
        }
        WidgetPosition.BOTTOM_LEFT -> {
            Coords(
                margin,
                screenHeight - height - margin
            )
        }
        WidgetPosition.BOTTOM_RIGHT -> {
            Coords(
                screenWidth - width - margin,
                screenHeight - height - margin
            )
        }
        WidgetPosition.MIDDLE_LEFT -> {
            Coords(
                margin,
                (screenHeight - height) / 2
            )
        }
        WidgetPosition.MIDDLE_RIGHT -> {
            Coords(
                screenWidth - width - margin,
                (screenHeight - height) / 2
            )
        }
    }
}

enum class WidgetModes {
    General,
    Mining,
    Fishing,
    Off
}

/** Iterate over the widget modes. */
fun WidgetModes.next(): WidgetModes {
    val entries = WidgetModes.entries
    return entries[(ordinal + 1) % entries.size]
}

/**
 * Represents the coordinates of a widget on the screen.
 * @param x The x coordinate of the widget.
 * @param y The y coordinate of the widget.
 * */
data class Coords(
    var x: Int,
    var y: Int
)

/**
 * Represents one of the renderable widgets in breakthemod.
 * @param name The name of the widget, must be underscored.
 * @property config The configuration of the widget, set by the widget.
 * @property margin The margin of the widget.
 * @property entryHeight The height of each entry on list widgets.
 * @property client Client access for the widgets.
 *  */
abstract class BaseWidget(
    val name: String,
) : ServerUtilsProvider, LoggingProvider(name) {

    protected val client: Minecraft
        get() = Minecraft.getInstance()

    abstract val config: WidgetConfig

    open val margin: Int = 10
    open val entryHeight: Int = 15

    /**
     * Checks if the widget should be rendered.
     * */
    open fun shouldNotRender(): Boolean {
        return !isModEnabled() || !config.enabled || client.gui.hud.isHidden
    }

    /**
     * Generate the ModMenu config for the widget.
     * @param category The sub-category the widget configuration will be generated in.
     * @param entryBuilder The entry builder.
     * */
    abstract fun getModMenuConfig(
        category: SubCategoryBuilder,
        entryBuilder: ConfigEntryBuilder
    )

    /** Registers the element after VanillaHudElements.CHAT.*/
    fun register() {
        HudElementRegistry.attachElementAfter(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath("breakthemod", name)
        ) { drawContext, _ ->
            render(drawContext, client.font)
        }
    }

    /** Render entry point.
     * @param drawContext The draw conComponent to use.
     * @param textRender The textRender to use.
     * */
    abstract fun render(drawContext: GuiGraphicsExtractor, textRender: Font)

    /**
     * Renders a list of strings.
     *  @param itemList The list of items to display.
     * */
    fun renderListWidget(
        drawContext: GuiGraphicsExtractor,
        textRender: Font,
        itemList: List<String>
    ) {
        if (shouldNotRender()) return

        val width = (itemList.maxOfOrNull { textRender.width(it) } ?: 100) + 2 * margin

        val height = (20 + itemList.size * entryHeight).coerceAtLeast(40)

        val renderCoords =
            config.position.getPos(margin, height, width, client.window.guiScaledWidth, client.window.guiScaledHeight)
                .apply { y += 5 }

        for (entry in itemList) {
            val color = if (entry == config.placeHolderText) config.placeHolderColor else config.textColor

            drawContext.text(textRender, entry, renderCoords.x + margin, renderCoords.y, color, false)

            renderCoords.y += entryHeight
        }
    }

    /**
     * Renders a string widget.
     * @param text The Component to render.
     * */
    fun renderTextWidget(
        drawContext: GuiGraphicsExtractor,
        textRender: Font,
        text: String
    ) {
        if (shouldNotRender()) return

        val width = textRender.width(text) + margin * 2

        val renderCoords =
            config.position.getPos(margin, 40, width, client.window.guiScaledWidth, client.window.guiScaledHeight)

        val color = if (text == config.placeHolderText) config.placeHolderColor else config.textColor

        drawContext.text(textRender, text, renderCoords.x + margin, renderCoords.y, color, false)
    }
}
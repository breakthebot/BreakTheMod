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
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.chariskar.breakthemod.client.api.providers.LoggingProvider
import net.chariskar.breakthemod.client.api.providers.ServerUtilsProvider
import net.chariskar.breakthemod.client.utils.Config
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier

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
 * Return a [Coords] object with the coords that match each position.
 *
 * @param height The height of the widget.
 * @param width The width of the widget.
 * @param margin The margin that should be kept from the widget to the screen.
 */
fun WidgetPosition.getPos(
    margin: Int,
    height: Int,
    width: Int
): Coords {
    val client = MinecraftClient.getInstance()
    val screenWidth = client.window.scaledWidth
    val screenHeight = client.window.scaledHeight
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

enum class WidgetCategories {
    General,
    Mining,
    Fishing,
}

fun WidgetCategories.next(): WidgetCategories {
    val entries = WidgetCategories.entries
    return entries[(ordinal + 1) % entries.size]
}

data class Coords(
    var x: Int,
    var y: Int
)

/**
 * Data class for representing the configuration of a widget.
 * @property enabled The status of the widget.
 * @property position The position of the widget.
 * @property category The category of the widget.
 *  */
@Serializable
data class WidgetConfig (
    var enabled: Boolean,
    var position: WidgetPosition,
    val category: WidgetCategories,
)

/**
 * Represents one of the renderable widgets in breakthemod.
 * @param name The name of the widget, must be underscored.
 * @property config The configuration of the widget, set by the widget.
 * */
abstract class BaseWidget(
    val name: String,
) : ServerUtilsProvider, LoggingProvider {
    protected val client: MinecraftClient = MinecraftClient.getInstance()

    abstract val config: WidgetConfig

    open val margin: Int = 10
    open val entryHeight: Int = 15

    open val placeholder: String = ""

    /**
     * Generate the modmenu entry for the configs position.
     * @param category The category to register the option in.
     * @param entryBuilder The entry builder.
     * */
    open fun getModMenuConfig(
        category: ConfigCategory,
        entryBuilder: ConfigEntryBuilder
    ) {
        category.addEntry(
            entryBuilder.startEnumSelector(
                Text.literal("$name Position"),
                WidgetPosition::class.java,
                config.position
            ).setSaveConsumer { position: WidgetPosition ->
                config.position = position
                Config.saveWidgetConfig(name, config)
            }.setDefaultValue { config.position }.build()
        )
    }

    /** Registers the element after VanillaHudElements.CHAT.*/
    open fun register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, Identifier.of("breakthemod", name)) { context, _ -> render(context, client.textRenderer) }
    }

    /** Render entry point.
     * @param drawContext The draw context to use.
     * */
    abstract fun render(drawContext: DrawContext, textRender: TextRenderer)


    /**
     * Widget render method.
     *
     * */
    open fun renderListWidget(
        drawContext: DrawContext,
        textRender: TextRenderer,
        itemList: List<String>
    ) {
        if (!isModEnabled() || !config.enabled || client.options.hudHidden) return

        val width = (itemList.maxOfOrNull { textRender.getWidth(it) } ?: 100) + 2 * margin

        val height = (20 + itemList.size * entryHeight).coerceAtLeast(40)

        val renderCoords = config.position.getPos(margin, height, width).apply { y+=5 }

        for (entry in itemList) {
            val color = if (entry == placeholder) 0xFFFF6B6B else 0xFFFFFFFF

            drawContext.drawText(textRender, entry, renderCoords.x + margin, renderCoords.y, color.toInt(), false)
            renderCoords.y += entryHeight
        }
    }

    open fun renderTextWidget(
        drawContext: DrawContext,
        textRender: TextRenderer,
        text: String
    ) {
        if (!isModEnabled() || !config.enabled || client.options.hudHidden) return

        val renderCoords = config.position.getPos(margin, 40, 100)

        val color = if (text == placeholder)  0xFFFF6B6B else 0xFFFFFFFF

        drawContext.drawText(textRender, text, renderCoords.x + margin, renderCoords.y, color.toInt(), false)
    }
}
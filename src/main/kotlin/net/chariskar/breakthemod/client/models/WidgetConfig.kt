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
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder
import net.chariskar.breakthemod.client.api.widget.WidgetModes
import net.chariskar.breakthemod.client.api.widget.WidgetPosition
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.network.chat.Component

/**
 * Data class for representing the configuration of a widget.
 * @property enabled The status of the widget.
 * @property position The position of the widget.
 * @property category The category of the widget.
 * @property text The text to draw.
 * @property textPlaceholder The placeholder to put the items in.
 * @property placeHolderText The placeholder text.
 * @property textColor The color of the text to draw.
 * @property placeHolderColor The color of the placeholder.
 * */
@Serializable
data class WidgetConfig (
    val name: String = "",
    var enabled: Boolean = false,
    var position: WidgetPosition,
    val category: WidgetModes,
    var text: String = "",
    var textPlaceholder: String = "",
    var placeHolderText: String = "",
    var textColor: Int = 0xFFFFFFFF.toInt(),
    var placeHolderColor: Int = 0xFFFF6B6B.toInt()
)

fun WidgetConfig.getPositionConfig(
    category: SubCategoryBuilder,
    entryBuilder: ConfigEntryBuilder,
    widgetName: String
) {
    category.add(
        entryBuilder.startEnumSelector(
            Component.literal("$name Position"),
            WidgetPosition::class.java,
            position
        ).setSaveConsumer { pos: WidgetPosition ->
            position = pos
            Config.saveWidgetConfig(widgetName, this)
        }.setDefaultValue { position }.build()
    )
}

/**
 * Generate the Component config for widgets.
 * @param category The subcategory.
 * @param entryBuilder The entry builder.
 * @param defaultText The default widget text.
 * @param defaultPlaceHolderText The default placeholder text.
 * @param defaultTextPlaceHolder The default text placeholder.
 * */
fun WidgetConfig.getTextConfig(
    category: SubCategoryBuilder,
    entryBuilder: ConfigEntryBuilder,
    defaultText: String,
    defaultPlaceHolderText: String,
    defaultTextPlaceHolder: String,
    widgetName: String,
) {
    category.add(
        entryBuilder.startStrField(
            Component.literal("$name text"),
            text
        ).setSaveConsumer { str: String ->
            if (text == str || str.isEmpty()) return@setSaveConsumer
            if (!str.contains(textPlaceholder)) return@setSaveConsumer
            text = str
            Config.saveWidgetConfig(widgetName, this)
        }.setDefaultValue { defaultText }.build()
    )

    category.add(
        entryBuilder.startStrField(
            Component.literal("$name placeholder text"),
            placeHolderText
        ).setSaveConsumer { str: String ->
            if (placeHolderText == str || str.isEmpty()) return@setSaveConsumer
            placeHolderText = str
            Config.saveWidgetConfig(widgetName, this)
        }.setDefaultValue { defaultPlaceHolderText }.build()
    )

    category.add(
        entryBuilder.startStrField(
            Component.literal("$name text placeholder"),
            textPlaceholder
        ).setSaveConsumer { str: String ->
            if (textPlaceholder == str || str.isEmpty()) return@setSaveConsumer
            textPlaceholder = text.replace(textPlaceholder, str)
            Config.saveWidgetConfig(widgetName, this)
        }.setDefaultValue { defaultTextPlaceHolder }.build()
    )
}

fun WidgetConfig.getTextColorConfig(
    category: SubCategoryBuilder,
    entryBuilder: ConfigEntryBuilder,
    widgetName: String
) {
    category.add(
        entryBuilder.startStrField(
            Component.literal("$name Component color"),
            textColor.toHexString()
        ).setSaveConsumer { str: String ->
            if (textColor == str.hexToInt() || str.isEmpty()) return@setSaveConsumer
            try {
                textColor = str.hexToInt()
            } catch (e: Exception) {
                return@setSaveConsumer
            }
            Config.saveWidgetConfig(widgetName, this)
        }.setDefaultValue { "0xFFFFFFFF" }.build()
    )

    category.add(
        entryBuilder.startStrField(
            Component.literal("$name placeholder Component color"),
            placeHolderColor.toHexString()
        ).setSaveConsumer { str: String ->
            if (placeHolderColor == str.hexToInt() || str.isEmpty()) return@setSaveConsumer
            try {
                placeHolderColor = str.hexToInt()
            } catch (e: Exception) {
                return@setSaveConsumer
            }
            Config.saveWidgetConfig(widgetName, this)
        }.setDefaultValue { "0xFFFF6B6B" }.build()
    )
}
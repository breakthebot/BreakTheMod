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
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder
import net.chariskar.breakthemod.client.api.widget.WidgetModes
import net.chariskar.breakthemod.client.api.widget.WidgetPosition
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.text.Text

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
    val name: String,
    var enabled: Boolean,
    var position: WidgetPosition,
    val category: WidgetModes,
    var text: String = "",
    var textPlaceholder: String = "",
    var placeHolderText: String,
    var textColor: Int = 0xFFFFFFFF.toInt(),
    var placeHolderColor: Int = 0xFFFF6B6B.toInt()
)

fun WidgetConfig.getPositionConfig(
    category: SubCategoryBuilder,
    entryBuilder: ConfigEntryBuilder,
) {
    category.add(
        entryBuilder.startEnumSelector(
            Text.literal("$name Position"),
            WidgetPosition::class.java,
            position
        ).setSaveConsumer { pos: WidgetPosition ->
            position = pos
            Config.saveWidgetConfig(name, this)
        }.setDefaultValue { position }.build()
    )
}

fun WidgetConfig.getTextConfig(
    category: SubCategoryBuilder,
    entryBuilder: ConfigEntryBuilder,
    defaultText: String,
    defaultPlaceHolderText: String
) {
    category.add(
        entryBuilder.startStrField(
            Text.literal("$name text"),
            text
        ).setSaveConsumer { str: String ->
            if (!str.contains(textPlaceholder)) return@setSaveConsumer
            text = str
        }.setDefaultValue { defaultText }.build()
    )

    category.add(
        entryBuilder.startStrField(
            Text.literal("$name placeholder text"),
            textPlaceholder
        ).setSaveConsumer { str: String ->
            textPlaceholder = str
        }.setDefaultValue { defaultPlaceHolderText }.build()
    )
}

fun WidgetConfig.getTextColorConfig(
    category: SubCategoryBuilder,
    entryBuilder: ConfigEntryBuilder,
) {
    category.add(
        entryBuilder.startStrField(
            Text.literal("$name text color"),
            textColor.toHexString()
        ).setSaveConsumer { str: String ->
            try {
                textColor = str.hexToInt()
            } catch (e: Exception) {
                return@setSaveConsumer
            }
        }.setDefaultValue { "0xFFFFFFFF" }.build()
    )

    category.add(
        entryBuilder.startStrField(
            Text.literal("$name placeholder text color"),
            placeHolderColor.toHexString()
        ).setSaveConsumer { str: String ->
            try {
                placeHolderColor = str.hexToInt()
            } catch (e: Exception) {
                return@setSaveConsumer
            }
        }.setDefaultValue { "0xFFFF6B6B" }.build()
    )
}
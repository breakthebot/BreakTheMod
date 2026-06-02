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

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

/**
 * Provides utilities for Text widgets to avoid writing similar code over and over.
 * @property placeholder The placeholder of the widget text.
 * @property margin The margin of the widget.
 * @property entryHeight The height of each entry.
 * */
abstract class TextWidget(
    name: String,
) : BaseWidget(name) {
    open val margin: Int = 10
    open val entryHeight: Int = 15

    abstract val placeholder: String

    /**
     * Widget render method.
     *
     * */
    open fun renderTextWidget(
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
}
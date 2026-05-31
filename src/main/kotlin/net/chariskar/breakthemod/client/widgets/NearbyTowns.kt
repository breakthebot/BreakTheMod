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

package net.chariskar.breakthemod.client.widgets

import net.chariskar.breakthemod.client.api.widget.BaseWidget
import net.chariskar.breakthemod.client.api.widget.WidgetCategories
import net.chariskar.breakthemod.client.api.widget.WidgetConfig
import net.chariskar.breakthemod.client.api.widget.WidgetPosition
import net.chariskar.breakthemod.client.api.widget.getPos
import net.chariskar.breakthemod.client.modules.Cache
import net.chariskar.breakthemod.client.modules.NearbyEngine
import net.chariskar.breakthemod.client.widgets.NearbyWidget.ENTRY_HEIGHT
import net.chariskar.breakthemod.client.widgets.NearbyWidget.MARGIN
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

object NearbyTowns : BaseWidget(
    "nearby_towns"
) {
    override val config: WidgetConfig = WidgetConfig(
        true,
        WidgetPosition.MIDDLE_LEFT,
        WidgetCategories.General
    )

    override fun render(
        drawContext: DrawContext,
        textRender: TextRenderer
    ) {
        if (client.options.hudHidden || client.world == null || client.player == null) return
        if (!config.enabled || !isModEnabled()) return

        val towns = Cache.nearbyTowns

        val townList = if (towns.isEmpty()) {
            listOf("No towns nearby")
        } else towns.map { it.name }

        val width = (townList.maxOfOrNull { textRender.getWidth(it) } ?: 100) + 2 * MARGIN

        val height = (20 + townList.size * ENTRY_HEIGHT).coerceAtLeast(40)

        val renderCoords = config.position.getPos(MARGIN, height, width).apply { y+=5 }

        for (entry in townList) {
            val color = if (entry == "No towns nearby") 0xFFFF6B6B else 0xFFFFFFFF

            drawContext.drawText(textRender, entry, renderCoords.x + MARGIN, renderCoords.y, color.toInt(), false)
            renderCoords.y += ENTRY_HEIGHT
        }
    }
}
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
import net.chariskar.breakthemod.client.api.widget.Coords
import net.chariskar.breakthemod.client.api.widget.WidgetCategories
import net.chariskar.breakthemod.client.api.widget.WidgetConfig
import net.chariskar.breakthemod.client.api.widget.WidgetPosition
import net.chariskar.breakthemod.client.api.widget.getPos
import net.chariskar.breakthemod.client.modules.NearbyEngine
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

object NearbyWidget : BaseWidget(
    "NearbyWidget",
    Identifier.of("breakthemod", "nearby_layer")
) {
    const val MARGIN: Int = 10
    const val ENTRY_HEIGHT: Int = 15

    override val config = WidgetConfig(
        name,
        true,
        WidgetCategories.General,
        WidgetPosition.TOP_LEFT
    )
    
    override fun render(drawContext: DrawContext) {
        if (client.options.hudHidden || client.world == null || client.player == null) return
        if (!config.enabled || !isModEnabled()) return

        val players = NearbyEngine.getPlayers()
        val playerList = if (players.isEmpty()) {
            listOf("No players nearby")
        } else players.map { it.toString() }

        val textRender = client.textRenderer

        val width = (playerList.maxOfOrNull { textRender.getWidth(it) } ?: 100) + 2 * MARGIN

        val height = (20 + playerList.size * ENTRY_HEIGHT).coerceAtLeast(40)

        val renderCoords = config.position.getPos(MARGIN, height, width).apply { y+=5 }

        for (entry in playerList) {
            val color = if (entry == "No players nearby") 0xFFFF6B6B else 0xFFFFFFFF

            drawContext.drawText(textRender, entry, renderCoords.x + MARGIN, renderCoords.y, color.toInt(), false)
            renderCoords.y += ENTRY_HEIGHT
        }
    }
}
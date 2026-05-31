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
import net.chariskar.breakthemod.client.widgets.NearbyWidget.ENTRY_HEIGHT
import net.chariskar.breakthemod.client.widgets.NearbyWidget.MARGIN
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerEntity
import org.breakthebot.breakthelibrary.models.Town
import kotlin.math.atan2
import kotlin.math.sqrt

val directions = arrayOf("S", "SW", "W", "NW", "N", "NE", "E", "SE")

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
        } else towns.map { formatTownEntry(it) }

        val width = (townList.maxOfOrNull { textRender.getWidth(it) } ?: 100) + 2 * MARGIN

        val height = (20 + townList.size * ENTRY_HEIGHT).coerceAtLeast(40)

        val renderCoords = config.position.getPos(MARGIN, height, width).apply { y+=5 }

        for (entry in townList) {
            val color = if (entry == "No towns nearby") 0xFFFF6B6B else 0xFFFFFFFF

            drawContext.drawText(textRender, entry, renderCoords.x + MARGIN, renderCoords.y, color.toInt(), false)
            renderCoords.y += ENTRY_HEIGHT
        }
    }

    fun formatTownEntry(t: Town): String {
        val player = client.player ?: return t.name
        return "-${t.name} direction: ${t.getTownDirection(player)}, distance: ${t.calculateDistance(player)} blocks"
    }

}

fun Town.getTownDirection(player: PlayerEntity): String {
    val dx = (player.x.toInt() - coordinates!!.spawn!!.x!!.toInt()).toDouble()
    val dz = (player.z.toInt() - coordinates!!.spawn!!.z!!.toInt()).toDouble()

    val angle = Math.toDegrees(atan2(-dx, dz))
    val index = (((angle + 360.0) % 360.0 + 22.5) / 45.0).toInt() % 8

    return directions[index]
}

fun Town.calculateDistance(player: PlayerEntity): Int {
    val dx = player.x - coordinates!!.spawn!!.x!!
    val dy = player.y - coordinates!!.spawn!!.y!!
    val dz = player.z - coordinates!!.spawn!!.z!!
    return sqrt(dx * dx + dy * dy + dz * dz).toInt()
}
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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.chariskar.breakthemod.client.api.widget.BaseWidget
import net.chariskar.breakthemod.client.api.widget.Coords
import net.chariskar.breakthemod.client.api.widget.WidgetCategories
import net.chariskar.breakthemod.client.api.widget.WidgetConfig
import net.chariskar.breakthemod.client.api.widget.WidgetPosition
import net.chariskar.breakthemod.client.modules.NearbyEngine
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

@Serializable
@SerialName("nearby_widget")
class NearbyWidgetConfig(
    override val name: String = "NearbyWidget",
    override val category: WidgetCategories = WidgetCategories.General,
    override var enabled: Boolean = Config.features.radarEnabled,
    override var coords: Coords = Coords(0,0),
    override var position: WidgetPosition = WidgetPosition.TOP_LEFT,
    var margin: Int = 10,
    var entryHeight: Int = 15,
) : WidgetConfig()

object NearbyWidget : BaseWidget(
    "NearbyWidget",
    Identifier.of("breakthemod", "nearby_layer")
) {

    override fun render(drawContext: DrawContext) {
        if (client.options.hudHidden || client.world == null || client.player == null) return
        if (!Config.features.radarEnabled || !getModEnabled()) return
        val players = NearbyEngine.getPlayers()
        val config = getConfig<NearbyWidgetConfig>() ?: NearbyWidgetConfig()

        val playerList = if (players.isEmpty()) {
            listOf("No players nearby")
        } else players.map { it.toString() }

        val textRender = client.textRenderer

        val margin = config.margin

        val width = (playerList.maxOfOrNull { textRender.getWidth(it) } ?: 100) + 2 * margin

        val height = (20 + playerList.size * config.entryHeight).coerceAtLeast(40)

        val renderCoords: Coords = when (config.position) {
            WidgetPosition.TOP_LEFT -> {
                Coords(margin, margin)
            }
            WidgetPosition.TOP_RIGHT -> {
                Coords(client.window.scaledWidth - width - margin, margin)
            }
            WidgetPosition.BOTTOM_LEFT -> {
                Coords(margin, client.window.scaledHeight - height - margin)
            }
            WidgetPosition.BOTTOM_RIGHT -> {
                Coords(client.window.scaledWidth - width - margin, client.window.scaledHeight - height - margin)
            }
            WidgetPosition.CUSTOM -> {
                config.coords
            }
        }.apply { y += 5 }

        for (entry in playerList) {
            val color = if (entry == "No players nearby") 0xFFFF6B6B else 0xFFFFFFFF

            drawContext.drawText(textRender, entry, renderCoords.x + margin, renderCoords.y, color.toInt(), false)
            renderCoords.y += config.entryHeight
        }
    }
}
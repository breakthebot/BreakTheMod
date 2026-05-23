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
class NearbyWidgetConfig(
    override val name: String,
    override val category: WidgetCategories,
    override var coords: Coords,
    override var position: WidgetPosition,
    override var enabled: Boolean,
    var margin: Int = 10,
    var entryHeight: Int = 15,
) : WidgetConfig

class NearbyWidget : BaseWidget(
    "NearbyWidget",
    Identifier.of("breakthemod", "nearby_layer")
) {

    override fun render(drawContext: DrawContext) {
        if (client.options.hudHidden || client.world == null || client.player == null) return
        if (!Config.features.radarEnabled || !getModEnabled()) return
        val players = NearbyEngine.getPlayers()

        val playerList = if (players.isEmpty()) {
            listOf("No players nearby")
        } else players.map { it.toString() }

        val textRender = client.textRenderer

        //val width = (playerList.maxOfOrNull { textRender.getWidth(it) } ?: 100) + 2 * margin

        //val height = (20 + playerList.size * entryHeight).coerceAtLeast(40)



    }

}
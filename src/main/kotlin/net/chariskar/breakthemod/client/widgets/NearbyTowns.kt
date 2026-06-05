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

import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder
import net.chariskar.breakthemod.client.api.widget.BaseWidget
import net.chariskar.breakthemod.client.api.widget.WidgetModes
import net.chariskar.breakthemod.client.api.widget.WidgetPosition
import net.chariskar.breakthemod.client.models.WidgetConfig
import net.chariskar.breakthemod.client.models.getPositionConfig
import net.chariskar.breakthemod.client.models.getTextColorConfig
import net.chariskar.breakthemod.client.models.getTextConfig
import net.chariskar.breakthemod.client.modules.Cache
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import org.breakthebot.breakthelibrary.models.Town
import kotlin.math.atan2
import kotlin.math.sqrt

val directions = arrayOf("S", "SW", "W", "NW", "N", "NE", "E", "SE")

object NearbyTowns : BaseWidget(
    "nearby_towns"
) {

    override val config: WidgetConfig = Config.getWidgetConfig(name) ?: WidgetConfig(
        name = "NearbyTowns",
        enabled = true,
        position = WidgetPosition.MIDDLE_LEFT,
        category = WidgetModes.General,
        placeHolderText = "There are no towns nearby",
    )

    override fun getModMenuConfig(category: SubCategoryBuilder, entryBuilder: ConfigEntryBuilder) {
        category.add(
            entryBuilder.startBooleanToggle(
                Text.literal("Enable nearby towns radar"),
                config.enabled
            ).setSaveConsumer { enabled: Boolean ->
                config.enabled = enabled
                Config.saveWidgetConfig(name, config)
            }.setDefaultValue { true }.build()
        )
        config.getPositionConfig(category, entryBuilder)
        config.getTextConfig(category, entryBuilder, "", "There are no towns nearby.")
        config.getTextColorConfig(category, entryBuilder)
    }

    override fun render(
        drawContext: DrawContext,
        textRender: TextRenderer
    ) {
        val towns = Cache.nearbyTowns

        val townList = if (towns.isEmpty()) {
            listOf(config.placeHolderText)
        } else towns.map { formatTownEntry(it) }

        renderListWidget(
            drawContext,
            textRender,
            townList
        )
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
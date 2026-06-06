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
import net.chariskar.breakthemod.client.modules.NearbyEngine
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

object NearbyPlayers : BaseWidget(
    "nearby_players"
) {

    override val config: WidgetConfig = Config.getWidgetConfig(name) ?: WidgetConfig(
        name = "NearbyPlayers",
        enabled = true,
        position = WidgetPosition.TOP_LEFT,
        category = WidgetModes.General,
        placeHolderText = "There are no players nearby",
    )

    override fun getModMenuConfig(category: SubCategoryBuilder, entryBuilder: ConfigEntryBuilder) {
        category.add(
            entryBuilder.startBooleanToggle(
                Text.literal("Enable nearby player radar"),
                config.enabled
            ).setSaveConsumer { enabled: Boolean ->
                config.enabled = enabled
                Config.saveWidgetConfig(name, config)
            }.setDefaultValue { true }.build()
        )
        config.getPositionConfig(category, entryBuilder, name)
        config.getTextConfig(category, entryBuilder, "", "There are no players nearby.", "", name)
        config.getTextColorConfig(category, entryBuilder, name)
    }
    
    override fun render(drawContext: DrawContext, textRender: TextRenderer) {
        val players = NearbyEngine.players

        val playerList = if (players.isEmpty()) {
            listOf(config.placeHolderText)
        } else players.map { it.toString() }

        renderListWidget(
            drawContext,
            textRender,
            playerList
        )
    }
}
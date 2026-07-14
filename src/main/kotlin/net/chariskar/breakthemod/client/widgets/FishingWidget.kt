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

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder
import net.chariskar.breakthemod.client.api.widget.BaseWidget
import net.chariskar.breakthemod.client.api.widget.WidgetModes
import net.chariskar.breakthemod.client.api.widget.WidgetPosition
import net.chariskar.breakthemod.client.models.WidgetConfig
import net.chariskar.breakthemod.client.models.getPositionConfig
import net.chariskar.breakthemod.client.models.getTextColorConfig
import net.chariskar.breakthemod.client.models.getTextConfig
import net.chariskar.breakthemod.client.modules.ActionTracker
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor

object FishingWidget : BaseWidget(
    "fishing_widget"
){
    override val config: WidgetConfig = Config.getWidgetConfig(name) ?: WidgetConfig(
        name = "FishingWidget",
        enabled = false,
        position = WidgetPosition.MIDDLE_RIGHT,
        category = WidgetModes.Fishing,
        placeHolderText = "You have not fished any items.",
        text = "You have FISHED items.",
        textPlaceholder = "FISHED",
    )

    override fun getModMenuConfig(
        category: SubCategoryBuilder,
        entryBuilder: ConfigEntryBuilder
    ) {
        config.getPositionConfig(category, entryBuilder, name)
        config.getTextConfig(category, entryBuilder, "You have FISHED items.", "You have not fished any items.", "FISHED", name)
        config.getTextColorConfig(category, entryBuilder, name)
    }

    override fun render(
        drawContext: GuiGraphicsExtractor,
        textRender: Font
    ) {
        val text = if (ActionTracker.fishFished == 0) {
            config.placeHolderText
        } else config.text.replace(config.textPlaceholder, ActionTracker.fishFished.toString())

        renderTextWidget(
            drawContext,
            textRender,
            text
        )
    }
}
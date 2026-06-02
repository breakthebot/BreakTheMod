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
import net.chariskar.breakthemod.client.modules.ActionTracker
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

object MiningWidget : BaseWidget("Mining_Widget"){
    override val placeholder: String = "You have not mined any gold."

    override val config: WidgetConfig = Config.getWidgetConfig(name) ?: WidgetConfig(
        false,
        WidgetPosition.MIDDLE_RIGHT,
        WidgetCategories.Mining,
    )

    override fun render(
        drawContext: DrawContext,
        textRender: TextRenderer
    ) {
        val text = if (ActionTracker.goldMined == 0) placeholder else "You have mined ${ActionTracker.goldMined} gold."

        renderTextWidget(
            drawContext,
            textRender,
            text
        )
    }
}
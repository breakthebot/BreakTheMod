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
import net.chariskar.breakthemod.client.api.widget.BaseWidget
import net.chariskar.breakthemod.client.api.widget.WidgetCategories
import net.chariskar.breakthemod.client.api.widget.WidgetConfig
import net.chariskar.breakthemod.client.api.widget.WidgetPosition
import net.chariskar.breakthemod.client.modules.ActionTracker
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

object MiningWidget : BaseWidget("mining_widget") {
    override val placeholder: String = "You have not mined any gold."

    override val config: WidgetConfig = Config.getWidgetConfig(name) ?: WidgetConfig(
        false,
        WidgetPosition.MIDDLE_RIGHT,
        null,
        WidgetCategories.Mining,
    )

    override fun getModMenuConfig(
        category: ConfigCategory,
        entryBuilder: ConfigEntryBuilder
    ) {
        super.getModMenuConfig(category, entryBuilder)
        category.addEntry(
            entryBuilder.startStrField(
                Text.literal("Mining widget text."),
                config.text ?: "You have mined GOLD gold"
            ).setSaveConsumer { str: String ->
                config.text = str
                Config.saveWidgetConfig(name, config)
            }.setDefaultValue { "You have mined GOLD gold." }.build()
        )
    }

    override fun render(
        drawContext: DrawContext,
        textRender: TextRenderer
    ) {
        val text = if (ActionTracker.goldMined == 0) {
            placeholder
        } else config.text?.replace("GOLD", ActionTracker.goldMined.toString()) ?: "You have mined ${ActionTracker.goldMined} gold."

        renderTextWidget(
            drawContext,
            textRender,
            text
        )
    }
}
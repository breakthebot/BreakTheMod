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

package net.chariskar.breakthemod.client.api.widget

import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.chariskar.breakthemod.Breakthemod

/**
 * Widget manager.
 * @property widgetMode The current widget mode.
 * */
object WidgetManager {
    var widgetMode = WidgetCategories.None
        private set

    fun changeMode(
        category: WidgetCategories
    ) {
        Breakthemod.widgets
            .filter { it.config.category == WidgetCategories.General }
            .forEach { it.config.enabled = false }

        Breakthemod.widgets
            .filter { it.config.category == category }
            .forEach { it.config.enabled = true }

        widgetMode = category
    }

    /**
     * Register the modmenu integrations for all the widgets.
     * */
    fun registerWidgetIntegration(
        category: ConfigCategory,
        entryBuilder: ConfigEntryBuilder
    ) {
        Breakthemod.widgets.forEach { it.getModMenuConfig(category, entryBuilder) }
    }
}
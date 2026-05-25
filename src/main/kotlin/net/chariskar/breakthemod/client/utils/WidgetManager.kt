package net.chariskar.breakthemod.client.utils

import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.api.widget.WidgetCategories

object WidgetManager {
    var activeCategory = WidgetCategories.None
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

        activeCategory = category
    }
}
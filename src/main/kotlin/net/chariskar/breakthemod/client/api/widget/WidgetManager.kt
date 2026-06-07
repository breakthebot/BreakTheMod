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
import net.chariskar.breakthemod.client.modules.ActionTracker
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Schedule
import net.chariskar.breakthemod.client.utils.Scheduler
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

/**
 * Widget manager.
 * @property widgetMode The current widget mode.
 * */
object WidgetManager {
    var widgetMode = WidgetModes.General
        private set

    @OptIn(ExperimentalTime::class)
    fun changeMode(
        category: WidgetModes
    ) {
        if (category == WidgetModes.Off) {
            Breakthemod.widgets.forEach { it.config.enabled = false }
            widgetMode = category
            return
        }
        if (category == WidgetModes.Mining) {
            Scheduler.cancel(widgetMode.name)
        } else {
            if (Scheduler.tasks.containsKey(category.name)) return
            val task = Schedule(
                widgetMode.name,
                {
                    ActionTracker.goldMined = 0
                },
                Config.features.widgetDataLife.minutes
            )

            Scheduler.schedule(task)
        }

        Breakthemod.widgets
            .forEach { it.config.enabled = false }

        Breakthemod.widgets
            .filter { it.config.category == category }
            .forEach { it.config.enabled = true }

        widgetMode = category
        if (category == WidgetModes.Fishing) {
            ActionTracker.fishingModeActivated = Clock.System.now()
        }

        Config.config.widgetMode = widgetMode

        Config.saveConfig(Config.config)
    }

    fun registerKeyBind() {
        val keyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "Switch Modes",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,
                KeyBinding.Category.MISC
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient? ->
            while (keyBinding.wasPressed()) {
                val handle = client!!.window

                val shiftHeld =
                    InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)
                            || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)

                if (shiftHeld) {
                    changeMode(
                        widgetMode.next()
                    )
                    client.player?.sendMessage(
                        Text.literal("Switched to $widgetMode"),
                        true
                    )
                }
            }
        })
    }

    /**
     * Register the modmenu integrations for all the widgets.
     * */
    fun registerWidgetIntegration(
        category: ConfigCategory,
        entryBuilder: ConfigEntryBuilder
    ) {
        Breakthemod.widgets.forEach {
            val subCategory = entryBuilder.startSubCategory(Text.literal("${it.config.name} configuration"))
            it.getModMenuConfig(subCategory, entryBuilder)
            category.addEntry(subCategory.build())
        }
    }
}
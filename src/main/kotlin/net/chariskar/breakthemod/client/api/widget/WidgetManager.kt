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

import com.mojang.blaze3d.platform.InputConstants
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.chariskar.breakthemod.Breakthemod
import net.chariskar.breakthemod.client.modules.ActionTracker
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Schedule
import net.chariskar.breakthemod.client.utils.Scheduler
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.lwjgl.glfw.GLFW
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Widget manager.
 * @property widgetMode The current widget mode.
 * */
object WidgetManager {
    var widgetMode = WidgetModes.General
        private set

    val CATEGORY: KeyMapping.Category = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath("breakthemod", "custom_category")
    )
    lateinit var keyMapping: KeyMapping

    /**
     * Changes modes to category.
     * */
    fun changeMode(
        category: WidgetModes,
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
        keyMapping = KeyMappingHelper.registerKeyMapping(
            KeyMapping(
                "key.breakthemod.switch_modes",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,
                CATEGORY
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register(
            ClientTickEvents.EndTick { client: Minecraft? ->
                while (keyMapping.consumeClick()) {
                    val handle = client!!.window
                    val shiftHeld = InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT) ||
                        InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)

                    if (shiftHeld) {
                        changeMode(widgetMode.next())
                        client.player?.sendOverlayMessage(
                            Component.literal("Switched to $widgetMode")
                        )
                    }
                }
            }
        )
    }

    /**
     * Register the modmenu integrations for all the widgets.
     * */
    fun registerWidgetIntegration(
        category: ConfigCategory,
        entryBuilder: ConfigEntryBuilder,
    ) {
        Breakthemod.widgets.forEach {
            val subCategory = entryBuilder.startSubCategory(Component.literal("${it.config.name} configuration"))
            it.getModMenuConfig(subCategory, entryBuilder)
            category.addEntry(subCategory.build())
        }
    }
}

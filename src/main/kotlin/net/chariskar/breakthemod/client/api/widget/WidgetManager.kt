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
import net.chariskar.breakthemod.client.api.widget.WidgetManager.widgetMode
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

/**
 * Widget manager.
 * @property widgetMode The current widget mode.
 * */
object WidgetManager {
    var widgetMode = WidgetCategories.General
        private set

    fun changeMode(
        category: WidgetCategories
    ) {
        Breakthemod.widgets
            .filterNot { it.config.category == WidgetCategories.General }
            .forEach { it.config.enabled = false }

        Breakthemod.widgets
            .filter { it.config.category == category }
            .forEach { it.config.enabled = true }

        widgetMode = category
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
        Breakthemod.widgets.forEach { it.getModMenuConfig(category, entryBuilder) }
    }
}
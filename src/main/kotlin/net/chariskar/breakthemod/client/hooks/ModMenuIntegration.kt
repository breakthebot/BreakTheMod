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

package net.chariskar.breakthemod.client.hooks

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Config.WidgetPosition
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text


class ModMenuIntegration : ModMenuApi {

    companion object {
        fun createConfigScreen(parent: Screen): Screen {
            val builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("BreakTheMod"))

            val entryBuilder = builder.entryBuilder()
            val general = builder.getOrCreateCategory(Text.literal("BreakTheMod config"))

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Text.literal("Enable BreakTheMod on other servers"),
                    Config.getEnabledServers()
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.getInstance().config!!.enabledOnOtherServers = enabled
                        Config.saveConfig(Config.getInstance().config)
                    }
                    .setDefaultValue { false }
                    .build()
            )

            general.addEntry(
                entryBuilder.startEnumSelector(
                    Text.literal("Widget Position"),
                    WidgetPosition::class.java,
                    Config.getWidgetPos().widgetPosition
                ).setSaveConsumer { position: WidgetPosition ->
                    Config.getInstance().config!!.widgetPosition = position
                    Config.saveConfig(Config.getInstance().config)
                }.setDefaultValue { WidgetPosition.TOP_LEFT }.build()
            )

            general.addEntry(
                entryBuilder.startIntField(
                    Text.literal("Custom X Position"),
                    Config.getWidgetPos().customX
                )
                    .setSaveConsumer { x: Int ->
                        Config.getInstance().config!!.customX = x
                        Config.saveConfig(Config.getInstance().config)
                    }
                    .setMin(0)
                    .setMax(MinecraftClient.getInstance().currentScreen!!.width)
                    .setDefaultValue { 0 }
                    .build()
            )

            general.addEntry(
                entryBuilder.startIntField(
                    Text.literal("Custom Y Position"),
                    Config.getWidgetPos().customY
                )
                    .setSaveConsumer { y: Int ->
                        Config.getInstance().config!!.customY = y
                        Config.saveConfig(Config.getInstance().config)
                    }
                    .setMin(0)
                    .setMax(MinecraftClient.getInstance().currentScreen!!.height)
                    .setDefaultValue { 0 }
                    .build()
            )

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Text.literal("Player radar"),
                    Config.getRadar()
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.getInstance().config!!.radarEnabled = enabled
                        Config.saveConfig(Config.getInstance().config)
                    }.setDefaultValue { true }
                    .build()
            )

            general.addEntry(
                entryBuilder.startStrField(
                    Text.literal("Townless message"),
                    Config.getTownlessMessage("TOWN")
                ).setSaveConsumer { message: String ->
                    Config.setTownlessMessage(message)
                    Config.saveConfig(Config.getInstance().config)
                }.setDefaultValue {
                    Config.getTownlessMessage("TOWN")
                }.build()
            )

            general.addEntry(
                entryBuilder.startIntField(
                    Text.literal("Nearby entry height"),
                    Config.getWidgetPos().entryHeight
                )
                    .setSaveConsumer { height: Int ->
                        Config.getInstance().config!!.entryHeight = height
                        Config.saveConfig(Config.getInstance().config)
                    }.setDefaultValue { 15 }
                    .build()
            )

            general.addEntry(
                entryBuilder.startIntField(
                    Text.literal("Nearby entry margin"),
                    Config.getWidgetPos().entryHeight
                )
                    .setSaveConsumer { margin: Int ->
                        Config.getInstance().config!!.margin = margin
                        Config.saveConfig(Config.getInstance().config)
                    }.setDefaultValue { 10 }
                    .build()
            )

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Text.literal("dev"),
                    Config.getDevMode()
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.getInstance().config!!.dev = enabled
                        Config.saveConfig(Config.getInstance().config)
                    }.setDefaultValue { false }
                    .build()
            )


            return builder.build()
        }
    }

    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> {
        return ConfigScreenFactory(::createConfigScreen)
    }

}

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
import me.shedaniel.clothconfig2.api.ConfigCategory
import net.chariskar.breakthemod.client.utils.Config
import net.chariskar.breakthemod.client.utils.Config.WidgetPosition
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.chariskar.breakthemod.client.utils.Config.AutoHudType


class ModMenuIntegration : ModMenuApi {

    companion object {
        fun createConfigScreen(parent: Screen): Screen {
            val builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("BreakTheMod"))

            val entryBuilder = builder.entryBuilder()
            val general = builder.getOrCreateCategory(Text.literal("BreakTheMod config"))

            val options: ConfigCategory? = if ( Config.config.options ) {
                builder.getOrCreateCategory(Text.literal("Options"))
            } else null

            val devOptions: ConfigCategory? = if ( Config.getDevMode() ) {
                builder.getOrCreateCategory(Text.literal("Developer Settings"))
            } else null

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Text.literal("Enable BreakTheMod on other servers"),
                    Config.getEnabledServers()
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.config.enabledOnOtherServers = enabled
                        saveConfig()
                    }
                    .setDefaultValue { Config.config.enabledOnOtherServers }
                    .build()
            )

            general.addEntry(
                entryBuilder.startEnumSelector(
                    Text.literal("Widget Position"),
                    WidgetPosition::class.java,
                    Config.getWidget().widgetPosition
                ).setSaveConsumer { position: WidgetPosition ->
                    Config.config.widget.widgetPosition = position
                    saveConfig()
                }.setDefaultValue { WidgetPosition.TOP_LEFT }.build()
            )

            general.addEntry(
                entryBuilder.startEnumSelector(
                    Text.literal("AutoHUD type"),
                    AutoHudType::class.java,
                    Config.getHud()
                ).setSaveConsumer { hudType: AutoHudType ->
                    Config.config.hudType = hudType
                    saveConfig()
                }.setDefaultValue { AutoHudType.None }.build()
            )

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Text.literal("Player radar"),
                    Config.getRadar()
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.config.radarEnabled = enabled
                        saveConfig()
                    }.setDefaultValue { Config.config.radarEnabled }
                    .build()
            )

            general.addEntry(
                entryBuilder.startStrField(
                    Text.literal("Townless message"),
                    Config.getTownlessMessage("TOWN")
                ).setSaveConsumer { message: String ->
                    Config.setTownlessMessage(message)
                    saveConfig()
                }.setDefaultValue {
                    Config.getTownlessMessage("TOWN")
                }.build()
            )

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Text.literal("dev"),
                    Config.getDevMode()
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.config.dev = enabled
                        saveConfig()
                    }.setDefaultValue { Config.config.dev}
                    .build()
            )

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Text.literal("Options"),
                    Config.config.options
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.config.options = enabled
                        saveConfig()
                    } .setDefaultValue { Config.config.options }
                    .build()
            )

            devOptions?.addEntry(
                entryBuilder.startIntField(
                    Text.literal("Nearby entry height"),
                    Config.getWidget().entryHeight
                )
                    .setSaveConsumer { height: Int ->
                        Config.config.widget.entryHeight = height
                        saveConfig()
                    }.setDefaultValue { 15 }
                    .build()
            )

            devOptions?.addEntry(
                entryBuilder.startIntField(
                    Text.literal("Nearby entry margin"),
                    Config.getWidget().margin
                )
                    .setSaveConsumer { margin: Int ->
                        Config.config.widget.margin = margin
                        saveConfig()
                    }.setDefaultValue { 10 }
                    .build()
            )

            devOptions?.addEntry(
                entryBuilder.startBooleanToggle(
                    Text.literal("Cache"),
                    Config.getCache(),
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.config.cacheEnabled = enabled
                        saveConfig()
                    }.setDefaultValue { true }
                    .build()
            )

            options?.addEntry(
                entryBuilder.startIntField(
                    Text.literal("Custom X Position"),
                    Config.getWidget().customX
                )
                    .setSaveConsumer { x: Int ->
                        Config.config.widget.customX = x
                        saveConfig()
                    }
                    .setMin(0)
                    .setMax(MinecraftClient.getInstance().currentScreen!!.width)
                    .setDefaultValue { Config.config.widget.customX }
                    .build()
            )

            options?.addEntry(
                entryBuilder.startIntField(
                    Text.literal("Custom Y Position"),
                    Config.getWidget().customY
                )
                    .setSaveConsumer { y: Int ->
                        Config.config.widget.customY = y
                        saveConfig()
                    }
                    .setMin(0)
                    .setMax(MinecraftClient.getInstance().currentScreen!!.height)
                    .setDefaultValue { Config.config.widget.customY }
                    .build()
            )

            options?.addEntry(
                entryBuilder.startStrField(
                    Text.literal("API url"),
                    Config.config.urls.apiUrl
                )
                    .setSaveConsumer { url: String ->
                        Config.setApiUrl(url)
                        saveConfig()
                    }.setDefaultValue { Config.config.urls.apiUrl }
                    .build()
            )

            options?.addEntry(
                entryBuilder.startStrField(
                    Text.literal("Map url"),
                    Config.config.urls.mapUrl
                )
                    .setSaveConsumer { url: String ->
                        Config.setMapUrl(url)
                        saveConfig()
                    }.setDefaultValue { Config.config.urls.mapUrl }
                    .build()
            )

            options?.addEntry(
                entryBuilder.startStrField(
                    Text.literal("Staff url"),
                    Config.config.urls.staffUrl
                )
                    .setSaveConsumer { url: String ->
                        Config.setStaffUrl(url)
                        saveConfig()
                    }.setDefaultValue { Config.config.urls.staffUrl }
                    .build()
            )

            return builder.build()
        }

        fun saveConfig() = Config.saveConfig(Config.config)
    }

    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> {
        return ConfigScreenFactory(::createConfigScreen)
    }

}

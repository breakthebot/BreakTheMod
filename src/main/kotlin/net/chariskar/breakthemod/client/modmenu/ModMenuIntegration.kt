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

package net.chariskar.breakthemod.client.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.chariskar.breakthemod.client.models.AutoHudType
import net.chariskar.breakthemod.client.utils.Config
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class ModMenuIntegration : ModMenuApi {

    companion object {
        fun createConfigScreen(parent: Screen): Screen {
            val builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("BreakTheMod"))

            val entryBuilder = builder.entryBuilder()
            val general = builder.getOrCreateCategory(Component.literal("BreakTheMod config"))

            val options = if (Config.config.options) {
                builder.getOrCreateCategory(Component.literal("Options"))
            } else {
                null
            }

            val devOptions = if (Config.config.dev) {
                builder.getOrCreateCategory(Component.literal("Developer Settings"))
            } else {
                null
            }

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.literal("Enable BreakTheMod on other servers"),
                    Config.config.enabledOnOtherServers
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
                    Component.literal("AutoHUD type"),
                    AutoHudType::class.java,
                    Config.features.hudType
                ).setSaveConsumer { hudType: AutoHudType ->
                    Config.config.features.hudType = hudType
                    saveConfig()
                }.setDefaultValue { AutoHudType.None }.build()
            )

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.literal("Experience text overlay"),
                    Config.features.experienceText
                ).setSaveConsumer { enabled: Boolean ->
                    Config.config.features.experienceText = enabled
                    saveConfig()
                }.setDefaultValue(Config.features.experienceText).build()
            )

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.literal("Player name tag info"),
                    Config.getNameTag()
                ).setSaveConsumer { enabled: Boolean ->
                    Config.config.features.nameTagInfo = enabled
                    saveConfig()
                }.setDefaultValue(Config.getNameTag()).build()
            )

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.literal("dev"),
                    Config.config.dev
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.config.dev = enabled
                        saveConfig()
                    }.setDefaultValue { Config.config.dev }
                    .build()
            )

            general.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.literal("Options"),
                    Config.config.options
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.config.options = enabled
                        saveConfig()
                    }.setDefaultValue { Config.config.options }
                    .build()
            )

            devOptions?.addEntry(
                entryBuilder.startBooleanToggle(
                    Component.literal("Cache"),
                    Config.features.cacheEnabled,
                )
                    .setSaveConsumer { enabled: Boolean ->
                        Config.config.features.cacheEnabled = enabled
                        saveConfig()
                    }.setDefaultValue { true }
                    .build()
            )

            options?.addEntry(
                entryBuilder.startStrField(
                    Component.literal("API url"),
                    Config.config.libraryConfig.apiUrl
                )
                    .setSaveConsumer { url: String ->
                        Config.setApiUrl(url)
                        saveConfig()
                    }.setDefaultValue { Config.config.libraryConfig.apiUrl }
                    .build()
            )

            options?.addEntry(
                entryBuilder.startStrField(
                    Component.literal("Map url"),
                    Config.config.libraryConfig.mapUrl
                )
                    .setSaveConsumer { url: String ->
                        Config.setMapUrl(url)
                        saveConfig()
                    }.setDefaultValue { Config.config.libraryConfig.mapUrl }
                    .build()
            )

            options?.addEntry(
                entryBuilder.startStrField(
                    Component.literal("Staff url"),
                    Config.config.libraryConfig.staffUrl
                )
                    .setSaveConsumer { url: String ->
                        Config.setStaffUrl(url)
                        saveConfig()
                    }.setDefaultValue { Config.config.libraryConfig.staffUrl }
                    .build()
            )

            return builder.build()
        }

        fun saveConfig() = Config.saveConfig(Config.config)
    }

    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> = ConfigScreenFactory(::createConfigScreen)
}

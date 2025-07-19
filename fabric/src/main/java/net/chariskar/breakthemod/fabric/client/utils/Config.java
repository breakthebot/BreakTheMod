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

package net.chariskar.breakthemod.fabric.client.utils;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.chariskar.breakthemod.utils.config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Config  {

    public static Screen createConfigScreen(Screen parent) {

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("BreakTheMod"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.literal("BreakTheMod config"));

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.literal("Enable BreakTheMod on other servers"),
                        config.getInstance().isEnabledOnOtherServers())
                .setSaveConsumer(enabled -> {
                    config.getInstance().setEnabledOnOtherServers(enabled);
                    config.getInstance().saveConfig();
                })
                .setDefaultValue(() -> false)
                .build()
        );

        general.addEntry(entryBuilder.startEnumSelector(
                Text.literal("Widget Position"),
                config.WidgetPosition.class,
                config.getInstance().getWidgetPosition()
        ).setSaveConsumer(position -> {
            config.getInstance().setWidgetPosition(position);
            config.getInstance().saveConfig();
        }).setDefaultValue(() -> config.WidgetPosition.TOP_LEFT).build());

        general.addEntry(entryBuilder.startIntField(
                        Text.literal("Custom X Position"),
                        config.getInstance().getCustomX()
                )
                .setSaveConsumer(x -> {
                    config.getInstance().setCustomX(x);
                    config.getInstance().saveConfig();
                })
                .setMin(0)
                .setMax(MinecraftClient.getInstance().currentScreen.width)
                .setDefaultValue(() -> 0)
                .build());

        general.addEntry(entryBuilder.startIntField(
                                Text.literal("Custom Y Position"),
                        config.getInstance().getCustomY()
                        )
                        .setSaveConsumer(y -> {
                            config.getInstance().setCustomY(y);
                            config.getInstance().saveConfig();
                        })
                        .setMin(0)
                        .setMax(MinecraftClient.getInstance().currentScreen.height)
                .setDefaultValue(() -> 0)
                        .build()
        );

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.literal("Player radar"),
                        config.getInstance().radarEnabled)
                .setSaveConsumer(enabled -> {
                    config.getInstance().radarEnabled = enabled;
                    config.getInstance().saveConfig();
                }).setDefaultValue(() -> true)
                .build()
        );


        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.literal("dev"),
                        config.getInstance().isDev())
                .setSaveConsumer(enabled -> {
                    config.getInstance().setDev(enabled);
                    config.getInstance().saveConfig();
                }).setDefaultValue(() -> false)
                .build()
        );

        general.addEntry(entryBuilder.startStrField(
                                Text.literal("API URL"),
                                config.getInstance().getApiURL()
                        )
                        .setSaveConsumer(text -> {
                            config.getInstance().setApiURL(text);
                            config.getInstance().saveConfig();
                        }).setDefaultValue(() -> "https://api.earthmc.net/v3/aurora")
                        .build()
        );


        general.addEntry(entryBuilder.startStrField(
                                Text.literal("MAP URL"),
                                config.getInstance().getMapURL()
                        )
                        .setSaveConsumer(text -> {
                            config.getInstance().setMapUrl(text);
                            config.getInstance().saveConfig();
                        }).setDefaultValue(() -> "https://map.earthmc.net/")
                        .build()
        );


        general.addEntry(entryBuilder.startStrField(
                                Text.literal("STAFF REPO URL"),
                                config.getInstance().getStaffRepoURL()
                        )
                        .setSaveConsumer(text -> {
                            config.getInstance().setStaffRepoURL(text);
                            config.getInstance().saveConfig();
                        }).setDefaultValue(() -> "https://raw.githubusercontent.com/jwkerr/staff/master/staff.json")
                        .build()
        );



        return builder.build();
    }


}

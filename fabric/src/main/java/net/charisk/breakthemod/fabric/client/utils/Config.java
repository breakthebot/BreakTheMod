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

package net.charisk.breakthemod.fabric.client.utils;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.charisk.breakthemod.utils.config;
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
                .build()
        );

        general.addEntry(entryBuilder.startEnumSelector(
                Text.literal("Widget Position"),
                config.WidgetPosition.class,
                config.getInstance().getWidgetPosition()
        ).setSaveConsumer(position -> {
            config.getInstance().setWidgetPosition(position);
            config.getInstance().saveConfig();
        }).build());

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
                        .build()
        );

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.literal("Player radar"),
                        config.getInstance().radarEnabled)
                .setSaveConsumer(enabled -> {
                    config.getInstance().radarEnabled = enabled;
                    config.getInstance().saveConfig();
                })
                .build()
        );


        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.literal("dev"),
                        config.getInstance().dev)
                .setSaveConsumer(enabled -> {
                    config.getInstance().dev = enabled;
                    config.getInstance().saveConfig();
                })
                .build()
        );


        return builder.build();
    }

    public enum WidgetPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        CUSTOM
    }
}

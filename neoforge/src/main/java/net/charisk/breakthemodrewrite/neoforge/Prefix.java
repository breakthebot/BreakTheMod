/*
 * This file is part of BreakTheMod.
 *
 * BreakTheMod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BreakTheMod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BreakTheMod. If not, see <https://www.gnu.org/licenses/>.
 */

package net.charisk.breakthemodrewrite.neoforge;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Style;

public class Prefix {

    public static TextColor getColorFromHex(String hex) {
        if (!hex.startsWith("#")) {
            throw new IllegalArgumentException("Invalid hex color format. Must start with #.");
        }
        try {
            int color = Integer.parseInt(hex.substring(1), 16);
            return TextColor.fromRgb(color);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex color format. Ensure it is in #RRGGBB format.", e);
        }
    }

    public static MutableComponent getPrefix() {
        MutableComponent prefix = Component.literal("")
                .append(Component.literal("B").setStyle(Style.EMPTY.withColor(getColorFromHex("#EAEAEA"))))
                .append(Component.literal("r").setStyle(Style.EMPTY.withColor(getColorFromHex("#EAEAEA"))))
                .append(Component.literal("e").setStyle(Style.EMPTY.withColor(getColorFromHex("#EAEAEA"))))
                .append(Component.literal("a").setStyle(Style.EMPTY.withColor(getColorFromHex("#EAEAEA"))))
                .append(Component.literal("k").setStyle(Style.EMPTY.withColor(getColorFromHex("#EAEAEA"))))
                .append(Component.literal("T").setStyle(Style.EMPTY.withColor(getColorFromHex("#4B56FF"))))
                .append(Component.literal("h").setStyle(Style.EMPTY.withColor(getColorFromHex("#4B56FF"))))
                .append(Component.literal("e").setStyle(Style.EMPTY.withColor(getColorFromHex("#4B56FF"))))
                .append(Component.literal("M").setStyle(Style.EMPTY.withColor(getColorFromHex("#FF8C1A"))))
                .append(Component.literal("o").setStyle(Style.EMPTY.withColor(getColorFromHex("#FF8C1A"))))
                .append(Component.literal("d").setStyle(Style.EMPTY.withColor(getColorFromHex("#FF8C1A"))))
                .append(Component.literal(">> ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF))));
        return prefix;
    }
}

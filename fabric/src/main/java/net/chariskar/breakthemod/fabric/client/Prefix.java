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

package net.chariskar.breakthemod.fabric.client;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class Prefix{

    public static TextColor getColorFromHex(String hex) {
        try {
            if (!hex.startsWith("#")) {
                throw new IllegalArgumentException("Invalid hex color format. Must start with #.");
            }
            int color = Integer.parseInt(hex.substring(1), 16);
            return TextColor.fromRgb(color);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex color format. Ensure it is in #RRGGBB format.", e);
        }
    }

    public static Text getPrefix() {
        String[][] segments = {
                {"Break", "#EAEAEA"},
                {"The", "#4B56FF"},
                {"Mod", "#FF8C1A"},
                {">> ", "#FFFFFF"}
        };

        MutableText prefix = Text.empty();
        for (String[] segment : segments) {
            String text = segment[0];
            int color = getColorFromHex(segment[1]).getRgb();

            for (char c : text.toCharArray()) {
                prefix.append(Text.literal(String.valueOf(c)).setStyle(Style.EMPTY.withColor(color)));
            }
        }

        return prefix;
    }

}
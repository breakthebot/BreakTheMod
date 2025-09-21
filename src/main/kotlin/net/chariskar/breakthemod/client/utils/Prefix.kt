/*
 * This file is part of breakthemodRewrite.
 *
 * breakthemodRewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * breakthemodRewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with breakthemodRewrite. If not, see <https://www.gnu.org/licenses/>.
 */
package net.chariskar.breakthemod.client.utils

import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor

class Prefix {
    fun getColorFromHex(hex: String): TextColor {
        try {
            require(hex.startsWith("#")) { "Invalid hex color format. Must start with #." }
            val color = hex.substring(1).toInt(16)
            return TextColor.fromRgb(color)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid hex color format. Ensure it is in #RRGGBB format.", e)
        }
    }

    val prefix: Text
        get() {
            val segments = arrayOf(
                arrayOf("Break", "#EAEAEA"),
                arrayOf("The", "#4B56FF"),
                arrayOf("Mod", "#FF8C1A"),
                arrayOf(">> ", "#FFFFFF")
            )

            val prefix = Text.empty()
            for (segment in segments) {
                val text: String = segment[0]
                val color = getColorFromHex(segment[1]).rgb

                for (c in text.toCharArray()) {
                    prefix.append(Text.literal(c.toString()).setStyle(Style.EMPTY.withColor(color)))
                }
            }

            return prefix
        }
}
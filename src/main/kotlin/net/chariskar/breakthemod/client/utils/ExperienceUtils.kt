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

// / Credit to https://github.com/jwkerr/Bottlet/blob/master/src/main/java/au/lupine/bottlet/api/Experience.java

package net.chariskar.breakthemod.client.utils

import net.minecraft.client.player.LocalPlayer
import kotlin.math.round

object ExperienceUtils {

    fun experience(
        player: LocalPlayer,
    ): Int = (
        experience(player.experienceLevel) +
            round(required(player.experienceLevel) * player.experienceProgress)
        ).toInt()

    fun experience(level: Int): Int {
        if (level > 30) return (4.5 * level * level - 162.5 * level + 2220).toInt()
        if (level > 15) return (2.5 * level * level - 40.5 * level + 360).toInt()
        return level * level + 6 * level
    }

    fun required(level: Int): Int {
        if (level >= 30) return level * 9 - 158
        if (level >= 15) return level * 5 - 38
        return level * 2 + 7
    }
}

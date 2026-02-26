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

package net.chariskar.breakthemod.client.utils

import java.time.Duration
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


object Timestamps {
    @OptIn(ExperimentalTime::class)
    fun parseTimestamp(timestamp: Long): MutableList<Long> {
        val current: Long = Clock.System.now().toEpochMilliseconds()
        val diff = current - timestamp
        val duration = Duration.ofMillis(diff)
        val days: Long = duration.toDays()
        val hours: Long = duration.toHours() % 24
        val minutes: Long = duration.toMinutes() % 60
        val list: MutableList<Long> = ArrayList<Long>()
        list.add(days)
        list.add(hours)
        list.add(minutes)
        return list
    }
}
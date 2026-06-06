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

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

data class Schedule(
    val name: String,
    val task: () -> Unit,
    val delay: Duration,
)

object Scheduler {
    private val scheduler = Executors.newScheduledThreadPool(2)

    fun schedule(schedule: Schedule) {
        scheduler.schedule(
            schedule.task,
            schedule.delay.inWholeSeconds,
            TimeUnit.SECONDS
        )
    }
}
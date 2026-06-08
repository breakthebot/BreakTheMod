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

import kotlinx.coroutines.*
import kotlin.time.Duration

/**
 * @param name The name of the routine.
 * @param task The task to be executed.
 * @param delay The delay or repetition time.
 * */
data class Schedule(
    val name: String,
    val task: suspend () -> Unit,
    val delay: Duration,
)

object Scheduler {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val tasks = mutableMapOf<String, Job>()

    fun schedule(schedule: Schedule) {
        val job = scope.launch {
            delay(schedule.delay)
            schedule.task()
            tasks.remove(schedule.name)
        }

        tasks[schedule.name] = job
    }

    fun scheduleRepeating(schedule: Schedule) {
        val job = scope.launch {
            while (isActive) {
                schedule.task()
                delay(schedule.delay)
            }
        }
        tasks[schedule.name] = job
    }

    fun cancel(name: String): Boolean {
        val job = tasks.remove(name) ?: return false
        job.cancel()
        return true
    }
}
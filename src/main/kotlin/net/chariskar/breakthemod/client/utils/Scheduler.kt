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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration

/**
 * Represents a schedule.
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

    private val _tasks = mutableMapOf<String, Job>()

    val tasks: HashMap<String, Job>
        get() = HashMap(_tasks)

    fun schedule(schedule: Schedule) {
        if (_tasks.containsKey(schedule.name)) return
        val job = scope.launch {
            delay(schedule.delay)
            schedule.task()
            tasks.remove(schedule.name)
        }

        tasks[schedule.name] = job
    }

    fun scheduleRepeating(schedule: Schedule) {
        if (_tasks.containsKey(schedule.name)) return
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
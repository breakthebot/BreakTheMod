package net.chariskar.breakthemod.client.utils

import java.time.Duration
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class Timestamps {
    companion object {
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

}
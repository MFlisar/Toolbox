package com.michaelflisar.toolbox.service.classes

import androidx.work.PeriodicWorkRequest
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.utils.TimeUtil
import java.util.Calendar

sealed class WorkerFrequency {

    companion object {
        val MINUTE = 1000L * 60L * 60L // 1 Minute
        val MIN_TIME_IN_FUTURE = MINUTE
    }

    abstract val durationInMillis: Long
    protected abstract fun getNextUpdateTime(): Calendar

    data class Week(
        private val dayOfWeek: Int = Calendar.SUNDAY,
        private val hour: Int = 8,
        private val minute: Int = 0
    ) : WorkerFrequency() {
        override val durationInMillis = 1000L * 60L * 60L * 24L * 7L
        override fun getNextUpdateTime(): Calendar {
            val calNextAlarm = Calendar.getInstance()
            calNextAlarm[Calendar.DAY_OF_WEEK] = dayOfWeek
            calNextAlarm[Calendar.HOUR_OF_DAY] = hour
            calNextAlarm[Calendar.MINUTE] = minute
            calNextAlarm[Calendar.SECOND] = 0
            calNextAlarm[Calendar.MILLISECOND] = 0
            return calNextAlarm
        }
    }

    data class Day(
        private val hour: Int = 8,
        private val minute: Int = 0
    ) : WorkerFrequency() {
        override val durationInMillis = 1000L * 60L * 60L * 24L
        override fun getNextUpdateTime(): Calendar {
            val calNextAlarm = Calendar.getInstance()
            calNextAlarm[Calendar.HOUR_OF_DAY] = hour
            calNextAlarm[Calendar.MINUTE] = minute
            calNextAlarm[Calendar.SECOND] = 0
            calNextAlarm[Calendar.MILLISECOND] = 0
            return calNextAlarm
        }
    }

    data class Hour(
        private val firstHour:Int  = 0,
        private val everyXHour: Int = 1
    ) : WorkerFrequency() {
        override val durationInMillis = 1000L * 60L * 60L * everyXHour
        override fun getNextUpdateTime(): Calendar {
            val calNextAlarm = Calendar.getInstance()
            calNextAlarm[Calendar.HOUR_OF_DAY] = firstHour
            calNextAlarm[Calendar.MINUTE] = 0
            calNextAlarm[Calendar.SECOND] = 0
            calNextAlarm[Calendar.MILLISECOND] = 0
            return calNextAlarm
        }
    }

    data object Minute : WorkerFrequency() {
        override val durationInMillis = 1000L * 60L * 60L
        override fun getNextUpdateTime(): Calendar {
            val calNextAlarm = Calendar.getInstance()
            calNextAlarm[Calendar.MINUTE] = 0
            calNextAlarm[Calendar.SECOND] = 0
            calNextAlarm[Calendar.MILLISECOND] = 0
            return calNextAlarm
        }
    }

    @Suppress("DEPRECATION")
    fun calcAlarm(factor: Int = 1): AlarmData {

        val now = Calendar.getInstance()
        val nextAlarm = getNextUpdateTime()

        L.d { "Alarm [frequency = $this | factor = $factor]: ${nextAlarm.time.toLocaleString()} (${now.time.toLocaleString()})" }

        var counter = 0
        while (nextAlarm.timeInMillis - now.timeInMillis <= MIN_TIME_IN_FUTURE) {
            val time = nextAlarm.timeInMillis + durationInMillis * factor
            nextAlarm.timeInMillis = time
            counter++
            if (counter == 100 && durationInMillis != MINUTE) {
                break
            }
        }

        L.d { "Alarm wird registriert: ${nextAlarm.time.toLocaleString()}" }

        return AlarmData(
            (nextAlarm.timeInMillis - Calendar.getInstance().timeInMillis).coerceAtLeast(0L),
            nextAlarm,
            durationInMillis.coerceAtLeast(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS)
        )
    }

    data class AlarmData(
        val initialDelayInMillis: Long,
        val nextTime: Calendar,
        val frequencyInMillis: Long
    ) {
        val info: String
            @Suppress("DEPRECATION")
            get() = "nextTime = ${nextTime.time.toLocaleString()} | frequency = ${TimeUtil.getTimeString(frequencyInMillis)} | initialDelay = ${TimeUtil.getTimeString(initialDelayInMillis)}"
    }
}
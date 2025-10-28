package com.michaelflisar.toolbox.utils

import com.michaelflisar.toolbox.extensions.LocalDateTimeSetup
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object TimeUtil {

    @OptIn(ExperimentalTime::class)
    fun parseMillis(millis: Long): LocalDateTime =
        Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())

    fun getTimeString(millis: Long): String {

        val totalSeconds = millis / 1000L

        //val duration = millis.toDuration(DurationUnit.MILLISECONDS)

        val seconds = totalSeconds % 60L
        val minutes = (totalSeconds / 60L) % 60L
        val hours = (totalSeconds / 60L / 60L) % 24L
        val days = totalSeconds / 60L / 60L / 24L

        val parts = listOf(days, hours, minutes, seconds)
        val units = listOf("d", "h", "m", "s")

        val info = ArrayList<String>()
        for (i in parts.indices) {
            if (parts[i] != 0L && info.isEmpty()) {
                info += parts[i].toString() + units[i]
            } else if (info.isNotEmpty()) {
                val padded = parts[i].toString().padStart(2, '0')
                info += padded + units[i]
            }
        }

        return info.joinToString(" ")
    }

    fun getSortedWeekDays(firstDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek): List<DayOfWeek> {
        val days = DayOfWeek.entries.toMutableList()
        while (days.first() != firstDayOfWeek) {
            val last = days.removeAt(days.lastIndex)
            days.add(0, last)
        }
        return days
    }

    fun getFirstWeekDay() = LocalDateTimeSetup.current.firstDayOfWeek

    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    fun date(year: Int, week: Int, day: DayOfWeek): LocalDate {
        // Erster Tag des Jahres
        val jan1 = LocalDate(year, 1, 1)
        // Wochentag des ersten Tages
        val jan1DayOfWeek = jan1.dayOfWeek.ordinal
        val firstDayOfWeek = getSortedWeekDays().first().ordinal
        // Offset zur ersten Woche
        val daysOffset = ((firstDayOfWeek - jan1DayOfWeek + 7) % 7)
        // Erster Tag der ersten Kalenderwoche
        val firstWeekStart = jan1.plus(daysOffset.toLong(), DateTimeUnit.DAY)
        // Zieltag berechnen
        val targetDate = firstWeekStart.plus(
            ((week - 1) * 7 + (day.ordinal - firstDayOfWeek)).toLong(),
            DateTimeUnit.DAY
        )
        return targetDate
    }

    fun weeksOfYear(year: Int): Int {
        return if (isLeapYear(year)) 53 else 52
    }

    fun daysOfYear(year: Int): Int {
        return if (isLeapYear(year)) 366 else 365
    }
}
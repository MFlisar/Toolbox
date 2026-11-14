package com.michaelflisar.toolbox.utils

import com.michaelflisar.toolbox.extensions.LocalDateTimeSetup
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object TimeUtil {

    @OptIn(ExperimentalTime::class)
    fun parseMillis(millis: Long): LocalDateTime =
        Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())

    fun getTimeString(
        millis: Long,
        secondFractionDigits: Int = 0,
    ): String {

        val totalSeconds = millis / 1000L

        val seconds = totalSeconds % 60L
        val minutes = (totalSeconds / 60L) % 60L
        val hours = (totalSeconds / 60L / 60L) % 24L
        val days = totalSeconds / 60L / 60L / 24L

        val parts = ArrayList<String>()

        if (days > 0) {
            parts += "${days}d"
        }

        if (parts.isNotEmpty() || hours > 0) {
            parts += "${hours}h"
        }

        if (parts.isNotEmpty() || minutes > 0) {
            parts += if (parts.isEmpty()) {
                "${minutes}m"
            } else {
                "${minutes.toString().padStart(2, '0')}m"
            }
        }

        val secondsText =
            if (secondFractionDigits > 0) {

                val secondsWithFraction =
                    (millis % 60_000) / 1000.0

                val factor = 10.0.pow(secondFractionDigits)
                val rounded =
                    (secondsWithFraction * factor).roundToInt() / factor

                val text = rounded
                    .toString()
                    .removeSuffix(".0")

                val formatted =
                    if (parts.isEmpty()) {
                        text
                    } else {
                        text.padStart(
                            2 + 1 + secondFractionDigits,
                            '0'
                        )
                    }

                "${formatted}s"

            } else {

                val value =
                    if (parts.isEmpty()) {
                        seconds.toString()
                    } else {
                        seconds.toString().padStart(2, '0')
                    }

                "${value}s"
            }

        parts += secondsText

        return parts.joinToString(" ")
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
    fun getLastWeekDay() = LocalDateTimeSetup.current.lastDayOfWeek

    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    fun date(year: Int, week: Int, day: DayOfWeek, firstDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek): LocalDate {
        val week1Start = startOfWeek1(year, firstDayOfWeek)
        val firstDayIndex = getSortedWeekDays(firstDayOfWeek).first().ordinal
        val dayOffset = (day.ordinal - firstDayIndex + 7) % 7
        return week1Start.plus(((week - 1) * 7 + dayOffset).toLong(), DateTimeUnit.DAY)
    }

    fun startOfWeek1(year: Int, firstDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek): LocalDate {
        // Woche 1 ist die Woche, die den 4. Januar enthält
        val firstDayIndex = getSortedWeekDays(firstDayOfWeek).first().ordinal
        val jan4 = LocalDate(year, 1, 4)
        val diff = (jan4.dayOfWeek.ordinal - firstDayIndex + 7) % 7
        return jan4.minus(diff, DateTimeUnit.DAY)
    }

    fun weeksOfYear(year: Int, firstDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek): Int {
        val startThis = startOfWeek1(year, firstDayOfWeek)
        val startNext = startOfWeek1(year + 1, firstDayOfWeek)
        val days = startThis.daysUntil(startNext)
        return days / 7 // 52 oder 53
    }

    fun daysOfYear(year: Int): Int {
        return if (isLeapYear(year)) 366 else 365
    }
}
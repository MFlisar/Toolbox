package com.michaelflisar.toolbox.extensions

import com.michaelflisar.toolbox.utils.TimeUtil
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(timezone: TimeZone = TimeZone.currentSystemDefault()) = Clock.System.now().toLocalDateTime(timezone)

fun LocalDate.Companion.now(timezone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timezone).date
fun LocalTime.Companion.now(timezone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timezone).time
@OptIn(ExperimentalTime::class)
fun LocalDateTime.millis(timezone: TimeZone = TimeZone.currentSystemDefault()) = toInstant(timezone).toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
fun LocalDateTime.plus(duration: Duration, timezone: TimeZone = TimeZone.currentSystemDefault()) : LocalDateTime {
    val durationThis = millis(timezone)
    return Instant
        .fromEpochMilliseconds(durationThis + duration.inWholeMilliseconds)
        .toLocalDateTime(timezone)
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.minus(duration: Duration, timezone: TimeZone = TimeZone.currentSystemDefault()) = plus(-duration, timezone)

@OptIn(ExperimentalTime::class)
fun LocalDateTime.plus(unit: DateTimeUnit, offset: Int, timezone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    val instant = toInstant(timezone)
    val instantNext = instant.plus(offset, unit, timezone)
    return instantNext.toLocalDateTime(timezone)
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.minus(unit: DateTimeUnit, offset: Int, timezone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    val instant = toInstant(timezone)
    val instantNext = instant.minus(offset, unit, timezone)
    return instantNext.toLocalDateTime(timezone)
}

@OptIn(ExperimentalTime::class)
fun LocalDate.plus(duration: Duration, timezone: TimeZone = TimeZone.currentSystemDefault()) =
    LocalDateTime(this, LocalTime(0,0)).plus(duration, timezone).date

@OptIn(ExperimentalTime::class)
fun LocalDate.minus(duration: Duration, timezone: TimeZone = TimeZone.currentSystemDefault()) =
    LocalDateTime(this, LocalTime(0,0)).minus(duration, timezone).date

@OptIn(ExperimentalTime::class)
fun LocalDate.plus(unit: DateTimeUnit, offset: Int, timezone: TimeZone = TimeZone.currentSystemDefault()) =
    LocalDateTime(this, LocalTime(0,0)).plus(unit, offset, timezone).date

@OptIn(ExperimentalTime::class)
fun LocalDate.minus(unit: DateTimeUnit, offset: Int, timezone: TimeZone = TimeZone.currentSystemDefault()) =
    LocalDateTime(this, LocalTime(0,0)).minus(unit, offset, timezone).date

fun LocalDate.startOfWeek(
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
): LocalDate {
    val diff = (this.dayOfWeek.ordinal - firstDayOfWeek.ordinal + 7) % 7
    return this.minus(diff, DateTimeUnit.DAY)
}

fun LocalDate.endOfWeek(
    lastDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY
): LocalDate {
    val diff = (this.dayOfWeek.ordinal + lastDayOfWeek.ordinal + 7) % 7
    return this.plus(diff, DateTimeUnit.DAY)
}

fun LocalDate.startOfMonth(): LocalDate {
    return LocalDate(year, month, 1)
}

fun LocalDate.endOfMonth(): LocalDate {
    return LocalDate(year, month, 1).plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
}

fun LocalDate.startOfYear(): LocalDate {
    return LocalDate(year, 1, 1)
}

fun LocalDate.endOfYear(): LocalDate {
    return LocalDate(year, 12, 31)
}

fun LocalDate.Companion.fromCalendarWeek(year: Int, week: Int, day: DayOfWeek, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
    // Erster Tag des Jahres
    val jan1 = LocalDate(year, 1, 1)
    // Wochentag des ersten Tages
    val jan1DayOfWeek = jan1.dayOfWeek.ordinal
    val firstDayOfWeek = TimeUtil.getSortedWeekDays(firstDayOfWeek).first().ordinal
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

fun LocalDate.calendarWeek(firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): Int {
    val firstDayOfWeek = TimeUtil.getSortedWeekDays(firstDayOfWeek).first().ordinal
    val jan1 = LocalDate(this.year, 1, 1)
    val jan1DayOfWeek = jan1.dayOfWeek.ordinal
    val daysOffset = ((firstDayOfWeek - jan1DayOfWeek + 7) % 7)
    val firstWeekStart = jan1.plus(daysOffset.toLong(), DateTimeUnit.DAY)
    val daysSinceFirstWeek = firstWeekStart.daysUntil(this)
    return if (daysSinceFirstWeek < 0) 1 else (daysSinceFirstWeek / 7) + 1
}


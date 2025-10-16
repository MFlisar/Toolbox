package com.michaelflisar.toolbox.extensions

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
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
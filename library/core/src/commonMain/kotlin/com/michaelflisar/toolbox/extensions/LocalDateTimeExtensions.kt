package com.michaelflisar.toolbox.extensions

import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelable
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.utils.TimeUtil
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class LocalDateTimeSetup(
    val firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val timezone: TimeZone = TimeZone.currentSystemDefault(),
) {

    companion object {
        var current = LocalDateTimeSetup()
    }

    val lastDayOfWeek: DayOfWeek
        get() {
            val days = TimeUtil.getSortedWeekDays(firstDayOfWeek)
            return days[days.size - 1]
        }
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(timezone: TimeZone = LocalDateTimeSetup.current.timezone) =
    Clock.System.now().toLocalDateTime(timezone)

fun LocalDate.Companion.now(timezone: TimeZone = LocalDateTimeSetup.current.timezone) =
    LocalDateTime.now(timezone).date

fun LocalTime.Companion.now(timezone: TimeZone = LocalDateTimeSetup.current.timezone) =
    LocalDateTime.now(timezone).time

@OptIn(ExperimentalTime::class)
fun LocalDateTime.millis(timezone: TimeZone = LocalDateTimeSetup.current.timezone) =
    toInstant(timezone).toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.fromMillis(
    millis: Long,
    timezone: TimeZone = LocalDateTimeSetup.current.timezone,
): LocalDateTime {
    return Instant.fromEpochMilliseconds(millis).toLocalDateTime(timezone)
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.plus(
    duration: Duration,
    timezone: TimeZone = LocalDateTimeSetup.current.timezone,
): LocalDateTime {
    val durationThis = millis(timezone)
    return Instant
        .fromEpochMilliseconds(durationThis + duration.inWholeMilliseconds)
        .toLocalDateTime(timezone)
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.minus(
    duration: Duration,
    timezone: TimeZone = LocalDateTimeSetup.current.timezone,
) = plus(-duration, timezone)

@OptIn(ExperimentalTime::class)
fun LocalDateTime.plus(
    unit: DateTimeUnit,
    offset: Int,
    timezone: TimeZone = LocalDateTimeSetup.current.timezone,
): LocalDateTime {
    val instant = toInstant(timezone)
    val instantNext = instant.plus(offset, unit, timezone)
    return instantNext.toLocalDateTime(timezone)
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.minus(
    unit: DateTimeUnit,
    offset: Int,
    timezone: TimeZone = LocalDateTimeSetup.current.timezone,
): LocalDateTime {
    val instant = toInstant(timezone)
    val instantNext = instant.minus(offset, unit, timezone)
    return instantNext.toLocalDateTime(timezone)
}

@OptIn(ExperimentalTime::class)
fun LocalDate.plus(duration: Duration, timezone: TimeZone = LocalDateTimeSetup.current.timezone) =
    LocalDateTime(this, LocalTime(0, 0)).plus(duration, timezone).date

@OptIn(ExperimentalTime::class)
fun LocalDate.minus(duration: Duration, timezone: TimeZone = LocalDateTimeSetup.current.timezone) =
    LocalDateTime(this, LocalTime(0, 0)).minus(duration, timezone).date

@OptIn(ExperimentalTime::class)
fun LocalDate.plus(
    unit: DateTimeUnit,
    offset: Int,
    timezone: TimeZone = LocalDateTimeSetup.current.timezone,
) =
    LocalDateTime(this, LocalTime(0, 0)).plus(unit, offset, timezone).date

@OptIn(ExperimentalTime::class)
fun LocalDate.minus(
    unit: DateTimeUnit,
    offset: Int,
    timezone: TimeZone = LocalDateTimeSetup.current.timezone,
) =
    LocalDateTime(this, LocalTime(0, 0)).minus(unit, offset, timezone).date

fun LocalDate.startOfWeek(
    firstDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek,
): LocalDate {
    val diff = (this.dayOfWeek.ordinal - firstDayOfWeek.ordinal + 7) % 7
    return this.minus(diff, DateTimeUnit.DAY)
}

fun LocalDate.endOfWeek(
    lastDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek,
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

fun LocalDate.Companion.fromCalendarWeek(
    year: Int,
    week: Int,
    day: DayOfWeek,
    firstDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek,
): LocalDate {
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

fun LocalDate.calendarWeek(firstDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek): Int {
    val firstDayOfWeek = TimeUtil.getSortedWeekDays(firstDayOfWeek).first().ordinal
    val jan1 = LocalDate(this.year, 1, 1)
    val jan1DayOfWeek = jan1.dayOfWeek.ordinal
    val daysOffset = ((firstDayOfWeek - jan1DayOfWeek + 7) % 7)
    val firstWeekStart = jan1.plus(daysOffset.toLong(), DateTimeUnit.DAY)
    val daysSinceFirstWeek = firstWeekStart.daysUntil(this)
    return if (daysSinceFirstWeek < 0) 1 else (daysSinceFirstWeek / 7) + 1
}

@Parcelize
data class YearWeek(
    val year: Int,
    val week: Int,
    val firstDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek,
) : Comparable<YearWeek>, Parcelable {

    override fun compareTo(other: YearWeek): Int {
        return if (this.year != other.year) {
            this.year.compareTo(other.year)
        } else {
            this.week.compareTo(other.week)
        }
    }

    @IgnoredOnParcel
    val date = LocalDate.fromCalendarWeek(year, week, firstDayOfWeek)

    fun between(other: YearWeek): Int {
        val startYear = minOf(this.year, other.year)
        val endYear = maxOf(this.year, other.year)
        val startWeek = if (this.year < other.year) this.week else other.week
        val endWeek = if (this.year > other.year) this.week else other.week

        var weeks = 0
        for (year in (startYear + 1) until endYear) {
            weeks += if (TimeUtil.isLeapYear(year)) 53 else 52
        }
        val weeksInStartYear = if (TimeUtil.isLeapYear(startYear)) 53 else 52
        //val weeksInEndYear = if (TimeUtil.isLeapYear(endYear)) 53 else 52

        weeks += (weeksInStartYear - startWeek) + endWeek
        return weeks
    }

    fun next(): YearWeek {
        val weeksInYear = if (TimeUtil.isLeapYear(year)) 53 else 52
        return if (week < weeksInYear) {
            YearWeek(year, week + 1)
        } else {
            YearWeek(year + 1, 1)
        }
    }

    fun previous(): YearWeek {
        return if (week > 1) {
            YearWeek(year, week - 1)
        } else {
            val previousYear = year - 1
            val weeksInPreviousYear = if (TimeUtil.isLeapYear(previousYear)) 53 else 52
            YearWeek(previousYear, weeksInPreviousYear)
        }
    }

    fun toDate(dayOfWeek: DayOfWeek): LocalDate {
        return LocalDate.fromCalendarWeek(year, week, dayOfWeek, firstDayOfWeek)
    }
}

fun LocalDate.yearWeek(firstDayOfWeek: DayOfWeek = LocalDateTimeSetup.current.firstDayOfWeek): YearWeek {
    val week = calendarWeek(firstDayOfWeek)
    var year = this.year
    // Sonderfall: Woche gehört zum Vorjahr
    if (this.month.number == 1 && week >= 52) {
        year -= 1
    }
    // Sonderfall: Woche gehört zum Folgejahr
    if (this.month.number == 12 && week == 1) {
        year += 1
    }
    return YearWeek(year, week, firstDayOfWeek)
}

data class DatePeriodWithYears(val years: Int, val months: Int, val days: Int) {
    fun isZero() = years == 0 && months == 0 && days == 0
}

fun LocalDate.getTimeBetween(other: LocalDate): DatePeriodWithYears {
    val start = if (this <= other) this else other
    val end = if (this > other) this else other

    var years = end.year - start.year
    var monthStart = start.plus(years, DateTimeUnit.YEAR)
    if (monthStart > end) {
        years -= 1
        monthStart = start.plus(years, DateTimeUnit.YEAR)
    }

    var months = end.month.number - monthStart.month.number
    if (months < 0) months += 12
    var dayStart = monthStart.plus(months, DateTimeUnit.MONTH)
    if (dayStart > end) {
        months -= 1
        dayStart = monthStart.plus(months, DateTimeUnit.MONTH)
    }

    val days = dayStart.daysUntil(end)

    return DatePeriodWithYears(years, months, days)
}
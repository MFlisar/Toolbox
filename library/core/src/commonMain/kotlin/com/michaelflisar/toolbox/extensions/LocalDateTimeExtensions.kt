package com.michaelflisar.toolbox.extensions

import kotlinx.datetime.TimeZone
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
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
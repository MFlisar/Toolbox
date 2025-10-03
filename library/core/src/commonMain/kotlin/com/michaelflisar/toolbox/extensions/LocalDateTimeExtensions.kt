package com.michaelflisar.toolbox.extensions

import kotlinx.datetime.TimeZone
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(timezone: TimeZone = TimeZone.currentSystemDefault()) = Clock.System.now().toLocalDateTime(timezone)

fun LocalDate.Companion.now(timezone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timezone).date
fun LocalTime.Companion.now(timezone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timezone).time
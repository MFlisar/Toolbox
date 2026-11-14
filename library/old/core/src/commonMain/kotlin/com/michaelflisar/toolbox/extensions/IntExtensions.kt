package com.michaelflisar.toolbox.extensions

import kotlin.math.abs

val Int.asDurationString: String
    get() {
        val hours = abs(this) / 3600
        val minutes = (abs(this) % 3600) / 60
        val seconds = abs(this) % 60

        val paddedHours = hours.toString().padStart(2, '0')
        val paddedMinutes = minutes.toString().padStart(2, '0')
        val paddedSeconds = seconds.toString().padStart(2, '0')
        val prefix = if (this < 0) "-" else ""

        return prefix + if (hours == 0) {
            "$paddedMinutes:$paddedSeconds"
        } else {
            "$paddedHours:$paddedMinutes:$paddedSeconds"
        }
    }
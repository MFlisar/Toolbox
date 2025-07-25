package com.michaelflisar.toolbox.extensions

val Int.asDurationString: String
    get() {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = this % 60

        val paddedHours = hours.toString().padStart(2, '0')
        val paddedMinutes = minutes.toString().padStart(2, '0')
        val paddedSeconds = seconds.toString().padStart(2, '0')

        return if (hours == 0) {
            "$paddedMinutes:$paddedSeconds"
        } else {
            "$paddedHours:$paddedMinutes:$paddedSeconds"
        }
    }
package com.michaelflisar.toolbox.utils

object TimeUtil {

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
}
package com.michaelflisar.toolbox.backup

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object BackupDefaults {

    @OptIn(ExperimentalTime::class)
    fun getDefaultBackupFileName(appName: String, extension: String): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val date = now.year.toString().padStart(4, '0') + "-" +
                now.month.number.toString().padStart(2, '0') + "-" +
                now.day.toString().padStart(2, '0') + " " +
                now.hour.toString().padStart(2, '0') + "-" +
                now.minute.toString().padStart(2, '0') + "-" +
                now.second.toString().padStart(2, '0')
        return "$appName $date.$extension"
    }
}
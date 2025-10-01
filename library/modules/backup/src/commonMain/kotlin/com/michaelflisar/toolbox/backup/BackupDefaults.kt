package com.michaelflisar.toolbox.backup

import com.michaelflisar.composedialogs.dialogs.frequency.classes.Frequency
import com.michaelflisar.toolbox.backup.ui.BackupDialog
import com.michaelflisar.toolbox.extensions.now
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

object BackupDefaults {

    @OptIn(ExperimentalTime::class)
    fun getInitialDay(
        datetime: LocalDateTime,
        frequency: Frequency
    ) : Duration {
        val start = datetime
        val end = frequency.calcNextOccurrence(start.date)
        val timezone = TimeZone.currentSystemDefault()
        val initialDelayInSeconds = (end.toInstant(timezone).epochSeconds - start.toInstant(timezone).epochSeconds).coerceAtLeast(0L)
        return initialDelayInSeconds.seconds
    }

    @OptIn(ExperimentalTime::class)
    fun getDefaultBackupFileName(appName: String, extension: String, auto: Boolean): BackupDialog.FileName {
        val now = LocalDateTime.now()
        val date = now.year.toString().padStart(4, '0') + "-" +
                now.month.number.toString().padStart(2, '0') + "-" +
                now.day.toString().padStart(2, '0') + " " +
                now.hour.toString().padStart(2, '0') + "-" +
                now.minute.toString().padStart(2, '0') + "-" +
                now.second.toString().padStart(2, '0')
        return BackupDialog.FileName("${if (auto) "auto " else ""}$appName $date", extension)
    }
}
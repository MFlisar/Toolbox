package com.michaelflisar.toolbox.backup.classes

import com.michaelflisar.composedialogs.dialogs.frequency.classes.Frequency
import org.jetbrains.compose.resources.StringResource

class AutoBackupConfig(
    val appName: StringResource,
    val frequencyData: () -> String,
    val backupPathData: () -> String
) {
    fun getFrequency() : Frequency? {
        val frequencyData = frequencyData()
        if (frequencyData.isEmpty())
            return null
        return Frequency.deserialize(frequencyData)
    }
}
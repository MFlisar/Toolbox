package com.michaelflisar.toolbox.backup.classes

import com.michaelflisar.composedialogs.dialogs.frequency.classes.Frequency

class AutoBackupConfig(
    val appName: String,
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
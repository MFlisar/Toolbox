package com.michaelflisar.toolbox.backup.classes

import androidx.compose.runtime.Stable
import com.michaelflisar.composedialogs.dialogs.frequency.classes.Frequency
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.backup.BackupDefaults

@Stable
class AutoBackupConfig(
    val appName: String,
    val frequencyData: suspend () -> String,
    val backupPathData: suspend () -> String
) {
    suspend fun getFrequency() : Frequency? {
        val frequencyData = frequencyData()
        L.logIf { BackupDefaults.LOG }?.d {
            "AutoBackupConfig.getFrequency: loaded frequency setting, configured=${frequencyData.isNotEmpty()}"
        }
        if (frequencyData.isEmpty()) {
            return null
        }
        return Frequency.deserialize(frequencyData).also { frequency ->
            L.logIf { BackupDefaults.LOG }?.d {
                "AutoBackupConfig.getFrequency: frequency deserialized=${frequency}"
            }
        }
    }
}
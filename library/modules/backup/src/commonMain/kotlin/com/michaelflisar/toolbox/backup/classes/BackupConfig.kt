package com.michaelflisar.toolbox.backup.classes

import androidx.compose.runtime.Stable
import com.michaelflisar.toolbox.backup.ZipFileContent

@Stable
class BackupConfig(
    val backupContent: List<ZipFileContent>,
    val extension: String = "zip",
    val addToPrefs: Boolean = true,
)
package com.michaelflisar.toolbox.backup.classes

import com.michaelflisar.toolbox.backup.ZipFileContent

class BackupConfig(
    val backupContent: List<ZipFileContent>,
    val extension: String = "zip",
    val addToPrefs: Boolean = true,
)
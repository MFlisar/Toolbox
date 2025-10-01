package com.michaelflisar.toolbox.backup

class BackupConfig(
    val backupContent: List<ZipFileContent>,
    val extension: String = "zip",
    val addToPrefs: Boolean = true,
)
package com.michaelflisar.toolbox.backup.classes

import androidx.compose.runtime.Stable
import com.michaelflisar.toolbox.backup.ZipFileContent

@Stable
class BackupConfig(
    val backupContent: List<ZipFileContent>,
    val backupMetadata: BackupMetadata,
    /**
     * Validates imported metadata. Legacy backups pass null; by default all backups are accepted.
     */
    val backupValidator: (metadata: BackupMetadata?) -> Boolean = { true },
    val extension: String = "zip",
    val addToPrefs: Boolean = true,
)
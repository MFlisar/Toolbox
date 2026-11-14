package com.michaelflisar.toolbox.backup

import androidx.compose.runtime.Stable
import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupConfig
import io.github.vinceglb.filekit.PlatformFile

@Stable
expect class BackupManagerImpl(
    config: BackupConfig,
    autoBackupConfig: AutoBackupConfig? = null,
) {

    val config: BackupConfig
    val autoBackupConfig: AutoBackupConfig?

    fun onBackupRestored()
    suspend fun backup(
        files: List<ZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable?

    suspend fun restore(
        files: List<ZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable?

    suspend fun getAutoBackupDirectoryData(directory: PlatformFile): String
    fun getAutoBackupDirectoryPath(directoryData: String): String

    suspend fun getAutoBackupFileName(): String
    fun onSettingsChanged()
    fun onEnqueueNextAutoBackup()

}
package com.michaelflisar.toolbox.backup

import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupConfig
import io.github.vinceglb.filekit.PlatformFile

expect class BackupManagerImpl(
    config: BackupConfig,
    autoBackupConfig: AutoBackupConfig? = null
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

    fun getAutoBackupFileName(): String
    fun onSettingsChanged()
    fun onEnqueueNextAutoBackup()

}
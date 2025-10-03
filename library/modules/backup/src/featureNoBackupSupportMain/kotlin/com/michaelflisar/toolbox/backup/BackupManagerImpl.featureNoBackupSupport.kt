package com.michaelflisar.toolbox.backup

import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupConfig
import io.github.vinceglb.filekit.PlatformFile

actual class BackupManagerImpl actual constructor(
    config: BackupConfig,
    autoBackupConfig: AutoBackupConfig?,
) {
    init {
        throw RuntimeException("BackupManager is not supported on this platform")
    }

    actual val config: BackupConfig
        get() = TODO("Not yet implemented")
    actual val autoBackupConfig: AutoBackupConfig?
        get() = TODO("Not yet implemented")

    actual fun onBackupRestored() {
    }

    actual suspend fun backup(
        files: List<ZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        TODO("Not yet implemented")
    }

    actual suspend fun restore(
        files: List<ZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        TODO("Not yet implemented")
    }

    actual fun getAutoBackupFileName(): String {
        TODO("Not yet implemented")
    }

    actual fun onSettingsChanged() {
    }

    actual fun onEnqueueNextAutoBackup() {
    }
}
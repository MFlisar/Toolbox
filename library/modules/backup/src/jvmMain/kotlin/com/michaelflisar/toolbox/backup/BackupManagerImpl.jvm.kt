package com.michaelflisar.toolbox.backup

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupConfig
import com.michaelflisar.toolbox.utils.JvmUtil
import com.michaelflisar.toolbox.zip.JavaZipFile
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import com.michaelflisar.toolbox.zip.JavaZipManager
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class BackupManagerImpl actual constructor(
    actual val config: BackupConfig,
    actual val autoBackupConfig: AutoBackupConfig?,
) {

    actual fun onBackupRestored() {
        // TODO
        // app neu starten oder alle Daten neu laden...
        JvmUtil.restartApp()
    }

    actual suspend fun backup(
        files: List<JavaZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        return try {
            val result = withContext(Dispatchers.IO) {
                // Zip direkt an der backup uri erstellen
                JavaZipManager.zip(files, JavaZipFile(backupFile.file))
            }
            if (result.isSuccess) {
                null
            } else {
                result.exceptionOrNull()!!
            }
        } catch (e: Exception) {
            L.e(e)
            e
        }
    }

    actual suspend fun restore(
        files: List<JavaZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        return try {
            val result = withContext(Dispatchers.IO) {
                // backup uri direkt im parent folder entpacken
                JavaZipManager.unzip(
                    JavaZipFile(backupFile.file),
                    files,
                    replaceExistingFiles = true
                )
            }
            if (result.isSuccess) {
                null
            } else {
                result.exceptionOrNull()!!
            }
        } catch (e: Exception) {
            L.e(e)
            e
        }
    }

    actual fun getAutoBackupFileName(): String {
        // --
        return ""
    }

    actual fun onSettingsChanged() {
        // not supported on jvm currently
    }

    actual fun onEnqueueNextAutoBackup() {
        // not supported on jvm currently
    }
}
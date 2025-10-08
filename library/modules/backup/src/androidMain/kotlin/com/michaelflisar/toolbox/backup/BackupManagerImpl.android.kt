package com.michaelflisar.toolbox.backup

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupConfig
import com.michaelflisar.toolbox.backup.internal.BackupServiceUtil
import com.michaelflisar.toolbox.backup.worker.BackupWorker
import com.michaelflisar.toolbox.extensions.now
import com.michaelflisar.toolbox.restartApp
import com.michaelflisar.toolbox.toAndroidUriInternal
import com.michaelflisar.toolbox.zip.AndroidZipFile
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import com.michaelflisar.toolbox.zip.JavaZipManager
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.getString
import kotlin.time.ExperimentalTime

actual class BackupManagerImpl actual constructor(
    actual val config: BackupConfig,
    actual val  autoBackupConfig: AutoBackupConfig?,
) {
    actual fun onBackupRestored() {
        AppContext.context().restartApp()
    }

    actual suspend fun backup(
        files: List<JavaZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        return try {
            val result = withContext(Dispatchers.IO) {
                // Zip direkt an der backup uri erstellen
                JavaZipManager.zip(files, AndroidZipFile(backupFile.toAndroidUriInternal()))
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
                    AndroidZipFile(backupFile.toAndroidUriInternal()),
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

    actual suspend fun getAutoBackupFileName( ): String {
        val backupFileName = BackupDefaults.getDefaultBackupFileName(
            getString(autoBackupConfig!!.appName),
            config.extension,
            true
        )
        return backupFileName.name
    }

    @OptIn(ExperimentalTime::class)
    actual fun onSettingsChanged( ) {
        BackupServiceUtil.createChannels()
        BackupWorker.cancelAutoWorker(AppContext.context())
        onEnqueueNextAutoBackup()
    }

    actual fun onEnqueueNextAutoBackup() {
        val frequency = autoBackupConfig?.getFrequency() ?: return
        val backupPath = autoBackupConfig.backupPathData().takeIf { it.isNotEmpty() } ?: return
        val backupFolderData = backupPath.toByteArray(Charsets.UTF_8)
        val initialDelay = BackupDefaults.getInitialDay(LocalDateTime.now(), frequency)
        val files = config.backupContent
        BackupWorker.enqueueAutoWorker(
            context = AppContext.context(),
            files = files,
            backupFolderData = backupFolderData,
            initialDelay = initialDelay
        )
    }
}
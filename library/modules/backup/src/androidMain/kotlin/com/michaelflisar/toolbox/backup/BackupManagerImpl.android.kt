package com.michaelflisar.toolbox.backup

import com.michaelflisar.kmp.platformcontext.PlatformApplicationContext
import com.michaelflisar.lumberjack.core.L
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
import io.github.vinceglb.filekit.bookmarkData
import io.github.vinceglb.filekit.fromBookmarkData
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

actual class BackupManagerImpl actual constructor(
    actual val config: BackupConfig,
    actual val autoBackupConfig: AutoBackupConfig?,
) {
    actual fun onBackupRestored() {
        PlatformApplicationContext.restartApp()
    }

    actual suspend fun backup(
        files: List<JavaZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupManagerImpl.backup: started, files=${files.size}"
        }
        return try {
            val result = withContext(Dispatchers.IO) {
                // Zip direkt an der backup uri erstellen
                JavaZipManager.zip(files, AndroidZipFile(backupFile.toAndroidUriInternal()))
            }
            if (result.isSuccess) {
                L.logIf { BackupDefaults.LOG }?.d { "BackupManagerImpl.backup: succeeded" }
                null
            } else {
                L.logIf { BackupDefaults.LOG }?.d {
                    "BackupManagerImpl.backup: failed, error=${result.exceptionOrNull()?.javaClass?.simpleName}"
                }
                result.exceptionOrNull()!!
            }
        } catch (e: Exception) {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupManagerImpl.backup: threw ${e.javaClass.simpleName}"
            }
            L.e(e)
            e
        }
    }

    actual suspend fun restore(
        files: List<JavaZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupManagerImpl.restore: started, files=${files.size}"
        }
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
                L.logIf { BackupDefaults.LOG }?.d { "BackupManagerImpl.restore: succeeded" }
                null
            } else {
                L.logIf { BackupDefaults.LOG }?.d {
                    "BackupManagerImpl.restore: failed, error=${result.exceptionOrNull()?.javaClass?.simpleName}"
                }
                result.exceptionOrNull()!!
            }
        } catch (e: Exception) {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupManagerImpl.restore: threw ${e.javaClass.simpleName}"
            }
            L.e(e)
            e
        }
    }

    actual suspend fun getAutoBackupDirectoryData(directory: PlatformFile): String {
        val bookmarkData = directory.bookmarkData()
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupManagerImpl.getAutoBackupDirectoryData: created bookmark, bytes=${bookmarkData.bytes.size}"
        }
        return bookmarkData.bytes.toString(Charsets.UTF_8)
    }

    actual fun getAutoBackupDirectoryPath(directoryData: String): String {
        val path = PlatformFile.fromBookmarkData(directoryData.toByteArray(Charsets.UTF_8)).path
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupManagerImpl.getAutoBackupDirectoryPath: resolved path, length=${path.length}"
        }
        return path
    }

    actual suspend fun getAutoBackupFileName(): String {
        val backupFileName = BackupDefaults.getDefaultBackupFileName(
            appName = autoBackupConfig!!.appName,
            extension = config.extension,
            auto = true
        )
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupManagerImpl.getAutoBackupFileName: generated automatic backup filename"
        }
        return backupFileName.name
    }

    actual fun onSettingsChanged() {
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupManagerImpl.onSettingsChanged: recreating automatic backup work"
        }
        BackupServiceUtil.createChannels()
        BackupWorker.cancelAutoWorker(PlatformApplicationContext)
        onEnqueueNextAutoBackup()
    }

    actual fun onEnqueueNextAutoBackup() {
        val frequency = autoBackupConfig?.getFrequency() ?: run {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupManagerImpl.onEnqueueNextAutoBackup: skipped, no frequency configured"
            }
            return
        }
        val backupPath = autoBackupConfig.backupPathData().takeIf { it.isNotEmpty() } ?: run {
            L.logIf { BackupDefaults.LOG }?.d {
                "BackupManagerImpl.onEnqueueNextAutoBackup: skipped, no backup directory configured"
            }
            return
        }
        val backupFolderData = backupPath.toByteArray(Charsets.UTF_8)
        val initialDelay = BackupDefaults.getInitialDay(LocalDateTime.now(), frequency)
        val files = config.backupContent
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupManagerImpl.onEnqueueNextAutoBackup: enqueueing, files=${files.size}, delay=$initialDelay"
        }
        BackupWorker.enqueueAutoWorker(
            context = PlatformApplicationContext,
            files = files,
            backupFolderData = backupFolderData,
            initialDelay = initialDelay
        )
        L.logIf { BackupDefaults.LOG }?.d {
            "BackupManagerImpl.onEnqueueNextAutoBackup: enqueue call completed"
        }
    }
}
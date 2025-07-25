package com.michaelflisar.toolbox.backup

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.restartApp
import com.michaelflisar.toolbox.zip.AndroidZipFile
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import com.michaelflisar.toolbox.zip.JavaZipManager
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual typealias ZipFileContentFile = JavaZipFileContent.File
actual typealias ZipFileContent = JavaZipFileContent
actual typealias BackupManager = AndroidBackupManager

actual typealias ActivityNotFoundException = android.content.ActivityNotFoundException

object AndroidBackupManager : IBackupManager<JavaZipFileContent.File, JavaZipFileContent> {

    override fun onBackupRestored() {
        AppContext.context().restartApp()
    }

    override suspend fun backup(
        files: List<JavaZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        return try {
            val result = withContext(Dispatchers.IO) {
                // Zip direkt an der backup uri erstellen
                JavaZipManager.zip(files, AndroidZipFile(backupFile.uri))
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

    override suspend fun restore(
        files: List<JavaZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        return try {
            val result = withContext(Dispatchers.IO) {
                // backup uri direkt im parent folder entpacken
                JavaZipManager.unzip(
                    AndroidZipFile(backupFile.uri),
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
}
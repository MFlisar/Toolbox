package com.michaelflisar.toolbox.backup

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupConfig
import com.michaelflisar.toolbox.backup.classes.BackupInspection
import com.michaelflisar.toolbox.backup.classes.BackupMetadata
import com.michaelflisar.toolbox.backup.classes.BackupValidationException
import com.michaelflisar.toolbox.backup.classes.inspect
import com.michaelflisar.toolbox.utils.JvmUtil
import com.michaelflisar.toolbox.extensions.now
import com.michaelflisar.toolbox.zip.JavaZipFile
import com.michaelflisar.toolbox.zip.JavaZipFileContent
import com.michaelflisar.toolbox.zip.JavaZipManager
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.bookmarkData
import io.github.vinceglb.filekit.fromBookmarkData
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class BackupManagerImpl actual constructor(
    actual val config: BackupConfig,
    actual val autoBackupConfig: AutoBackupConfig?,
) {

    actual fun onBackupRestored() {
        // TODO
        // app neu starten oder alle Daten neu laden...
        //JvmUtil.restartApp()
    }

    actual suspend fun backup(
        files: List<JavaZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        return try {
            check(config.backupMetadata.metadataVersion == BackupMetadata.CURRENT_METADATA_VERSION) {
                "Unsupported backup metadata version: ${config.backupMetadata.metadataVersion}"
            }
            val metadata = config.backupMetadata.copy(createdAt = LocalDateTime.now().toString())
            val metadataBytes = Json.encodeToString(metadata).encodeToByteArray()
            check(metadataBytes.size <= BackupMetadata.MAX_ENTRY_SIZE_BYTES) {
                "Backup metadata exceeds the maximum size of ${BackupMetadata.MAX_ENTRY_SIZE_BYTES} bytes"
            }
            val result = withContext(Dispatchers.IO) {
                JavaZipManager.zip(
                    files,
                    JavaZipFile(backupFile.file),
                    additionalEntries = mapOf(BackupMetadata.ZIP_ENTRY to metadataBytes)
                )
            }
            if (result.isSuccess) {
                null
            } else {
                result.exceptionOrNull()!!
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            L.e(e)
            e
        }
    }

    actual suspend fun inspectBackup(
        backupFile: PlatformFile,
    ): BackupInspection {
        return try {
            val result = JavaZipManager.readEntry(
                zipFile = JavaZipFile(backupFile.file),
                entryPath = BackupMetadata.ZIP_ENTRY,
                maxSizeBytes = BackupMetadata.MAX_ENTRY_SIZE_BYTES
            )
            val metadataBytes = result.getOrThrow()
            val metadata = metadataBytes?.decodeToString()?.let {
                Json { ignoreUnknownKeys = true }.decodeFromString<BackupMetadata>(it)
            }
            config.inspect(metadata)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            L.e(e)
            throw e
        }
    }

    actual suspend fun restore(
        files: List<JavaZipFileContent>,
        backupFile: PlatformFile,
    ): Throwable? {
        return try {
            val inspection = inspectBackup(backupFile)
            if (!inspection.isValid) return BackupValidationException()
            val result = withContext(Dispatchers.IO) {
                // backup uri direkt im parent folder entpacken
                JavaZipManager.unzip(
                    zipFile = JavaZipFile(backupFile.file),
                    files = files,
                    replaceExistingFiles = true
                )
            }
            if (result.isSuccess) {
                null
            } else {
                result.exceptionOrNull()!!
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            L.e(e)
            e
        }
    }

    actual suspend fun getAutoBackupDirectoryData(directory: PlatformFile): String {
        val bookmarkData = directory.bookmarkData()
        return bookmarkData.bytes.toString(Charsets.UTF_8)
    }

    actual fun getAutoBackupDirectoryPath(directoryData: String): String {
        return PlatformFile.fromBookmarkData(directoryData.toByteArray(Charsets.UTF_8)).path
    }

    actual suspend fun getAutoBackupFileName(): String {
        // --
        return ""
    }

    actual suspend fun onSettingsChanged() {
        // not supported on jvm currently
    }

    actual suspend fun onEnqueueNextAutoBackup() {
        // not supported on jvm currently
    }
}
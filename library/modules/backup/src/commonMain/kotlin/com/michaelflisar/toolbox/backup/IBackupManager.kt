package com.michaelflisar.toolbox.backup

import com.michaelflisar.toolbox.backup.classes.AutoBackupConfig
import com.michaelflisar.toolbox.zip.interfaces.IZipContent
import io.github.vinceglb.filekit.PlatformFile

expect class ActivityNotFoundException : RuntimeException

expect class ZipFileContentFile
expect sealed class ZipFileContent : IZipContent<ZipFileContentFile>

typealias BaseBackupManager = IBackupManager<ZipFileContentFile, ZipFileContent>

interface IBackupManager<ZipFileContentFile, ZipFileContent : IZipContent<ZipFileContentFile>> {

    val config: BackupConfig
    val autoBackupConfig: AutoBackupConfig?

    fun onBackupRestored()

    suspend fun backup(files: List<ZipFileContent>, backupFile: PlatformFile): Throwable?
    suspend fun restore(files: List<ZipFileContent>, backupFile: PlatformFile): Throwable?

    fun getAutoBackupFileName(): String
    fun onSettingsChanged()

    fun onEnqueueNextAutoBackup()

}


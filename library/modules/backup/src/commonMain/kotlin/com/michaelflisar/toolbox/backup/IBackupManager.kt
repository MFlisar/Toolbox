package com.michaelflisar.toolbox.backup

import com.michaelflisar.toolbox.zip.interfaces.IZipContent
import io.github.vinceglb.filekit.PlatformFile

expect class ActivityNotFoundException : RuntimeException

expect class ZipFileContentFile
expect sealed class ZipFileContent: IZipContent<ZipFileContentFile>
expect object BackupManager : IBackupManager<ZipFileContentFile, ZipFileContent>
{
    override fun onBackupRestored()
    override suspend fun backup(files: List<ZipFileContent>, backupFile: PlatformFile): Throwable?
    override suspend fun restore(files: List<ZipFileContent>, backupFile: PlatformFile): Throwable?
}

interface IBackupManager<ZipFileContentFile, ZipFileContent: IZipContent<ZipFileContentFile>> {

    fun onBackupRestored()

    suspend fun backup(files: List<ZipFileContent>, backupFile: PlatformFile): Throwable?
    suspend fun restore(files: List<ZipFileContent>, backupFile: PlatformFile): Throwable?

}


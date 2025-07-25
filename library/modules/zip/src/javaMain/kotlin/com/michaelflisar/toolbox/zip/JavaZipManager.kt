package com.michaelflisar.toolbox.zip

import com.michaelflisar.lumberjack.core.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object JavaZipManager : IZipManager<IJavaZipFile, JavaZipFileContent.File, JavaZipFileContent> {

    override suspend fun zip(
        files: List<JavaZipFileContent>,
        zipFile: IJavaZipFile,
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                ZipOutputStream(zipFile.openOutputStream()).use { zipOutputStream ->
                    files.map {
                        when (it) {
                            is JavaZipFileContent.File -> listOf(it)
                            is JavaZipFileContent.Folder -> it.listFiles()
                        }
                    }
                        .flatten()
                        .forEach { file ->
                            val relativePath = file.zipPath
                            val zipEntry = ZipEntry(relativePath)
                            zipOutputStream.putNextEntry(zipEntry)
                            file.openInputStream().use { inputStream ->
                                inputStream.copyTo(zipOutputStream)
                            }
                            zipOutputStream.closeEntry()
                        }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                L.e(e)
                Result.failure(e)
            }
        }
    }

    override suspend fun unzip(
        zipFile: IJavaZipFile,
        files: List<JavaZipFileContent>,
        replaceExistingFiles: Boolean,
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                ZipInputStream(zipFile.openInputStream()).use { zipInputStream ->

                    var entry = zipInputStream.nextEntry
                    while (entry != null) {

                        val targetFile =
                            files.find { it.getSubFile(entry.name) != null }?.getSubFile(entry.name)

                        // nur DATEIEN entpacken! diese erstellen die Ordner ggf. eh...
                        if (targetFile != null && targetFile.file.isFile) {
                            targetFile.mkParentDirs()
                            if (replaceExistingFiles) {
                                targetFile.delete()
                            }
                            targetFile.openOutputStream().use { outputStream ->
                                zipInputStream.copyTo(outputStream)
                            }
                        }

                        entry = zipInputStream.nextEntry
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                L.e(e)
                Result.failure(e)
            }
        }
    }
}
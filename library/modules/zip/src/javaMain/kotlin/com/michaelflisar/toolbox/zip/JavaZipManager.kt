package com.michaelflisar.toolbox.zip

import com.michaelflisar.lumberjack.core.L
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object JavaZipManager : IZipManager<IJavaZipFile, JavaZipFileContent.File, JavaZipFileContent> {

    override suspend fun zip(
        files: List<JavaZipFileContent>,
        zipFile: IJavaZipFile,
    ): Result<Unit> = zip(files, zipFile, emptyMap())

    suspend fun zip(
        files: List<JavaZipFileContent>,
        zipFile: IJavaZipFile,
        additionalEntries: Map<String, ByteArray>,
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                ZipOutputStream(zipFile.openOutputStream()).use { zipOutputStream ->
                    additionalEntries.forEach { (path, content) ->
                        zipOutputStream.putNextEntry(ZipEntry(path))
                        zipOutputStream.write(content)
                        zipOutputStream.closeEntry()
                    }
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
                if (e is CancellationException) throw e
                L.e(e)
                Result.failure(e)
            }
        }
    }

    suspend fun readEntry(
        zipFile: IJavaZipFile,
        entryPath: String,
        maxSizeBytes: Int,
    ): Result<ByteArray?> {
        return withContext(Dispatchers.IO) {
            try {
                ZipInputStream(zipFile.openInputStream()).use { zipInputStream ->
                    var entry = zipInputStream.nextEntry
                    while (entry != null) {
                        if (entry.name == entryPath) {
                            val output = ByteArrayOutputStream()
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            var totalBytes = 0
                            while (true) {
                                val bytesRead = zipInputStream.read(buffer)
                                if (bytesRead < 0) break
                                totalBytes += bytesRead
                                check(totalBytes <= maxSizeBytes) {
                                    "ZIP entry '$entryPath' exceeds the maximum size of $maxSizeBytes bytes"
                                }
                                output.write(buffer, 0, bytesRead)
                            }
                            return@withContext Result.success(output.toByteArray())
                        }
                        entry = zipInputStream.nextEntry
                    }
                }
                Result.success(null)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
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
                if (e is CancellationException) throw e
                L.e(e)
                Result.failure(e)
            }
        }
    }
}
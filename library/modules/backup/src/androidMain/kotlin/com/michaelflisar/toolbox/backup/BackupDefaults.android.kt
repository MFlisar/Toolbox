package com.michaelflisar.toolbox.backup

import com.michaelflisar.kmp.platformcontext.PlatformApplicationContext
import com.michaelflisar.toolbox.zip.JavaZipFileContent

actual fun BackupDefaults.createDefaultBackupContent(): List<ZipFileContent> {
    return listOf(
        JavaZipFileContent.Folder(
            folder = PlatformApplicationContext.filesDir,
            zipPath = "files",
            exclude = { relativePath ->
                // FILES
                relativePath == "log.txt" ||
                        relativePath == "rList" ||
                        relativePath == "profileInstalled" ||
                        // FOLDERS
                        relativePath.startsWith("shared-files/") ||
                        relativePath.startsWith("shared/") ||
                        relativePath.startsWith("lumberjack/")
            }
        ),
        JavaZipFileContent.Folder(
            folder = PlatformApplicationContext.getDatabasePath("unused").parentFile!!,
            zipPath = "database",
            exclude = { relativePath ->
                relativePath.contains("google") ||
                        relativePath.contains("leaks")
            }
        )
    )
}
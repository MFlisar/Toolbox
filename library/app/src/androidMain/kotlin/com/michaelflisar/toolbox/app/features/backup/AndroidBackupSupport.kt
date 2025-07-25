package com.michaelflisar.toolbox.app.features.backup

import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.zip.JavaZipFileContent

class AndroidBackupSupport(
    override val prefBackupPath: StorageSetting<String>,
    override val autoBackup: Boolean = false,
    override val extension: String = "zip",
    override val addToPrefs: Boolean = true,
    override val backupContent: List<JavaZipFileContent> = createDefaultBackupContent(),
) : IBackupSupport {

    companion object {
        fun createDefaultBackupContent(): List<JavaZipFileContent> {
            /*
            return listOf(
                JavaZipFileContent.Folder(AppContext.context().filesDir, "files"),
                JavaZipFileContent.Folder(
                    AppContext.context().getDatabasePath("unused").parentFile!!,
                    "database"
                )
            )*/
            return listOf(
                JavaZipFileContent.Folder(
                    folder = AppContext.context().filesDir,
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
                    folder = AppContext.context().getDatabasePath("unused").parentFile!!,
                    zipPath = "database",
                    exclude = { relativePath ->
                        relativePath.contains("google") ||
                                relativePath.contains("leaks")
                    }
                )
            )
        }
    }
}
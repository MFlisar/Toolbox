package com.michaelflisar.toolbox.app.features.backup

import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.toolbox.backup.JvmBackupManager
import com.michaelflisar.toolbox.zip.JavaZipFileContent

class JvmBackupSupport(
    override val prefBackupPath: StorageSetting<String>,
    override val extension: String = "zip",
    override val addToPrefs: Boolean = true,
    override val backupContent: List<JavaZipFileContent> = createDefaultBackupContent(),
) : IBackupSupport {

    // TODO: eventuell auch unterstützen
    override val autoBackup: Boolean = false

    companion object {

        // TODO
        fun createDefaultBackupContent(): List<JavaZipFileContent> {
            return emptyList()
            /*
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
            )*/
        }
    }
}
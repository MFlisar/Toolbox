package com.michaelflisar.toolbox.backup

import com.michaelflisar.toolbox.zip.JavaZipFileContent

object JvmBackupDefaults {

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
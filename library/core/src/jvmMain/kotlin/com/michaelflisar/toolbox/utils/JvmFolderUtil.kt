package com.michaelflisar.toolbox.utils

import java.io.File
import javax.swing.filechooser.FileSystemView

enum class AppData {
    /**
     * C:\Users\<USER>\AppData\Local
     */
    Local,
    /**
     * C:\Users\<USER>\AppData\Roaming
     */
    Roaming
}

object JvmFolderUtil {

    fun getUserDirPath() = System.getProperty("user.dir")

    fun getDesktopPath() = FileSystemView.getFileSystemView().homeDirectory.absolutePath

    fun getPathForAppData(meta: JvmAppMeta, namespace: String) = getAppDataPath(meta, AppData.Roaming, namespace)

    /**
     * C:\Users\<USER>
     */
    fun getUserHomePath() = System.getProperty("user.home")

    /**
     * AppData.Roaming... C:\Users\<USER>\AppData\Roaming
     * AppData.Local...   C:\Users\<USER>\AppData\Local
     *
     * in debug builds it creates a .data folder in the project root folder and places the data there
     */
    fun getAppDataPath(meta: JvmAppMeta, type: AppData, subFolder: String? = null): String {
        return if (meta.isDebug) {
            val root = getUserDirPath()
            val folder = type.name
            if (subFolder == null) "$root/.data/$folder" else "$root/.data/$folder/$subFolder".also {
                val file = File(it)
                file.mkdirs()
            }
        } else {
            val folder = when (type) {
                AppData.Local -> System.getenv("localappdata")
                AppData.Roaming -> System.getenv("appdata")
            }
             if (subFolder == null) folder else "$folder/$subFolder"
        }
    }
}
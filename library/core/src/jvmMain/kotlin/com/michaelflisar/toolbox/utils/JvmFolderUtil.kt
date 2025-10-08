package com.michaelflisar.toolbox.utils

import java.io.File

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

    fun getApplicationPath() = System.getProperty("user.dir")

    fun getDesktopPath() = "${System.getProperty("user.home").replace("\\", "/")}/Desktop"

    fun getPathForAppData(packageName: String) = getAppDataPath(AppData.Roaming, packageName)

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
    fun getAppDataPath(type: AppData, subFolder: String? = null): String {
        return if (JvmUtil.isDebug()) {
            val root = getApplicationPath()
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
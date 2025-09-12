package com.michaelflisar.toolbox.utils

import java.io.File

enum class AppData {
    Local,
    Roaming
}

object JvmFolderUtil {

    fun getApplicationPath() = System.getProperty("user.dir")

    fun getDesktopPath() = "${System.getProperty("user.home").replace("\\", "/")}/Desktop"

    /**
     * C:\Users\<USER>
     */
    fun getUserHomePath() = System.getProperty("user.home")

    /**
     * AppData.Roaming... C:\Users\<USER>\AppData\Roaming
     * AppData.Local...   C:\Users\<USER>\AppData\Local
     */
    fun getAppDataPath(type: AppData, subFolder: String? = null): String {
        val folder = when (type) {
            AppData.Local -> System.getenv("localappdata")
            AppData.Roaming -> System.getenv("appdata")
        }
        return if (subFolder == null) folder else "$folder/$subFolder"
    }
}
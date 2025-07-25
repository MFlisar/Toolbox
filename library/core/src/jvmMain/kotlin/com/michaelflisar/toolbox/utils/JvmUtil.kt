package com.michaelflisar.toolbox.utils

import com.michaelflisar.composechangelog.runPS
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.InetAddress
import kotlin.system.exitProcess

object JvmUtil {

    fun getEXEPath(): String {
        return File(JvmUtil::class.java.protectionDomain.codeSource.location.toURI()).absolutePath
    }

    //return Changelog.getAppVersionName().takeIf { it != "<UNKNOWN>" } == null
    fun getAppVersion(): String? {
        val exePath = getEXEPath()
        val version = runPS("(Get-Item '${exePath}').VersionInfo.FileVersion").takeIf { it.isNotEmpty() && it != "unspecified" }
        return version
    }

    fun getLocalAppDataFolder(appName: String) : File {
        val appDataLocal = System.getenv("LOCALAPPDATA")
        return File(appDataLocal, appName)
    }

    fun isDebug(): Boolean {
        return getAppVersion() == null
    }

    fun restartApp() {
        // Restart the application by executing the same command that started it
        val exePath = getEXEPath()
        runPS("Start-Process -FilePath '${exePath}'")
        // Exit the current process
        exitProcess(0)
    }

    fun killApp() {
        exitProcess(0)
    }

    fun setClipboard(s: String) {
        val selection = StringSelection(s)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, selection)
    }

    fun appDir() = File(System.getProperty("user.dir"))

    fun javaVersion(versionOnly: Boolean = false) = System.getProperty("java.version").let {
        if (versionOnly) {
            it
        } else "Java $it"
    }

    fun userName() = System.getenv("username")
    fun hostName() = InetAddress.getLocalHost().hostName
}
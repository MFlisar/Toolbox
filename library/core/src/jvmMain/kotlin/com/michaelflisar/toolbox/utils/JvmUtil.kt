package com.michaelflisar.toolbox.utils

import com.michaelflisar.composechangelog.runPS
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.InetAddress
import kotlin.system.exitProcess

object JvmUtil {

    fun getEXEPath(): String {
        return File(JvmUtil::class.java.protectionDomain.codeSource.location.toURI()).absolutePath
    }

    fun getUserDataRoot(appFolderName: String, createIfMissing: Boolean = true): File {
        val folder = if (isDebug()) {
            File(JvmFolderUtil.getApplicationPath(), "app-data")
        } else {
            File(JvmFolderUtil.getAppDataPath(AppData.Local, appFolderName)).also { it.mkdirs() }
        }
        if (createIfMissing && !folder.exists())
            folder.mkdirs()
        return folder
    }

    //return Changelog.getAppVersionName().takeIf { it != "<UNKNOWN>" } == null
    fun getAppVersion(): String? {
        val exePath = getEXEPath()
        val version =
            runPS("(Get-Item '${exePath}').VersionInfo.FileVersion").takeIf { it.isNotEmpty() && it != "unspecified" }
        return version
    }

    fun isDebug(): Boolean {
        // gibt ohne fat jar immer einen jar Pfad zurück
        val exe = getEXEPath()
        // workaround: wenn exe im .gradle Ordner ist, dann sind wir im debug modus
        val gradlePath = File(JvmFolderUtil.getUserHomePath(), ".gradle")
        //println("exe: $exe")
        //println("gradle: $gradlePath")
        if (exe.startsWith(gradlePath.absolutePath)) {
            return true
        }
        return false
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

    fun javaVersion(versionOnly: Boolean = false) = System.getProperty("java.version").let {
        if (versionOnly) {
            it
        } else "Java $it"
    }

    fun userName() = System.getenv("username")
    fun hostName() = InetAddress.getLocalHost().hostName

    fun open(file: File) {
        Desktop.getDesktop().open(file)
    }
}
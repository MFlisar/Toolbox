package com.michaelflisar.toolbox.utils

import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.InetAddress
import kotlin.system.exitProcess

object JvmUtil {

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
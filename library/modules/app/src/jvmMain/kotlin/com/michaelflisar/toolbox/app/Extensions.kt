package com.michaelflisar.toolbox.app

import com.michaelflisar.toolbox.Toolbox
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.InetAddress

fun Toolbox.setClipboard(s: String) {
    val selection = StringSelection(s)
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(selection, selection)
}

fun Toolbox.appDir() = File(System.getProperty("user.dir"))

fun Toolbox.javaVersion(versionOnly: Boolean = false) = System.getProperty("java.version").let {
    if (versionOnly) {
        it
    } else "Java $it"
}

fun Toolbox.userName() = System.getenv("username")
fun Toolbox.hostName() = InetAddress.getLocalHost().hostName
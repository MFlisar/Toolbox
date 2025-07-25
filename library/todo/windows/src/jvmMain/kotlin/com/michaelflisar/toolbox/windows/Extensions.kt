package com.michaelflisar.toolbox.windows

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
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
fun Toolbox.initLumberjack(file: File = File(System.getProperty("user.dir"), "log.txt")) = initLumberjackImpl(file)!!
fun Toolbox.initLumberjackNoLogFile() = initLumberjackImpl(null)

private fun Toolbox.initLumberjackImpl(file: File?): IFileLoggingSetup? {
    L.init(LumberjackLogger)
    L.plant(ConsoleLogger())
    return if (file != null) {
        val setup = FileLoggerSetup.SingleFile.create(
            file = file
        )
        L.plant(FileLogger(setup))
        setup
    } else null
}

fun Toolbox.javaVersion(versionOnly: Boolean = false) = System.getProperty("java.version").let {
    if (versionOnly) {
        it
    } else "Java $it"
}
fun Toolbox.userName() = System.getenv("username")
fun Toolbox.hostName() = InetAddress.getLocalHost().hostName
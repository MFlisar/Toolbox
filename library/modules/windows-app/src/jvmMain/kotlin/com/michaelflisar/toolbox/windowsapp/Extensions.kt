package com.michaelflisar.toolbox.windowsapp

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

fun Toolbox.setClipboard(s: String) {
    val selection = StringSelection(s)
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(selection, selection)
}

fun Toolbox.initDesktopApp(): IFileLoggingSetup {
   return initDesktopApp(File(System.getProperty("user.dir"), "log.txt"))!!
}

fun Toolbox.initDesktopApp(file: File?): IFileLoggingSetup? {
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
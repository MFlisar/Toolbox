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

fun Toolbox.initLoggingConsoleOnly() {
    L.init(LumberjackLogger)
    L.plant(ConsoleLogger())
}

fun Toolbox.initLogging(): IFileLoggingSetup {
    L.init(LumberjackLogger)
    L.plant(ConsoleLogger())
    val setup = FileLoggerSetup.FileSize.create(
        folder = File(System.getProperty("user.dir")),
        maxFileSizeInBytes = Int.MAX_VALUE
    )
    L.plant(FileLogger(setup))
    return setup
}
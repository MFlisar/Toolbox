package com.michaelflisar.toolbox.app

import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.utils.JvmUtil
import java.io.File

fun JvmUtil.createFileLogger(folder: File = File(System.getProperty("user.dir")), fileName: String = "log.txt"): FileLogger<*> {
    return FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.SingleFile.create(
            file = File(folder, fileName)
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )
}

fun JvmUtil.createFileLogger(file: File = File(System.getProperty("user.dir"), "log.txt")): FileLogger<*> {
    return FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.SingleFile.create(
            file = file
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )
}
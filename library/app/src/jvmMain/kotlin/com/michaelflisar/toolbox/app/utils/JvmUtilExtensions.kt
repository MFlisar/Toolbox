package com.michaelflisar.toolbox.app.utils

import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.utils.JvmUtil
import java.io.File

fun JvmUtil.createFileLogger(folder: String, fileName: String = "log.txt"): FileLogger<*> {
    return FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.SingleFile.create(
            file = File(folder, fileName)
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )
}

fun JvmUtil.createFileLogger(file: String): FileLogger<*> {
    return FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.SingleFile.create(
            file = File(file)
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )
}
package com.michaelflisar.toolbox.app.utils

import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.utils.AndroidUtil

fun AndroidUtil.createFileLogger(
    fileBaseName: String = "log",
    fileExtension: String = "txt",
    filesToKeep: Int = 1,
): FileLogger<*> {
    return FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.Daily.create(
            context = AppContext.context(),
            fileBaseName = fileBaseName,
            fileExtension = fileExtension,
            filesToKeep = filesToKeep
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )
}
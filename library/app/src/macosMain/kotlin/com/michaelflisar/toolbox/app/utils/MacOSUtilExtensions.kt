package com.michaelflisar.toolbox.app.utils

import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.utils.IOSUtil

fun MacOSUtil.createFileLogger(
    fileBaseName: String = "log",
    fileExtension: String = "log",
    filesToKeep: Int = 1,
): FileLogger<*> {
    return FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.Daily.create(
            fileBaseName = fileBaseName,
            fileExtension = fileExtension,
            filesToKeep = filesToKeep
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )
}
package com.michaelflisar.toolbox.app.features.logging

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup

actual fun <Setup : IFileLoggingSetup> LogManager.createFileLogger(
    setup: Setup,
): ILumberjackLogger {
    return com.michaelflisar.lumberjack.loggers.file.FileLogger(setup as FileLoggerSetup)
}
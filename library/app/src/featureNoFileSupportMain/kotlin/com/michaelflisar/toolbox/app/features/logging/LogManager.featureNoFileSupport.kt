package com.michaelflisar.toolbox.app.features.logging

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger

actual fun <Setup : IFileLoggingSetup> LogManager.createFileLogger(
    setup: Setup,
): ILumberjackLogger {
    throw NotImplementedError("File logging is not supported on this platform")
}
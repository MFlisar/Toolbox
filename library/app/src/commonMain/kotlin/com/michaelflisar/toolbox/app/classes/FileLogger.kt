package com.michaelflisar.toolbox.app.classes

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger

class FileLogger<Setup: IFileLoggingSetup>(
    val setup: Setup,
    val logger: (setup: Setup) -> ILumberjackLogger
) {
    fun createLogger(): ILumberjackLogger {
        return logger(setup)
    }
}
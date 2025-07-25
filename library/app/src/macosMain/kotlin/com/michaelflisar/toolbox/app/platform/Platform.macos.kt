package com.michaelflisar.toolbox.app.platform

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.MacOSAppPrefs

actual typealias AppPrefs = MacOSAppPrefs
actual val Platform.fileLogger: FileLogger<*>?
    get() = FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.Daily.create(
            fileExtension = "txt"
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )
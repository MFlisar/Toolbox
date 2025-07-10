package com.michaelflisar.toolbox.app.platform

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.MacOSAppPrefs

actual typealias AppPrefs = MacOSAppPrefs

actual val Platform.fileLoggerSetup: IFileLoggingSetup?
    get() = FileLoggerSetup.Daily.create()
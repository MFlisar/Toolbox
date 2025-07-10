package com.michaelflisar.toolbox.app.platform

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.DesktopAppPrefs
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import java.io.File

actual typealias AppPrefs = DesktopAppPrefs

actual val Platform.fileLoggerSetup: IFileLoggingSetup?
    get() = FileLoggerSetup.SingleFile.create(
        file = File(System.getProperty("user.dir"), "log.txt")
    )


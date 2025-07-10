package com.michaelflisar.toolbox.app.platform

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.WasmAppPrefs

actual typealias AppPrefs = WasmAppPrefs

actual val Platform.fileLoggerSetup: IFileLoggingSetup?
    get() = null
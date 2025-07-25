package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.IOSAppPrefs
import com.michaelflisar.toolbox.app.classes.FileLogger
import com.michaelflisar.toolbox.app.classes.PlatformContext

actual typealias AppPrefs = IOSAppPrefs

actual val Platform.fileLogger: FileLogger<*>?
    get() = FileLogger<FileLoggerSetup>(
        setup = FileLoggerSetup.Daily.create(
            fileExtension = "txt"
        ),
        logger = { com.michaelflisar.lumberjack.loggers.file.FileLogger(it) }
    )
actual val Platform.restart: ((context: PlatformContext) -> Unit)?
    get() = null

actual val Platform.kill: ((context: PlatformContext) -> Unit)?
    get() = null

actual val Platform.showToast: ((message: String, duration: Int) -> Unit)?
    get() = null

@Composable
actual fun Platform.UpdateComposeThemeStatusBar(
    activity: Any?,
    composeThemeState: ComposeTheme.State,
) {
    // --
}
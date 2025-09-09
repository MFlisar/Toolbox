package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.createFileLogger
import com.michaelflisar.toolbox.utils.JvmUtil
import java.io.File

actual val Platform.fileLogger: FileLogger<*>?
    get() = JvmUtil.createFileLogger()

actual val Platform.restart: ((context: PlatformContext) -> Unit)?
    get() = { JvmUtil.restartApp() }

actual val Platform.kill: ((context: PlatformContext) -> Unit)?
    get() = { JvmUtil.killApp() }

actual val Platform.showToast: ((message: String, duration: Int) -> Unit)?
    get() = null

@Composable
actual fun Platform.UpdateComposeThemeStatusBar(
    activity: Any?,
    composeThemeState: ComposeTheme.State,
) {
    // --
}

@Composable
actual fun Platform.isDarkTheme(): Boolean {
    val desktopSetup = DesktopApp.setup
    val theme = desktopSetup.prefs.jewelTheme.collectAsStateNotNull()
    return theme.value.isDark()
}
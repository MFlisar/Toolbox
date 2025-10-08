package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.features.logging.FileLogger
import com.michaelflisar.toolbox.app.classes.PlatformContext
import com.michaelflisar.toolbox.app.utils.createFileLogger
import com.michaelflisar.toolbox.utils.JvmUtil

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
    val desktopSetup = DesktopAppSetup.get()
    val theme = desktopSetup.prefs.jewelTheme.collectAsStateNotNull()
    return theme.value.isDark()
}
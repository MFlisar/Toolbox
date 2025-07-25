package com.michaelflisar.toolbox.app.platform

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.WasmAppPrefs
import com.michaelflisar.toolbox.app.classes.FileLogger
import com.michaelflisar.toolbox.app.classes.PlatformContext

actual typealias AppPrefs = WasmAppPrefs

actual val Platform.fileLogger: FileLogger<*>?
    get() = null

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
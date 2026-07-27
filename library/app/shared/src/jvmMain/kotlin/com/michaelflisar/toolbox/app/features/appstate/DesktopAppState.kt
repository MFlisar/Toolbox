package com.michaelflisar.toolbox.app.features.appstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.WindowState
import com.michaelflisar.toolbox.app.classes.rememberDesktopWindowState
import com.michaelflisar.toolbox.app.classes.resetAll
import com.michaelflisar.toolbox.app.features.preferences.BaseDesktopPrefs
import com.michaelflisar.toolbox.app.utils.WindowUtil

val LocalDesktopAppState =
    compositionLocalOf<DesktopAppState> { throw RuntimeException("DesktopAppState not initialised!") }

@Composable
fun rememberDesktopAppState(
    prefs: BaseDesktopPrefs,
): DesktopAppState {
    return DesktopAppState(
        rememberDesktopWindowState(prefs),
    )
}

@Composable
fun rememberDesktopAppState(
    windowState: WindowState,
): DesktopAppState {
    return DesktopAppState(windowState)
}

class DesktopAppState internal constructor(
    val windowState: WindowState,
) {
    suspend fun ensureIsAtLeastPartlyOnScreen(density: Density, window: ComposeWindow) {
        if (!WindowUtil.isWindowOnScreen(window, true)) {
            windowState.resetAll(density, window)
        }
    }

    suspend fun ensureIsFullyOnScreen(density: Density, window: ComposeWindow) {
        if (!WindowUtil.isWindowOnScreen(window, false)) {
            windowState.resetAll(density, window)
        }
    }
}
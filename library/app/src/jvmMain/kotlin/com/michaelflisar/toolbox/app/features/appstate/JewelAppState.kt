package com.michaelflisar.toolbox.app.features.appstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.WindowState
import com.michaelflisar.toolbox.app.classes.rememberJewelWindowState
import com.michaelflisar.toolbox.app.classes.resetAll
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs
import com.michaelflisar.toolbox.app.utils.WindowUtil

internal val LocalJewelAppState =
    compositionLocalOf<JewelAppState> { throw RuntimeException("JewelAppState not initialised!") }

@Composable
internal fun rememberJewelAppState(
    prefs: DesktopPrefs,
): JewelAppState {
    return JewelAppState(
        rememberJewelWindowState(prefs)
    )
}

@Composable
internal fun rememberJewelAppState(
    windowState: WindowState,
): JewelAppState {
    return JewelAppState(windowState)
}

internal class JewelAppState internal constructor(
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
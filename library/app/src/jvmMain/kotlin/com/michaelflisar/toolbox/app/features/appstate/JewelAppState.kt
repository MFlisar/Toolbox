package com.michaelflisar.toolbox.app.features.appstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.WindowState
import com.michaelflisar.toolbox.app.classes.rememberJewelWindowState
import com.michaelflisar.toolbox.app.platform.AppPrefs

val LocalJewelAppState =
    compositionLocalOf<JewelAppState> { throw RuntimeException("JewelAppState not initialised!") }

@Composable
fun rememberJewelAppState(
    prefs: AppPrefs
): JewelAppState {
    return JewelAppState(
        rememberJewelWindowState(prefs)
    )
}

@Composable
fun rememberJewelAppState(
    windowState: WindowState
): JewelAppState {
    return JewelAppState(windowState)
}

class JewelAppState internal constructor(
    val windowState: WindowState
)
package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs

class DesktopAppSetup(
    val prefs: DesktopPrefs,
    val titleBarIcon: @Composable (light: Boolean) -> Painter?,
    val appIcon: @Composable () -> Painter?,
    val visible: Boolean = true,
    val resizable: Boolean = true,
    val enabled: Boolean = true,
    val focusable: Boolean = true,
    val swingCompatMode: Boolean = false,
    val ensureIsFullyOnScreen: Boolean = false
) {
    companion object {
        fun get() = App.requireSingleton<DesktopAppSetup>()
    }
}
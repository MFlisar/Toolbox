package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.michaelflisar.toolbox.app.features.appstate.DesktopAppState
import com.michaelflisar.toolbox.app.features.appstate.LocalDesktopAppState

@Composable
fun DesktopLocalProvider(
    desktopAppState: DesktopAppState,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDesktopAppState provides desktopAppState
    ) {
        content()
    }
}
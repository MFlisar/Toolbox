package com.michaelflisar.toolbox.app.jewel

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.features.appstate.JewelAppState
import kotlinx.coroutines.launch
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.defaultDecoratedWindowStyle

@Composable
fun JewelRoot(
    jewelAppState: JewelAppState,
    appIsClosing: MutableState<Boolean>,
    content: @Composable DecoratedWindowScope.() -> Unit,
) {
    val setup = CommonApp.setup
    val desktopSetup = DesktopApp.setup

    val scope = rememberCoroutineScope()
    val alwaysOnTop by desktopSetup.prefs.alwaysOnTop.collectAsStateNotNull()
    JewelLocalProvider(
        jewelAppState = jewelAppState
    ) {
        DecoratedWindow(
            onCloseRequest = {
                scope.launch {
                    desktopSetup.onClosed?.invoke()
                    appIsClosing.value = true
                }
            },
            state = jewelAppState.windowState,
            visible = desktopSetup.visible,
            title = setup.name(),
            icon = setup.icon(),
            resizable = desktopSetup.resizable,
            enabled = desktopSetup.enabled,
            focusable = desktopSetup.focusable,
            alwaysOnTop = alwaysOnTop,
            onPreviewKeyEvent = desktopSetup.onPreviewKeyEvent,
            onKeyEvent = desktopSetup.onKeyEvent,
            style = org.jetbrains.jewel.foundation.theme.JewelTheme.defaultDecoratedWindowStyle,
            content = {
                JewelLocalWindowProvider(
                    window = this.window
                ) {
                    content()
                }

            }
        )
    }
}
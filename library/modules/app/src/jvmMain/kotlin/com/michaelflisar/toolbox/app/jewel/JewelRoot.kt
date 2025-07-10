package com.michaelflisar.toolbox.app.jewel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.appstate.AppState
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState
import kotlinx.coroutines.launch
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.defaultDecoratedWindowStyle

@Composable
fun JewelRoot(
    appState: AppState,
    close: MutableState<Boolean>,
    titlebar: @Composable DecoratedWindowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val setup = CommonApp.setup
    val desktopSetup = DesktopApp.setup
    val prefs = setup.prefs
    val jewelAppState = LocalJewelAppState.current

    val scope = rememberCoroutineScope()
    val alwaysOnTop by prefs.alwaysOnTop.collectAsStateNotNull()

    RootLocalProvider(appState) {
        DecoratedWindow(
            onCloseRequest = {
                scope.launch {
                    desktopSetup.onClosed?.invoke()
                    close.value = true
                }
            },
            state = jewelAppState.windowState,
            visible = desktopSetup.visible,
            title = appState.toolbar.title.value,
            icon = setup.icon(),
            resizable = desktopSetup.resizable,
            enabled = desktopSetup.enabled,
            focusable = desktopSetup.focusable,
            alwaysOnTop = alwaysOnTop,
            onPreviewKeyEvent = desktopSetup.onPreviewKeyEvent,
            onKeyEvent = desktopSetup.onKeyEvent,
            style = org.jetbrains.jewel.foundation.theme.JewelTheme.defaultDecoratedWindowStyle,
            content = {
                titlebar()
                content()
            }
        )
    }
}
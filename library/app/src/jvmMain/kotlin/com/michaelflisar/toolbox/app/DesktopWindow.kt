package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.LocalWindowExceptionHandlerFactory
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.michaelflisar.toolbox.app.utils.WindowUtil
import com.michaelflisar.toolbox.app.utils.createWindowExceptionHandlerFactory
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.rememberDesktopWindowState
import com.michaelflisar.toolbox.app.classes.resetAll
import com.michaelflisar.toolbox.utils.JvmUtil

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DesktopWindow(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberDesktopWindowState(DesktopAppSetup.get().prefs),
    visible: Boolean = true,
    title: String = AppSetup.get().appData.name,
    icon: Painter? = AppSetup.get().icon.invoke(),
    undecorated: Boolean = false,
    transparent: Boolean = false,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    appIsClosing: MutableState<Boolean> = remember { mutableStateOf(false) },
    content: @Composable FrameWindowScope.() -> Unit,
) {
    CompositionLocalProvider(
        LocalWindowExceptionHandlerFactory provides JvmUtil.createWindowExceptionHandlerFactory()
    ) {
        Window(
            onCloseRequest = {
                if (!appIsClosing.value) {
                    appIsClosing.value = true
                    onCloseRequest()
                }
            },
            state = state,
            visible = visible,
            title = title,
            icon = icon,
            undecorated = undecorated,
            transparent = transparent,
            resizable = resizable,
            enabled = enabled,
            focusable = focusable,
            alwaysOnTop = alwaysOnTop,
            onPreviewKeyEvent = onPreviewKeyEvent,
            onKeyEvent = onKeyEvent
        ) {
            if (DesktopAppSetup.get().ensureIsFullyOnScreen) {
                val density = LocalDensity.current
                LaunchedEffect(state, density, window) {
                    ensureIsFullyOnScreen(state, density, window)
                }
            }

            content()
        }
    }
}

private fun ensureIsAtLeastPartlyOnScreen(
    state: WindowState,
    density: Density,
    window: ComposeWindow,
) {
    if (!WindowUtil.isWindowOnScreen(window, true)) {
        state.resetAll(density, window)
    }
}

private fun ensureIsFullyOnScreen(state: WindowState, density: Density, window: ComposeWindow) {
    if (!WindowUtil.isWindowOnScreen(window, false)) {
        state.resetAll(density, window)
    }
}
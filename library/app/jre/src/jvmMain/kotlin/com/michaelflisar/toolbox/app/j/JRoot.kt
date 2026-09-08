package com.michaelflisar.toolbox.app.j

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.LocalComposeWindow
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.DesktopLocalProvider
import com.michaelflisar.toolbox.app.features.appstate.DesktopAppState
import com.michaelflisar.toolbox.app.features.filekit.LocalFileKitDialogSettingsState
import io.github.vinceglb.filekit.dialogs.FileKitDialogParent
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

internal val LocalFrameWindowScope =
    compositionLocalOf<FrameWindowScope> { throw RuntimeException("FrameWindowScope not initialised!") }

@Composable
internal fun JRoot(
    desktopAppState: DesktopAppState,
    appIsClosing: MutableState<Boolean>,
    onCloseRequest: (() -> Unit)?,
    onPreviewKeyEvent: (KeyEvent) -> Boolean,
    onKeyEvent: (KeyEvent) -> Boolean,
    content: @Composable () -> Unit,
) {
    val setup = AppSetup.get()
    val desktopSetup = DesktopAppSetup.get()

    val alwaysOnTop by desktopSetup.prefs.alwaysOnTop.collectAsStateNotNull()

    DesktopLocalProvider(
        desktopAppState = desktopAppState
    ) {
        Window(
            onCloseRequest = {
                if (!appIsClosing.value) {
                    appIsClosing.value = true
                    onCloseRequest?.invoke()
                }
            },
            state = desktopAppState.windowState,
            visible = desktopSetup.visible,
            title = setup.appData.name,
            icon = desktopSetup.appIcon(), // icon in windows toolbar
            resizable = desktopSetup.resizable,
            enabled = desktopSetup.enabled,
            focusable = desktopSetup.focusable,
            alwaysOnTop = alwaysOnTop,
            onPreviewKeyEvent = onPreviewKeyEvent,
            onKeyEvent = onKeyEvent,
            content = {
                CompositionLocalProvider(
                    LocalFrameWindowScope provides this,
                    LocalComposeWindow provides this.window,
                    LocalFileKitDialogSettingsState provides FileKitDialogSettings(parent = FileKitDialogParent.awt(window)),
                ) {
                    content()
                }
            }
        )
    }
}
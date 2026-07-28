package com.michaelflisar.toolbox.app.jewel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.key.KeyEvent
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.LocalComposeWindow
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.DesktopLocalProvider
import com.michaelflisar.toolbox.app.features.appstate.DesktopAppState
import com.michaelflisar.toolbox.app.features.filekit.LocalFileKitDialogSettingsState
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.defaultDecoratedWindowStyle

internal val LocalDecoratedWindowScope =
    compositionLocalOf<DecoratedWindowScope> { throw RuntimeException("DecoratedWindowScope not initialised!") }


@Composable
internal fun JewelRoot(
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
        DecoratedWindow(
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
            style = JewelTheme.defaultDecoratedWindowStyle,
            content = {
                CompositionLocalProvider(
                    LocalDecoratedWindowScope provides this,
                    LocalComposeWindow provides this.window,
                    LocalFileKitDialogSettingsState provides FileKitDialogSettings(parentWindow = window),
                ) {
                    content()
                }
            }
        )
    }
}
package com.michaelflisar.publicutilities.windowsapp

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.publicutilities.windowsapp.classes.AppState
import com.michaelflisar.publicutilities.windowsapp.classes.LocalAppState
import com.michaelflisar.publicutilities.windowsapp.classes.rememberAppState
import com.michaelflisar.publicutilities.windowsapp.internal.rememberWindowState
import kotlinx.coroutines.launch
import org.pushingpixels.aurora.component.model.CommandGroup
import org.pushingpixels.aurora.theming.IconFilterStrategy
import org.pushingpixels.aurora.theming.businessBlackSteelSkin
import org.pushingpixels.aurora.window.*
import java.io.File

@Composable
fun AuroraApplicationScope.DesktopApplication(
    title: String,
    state: AppState = rememberAppState(),
    visible: Boolean = true,
    icon: Painter? = null,
    iconFilterStrategy: IconFilterStrategy = IconFilterStrategy.Original,
    windowTitlePaneConfiguration: AuroraWindowTitlePaneConfiguration = AuroraWindowTitlePaneConfigurations.AuroraPlain(),
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: (androidx.compose.ui.input.key.KeyEvent) -> Boolean = { false },
    onKeyEvent: (androidx.compose.ui.input.key.KeyEvent) -> Boolean = { false },
    menuCommands: @Composable (() -> CommandGroup)? = null,
    onClosed: (suspend () -> Unit)? = null,
    colors: ColorScheme = DesktopApp.Constants.COLORS,
    saveSettingsOnAppClose: Boolean = true,
    content: @Composable AuroraWindowScope.() -> Unit
) {
    MaterialTheme(
        colorScheme = colors
    ) {
        CompositionLocalProvider(LocalAppState provides state) {
            val appState = LocalAppState.current
            val scope = rememberCoroutineScope()
            val windowState = rememberWindowState(scope, appState)
            AuroraWindow(
                skin = businessBlackSteelSkin(),
                windowTitlePaneConfiguration = windowTitlePaneConfiguration,
                state = windowState,
                onCloseRequest = {
                    scope.launch {
                        onClosed?.invoke()
                        appState.close.value = true
                    }
                },
                title = title,
                icon = icon,
                resizable = resizable,
                alwaysOnTop = alwaysOnTop,
                menuCommands = menuCommands?.invoke(),
                visible = visible,
                iconFilterStrategy = iconFilterStrategy,
                enabled = enabled,
                focusable = focusable,
                onPreviewKeyEvent = onPreviewKeyEvent,
                onKeyEvent = onKeyEvent
            ) {
                content()
            }

            // Close Action
            if (appState.close.value) {
                if (saveSettingsOnAppClose) {
                    appState.settings.value.write()
                }
                exitApplication()
            }
        }
    }
}

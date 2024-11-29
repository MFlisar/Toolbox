package com.michaelflisar.toolbox.windowsapp

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.WindowState
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.classes.Theme
import com.michaelflisar.toolbox.windowsapp.classes.AppState
import com.michaelflisar.toolbox.windowsapp.classes.LocalAppState
import com.michaelflisar.toolbox.windowsapp.classes.rememberAppState
import com.michaelflisar.toolbox.windowsapp.internal.rememberWindowState
import com.michaelflisar.toolbox.windowsapp.locals.LocalAuroraApplicationScope
import com.michaelflisar.toolbox.windowsapp.locals.LocalAuroraWindowState
import com.michaelflisar.toolbox.windowsapp.prefs.DesktopAppPrefs
import kotlinx.coroutines.launch
import org.pushingpixels.aurora.component.model.CommandGroup
import org.pushingpixels.aurora.theming.IconFilterStrategy
import org.pushingpixels.aurora.theming.businessBlackSteelSkin
import org.pushingpixels.aurora.window.AuroraApplicationScope
import org.pushingpixels.aurora.window.AuroraWindow
import org.pushingpixels.aurora.window.AuroraWindowScope
import org.pushingpixels.aurora.window.AuroraWindowTitlePaneConfiguration
import org.pushingpixels.aurora.window.AuroraWindowTitlePaneConfigurations

@Composable
fun AuroraApplicationScope.DesktopApplication(
    title: String,
    prefs: DesktopAppPrefs,
    windowState: WindowState = rememberWindowState(prefs),
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
    colors: @Composable (theme: Theme) -> ColorScheme = { ToolboxDefaults.colorScheme(it) },
    shapes: Shapes = ToolboxDefaults.SHAPES,
    typography: Typography = MaterialTheme.typography,
    content: @Composable AuroraWindowScope.() -> Unit
) {
    val theme by prefs.theme.collectAsStateNotNull()
    MaterialTheme(
        colorScheme = colors(theme),
        shapes = shapes,
        typography = typography
    ) {
        CompositionLocalProvider(
            LocalAppState provides state
        ) {
            val appState = LocalAppState.current
            val scope = rememberCoroutineScope()
            val enabled = remember { mutableStateOf(enabled) }
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
                enabled = enabled.value,
                focusable = focusable,
                onPreviewKeyEvent = onPreviewKeyEvent,
                onKeyEvent = onKeyEvent
            ) {
                CompositionLocalProvider(
                    LocalAuroraWindowState provides enabled,
                    LocalAuroraApplicationScope provides this@DesktopApplication
                ) {
                    content()
                }
            }

            // Close Action
            if (appState.close.value) {
                exitApplication()
            }
        }
    }
}

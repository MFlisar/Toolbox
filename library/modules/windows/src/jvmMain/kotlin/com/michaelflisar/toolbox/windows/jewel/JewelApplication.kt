package com.michaelflisar.toolbox.windows.jewel

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.classes.Style
import com.michaelflisar.toolbox.windows.classes.AppState
import com.michaelflisar.toolbox.windows.classes.LocalAppState
import com.michaelflisar.toolbox.windows.classes.rememberAppState
import com.michaelflisar.toolbox.windows.prefs.DesktopAppPrefs
import com.michaelflisar.toolbox.windows.rememberJewelWindowState
import kotlinx.coroutines.launch
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.intui.standalone.theme.createEditorTextStyle
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.intui.window.styling.lightWithLightHeader
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.defaultDecoratedWindowStyle
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarStyle

fun jewelApplication(
    prefs: DesktopAppPrefs,
    windowState: @Composable () -> WindowState = { rememberJewelWindowState(prefs) },
    state: @Composable () -> AppState = { rememberAppState(prefs) },
    visible: Boolean = true,
    title: @Composable () -> String = { "" },
    icon: @Composable (() -> Painter)? = null,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    windowStyle: @Composable () -> DecoratedWindowStyle = { org.jetbrains.jewel.foundation.theme.JewelTheme.defaultDecoratedWindowStyle },
    onClosed: (suspend () -> Unit)? = null,
    colors: @Composable (theme: JewelTheme) -> ColorScheme = { ToolboxDefaults.colorScheme(it.theme) },
    shapes: Shapes = ToolboxDefaults.SHAPES,
    typography: @Composable () -> Typography = { Style.windowsTypography() },
    style: @Composable () -> Style = { Style.windowsDefault() },
    content: @Composable DecoratedWindowScope.(windowState: WindowState) -> Unit
) {
    application {

        val textStyle = org.jetbrains.jewel.foundation.theme.JewelTheme.createDefaultTextStyle()
        val editorStyle = org.jetbrains.jewel.foundation.theme.JewelTheme.createEditorTextStyle()

        val title = title()
        val state = state()
        val style = style()
        val windowState = windowState()

        val theme by prefs.jewelTheme.collectAsStateNotNull()

        val themeDefinition = if (theme.isDark()) {
            org.jetbrains.jewel.foundation.theme.JewelTheme.darkThemeDefinition(
                defaultTextStyle = textStyle,
                editorTextStyle = editorStyle
            )
        } else {
            org.jetbrains.jewel.foundation.theme.JewelTheme.lightThemeDefinition(
                defaultTextStyle = textStyle,
                editorTextStyle = editorStyle
            )
        }

        IntUiTheme(
            theme = themeDefinition,
            styling = ComponentStyling
                .default()
                .decoratedWindow(
                    titleBarStyle = when (theme) {
                        JewelTheme.Light -> TitleBarStyle.light()
                        JewelTheme.LightWithLightHeader -> TitleBarStyle.lightWithLightHeader()
                        JewelTheme.Dark -> TitleBarStyle.dark()
                        JewelTheme.System ->
                            if (theme.isDark()) {
                                TitleBarStyle.dark()
                            } else {
                                TitleBarStyle.light()
                            }
                    }
                ),
            swingCompatMode = false
        ) {
            MaterialTheme(
                colorScheme = colors(theme),
                shapes = shapes,
                typography = typography()
            ) {
                CompositionLocalProvider(
                    LocalAppState provides state,
                    LocalMinimumInteractiveComponentSize provides style.minimumInteractiveComponentSize,
                    LocalStyle provides style
                ) {
                    val scope = rememberCoroutineScope()
                    val alwaysOnTop by prefs.alwaysOnTop.collectAsStateNotNull()
                    DecoratedWindow(
                        onCloseRequest = {
                            scope.launch {
                                onClosed?.invoke()
                                state.close()
                            }
                        },
                        state = windowState,
                        visible = visible,
                        title = title,
                        icon = icon?.invoke(),
                        resizable = resizable,
                        enabled = enabled,
                        focusable = focusable,
                        alwaysOnTop = alwaysOnTop,
                        onPreviewKeyEvent = onPreviewKeyEvent,
                        onKeyEvent = onKeyEvent,
                        style = windowStyle(),
                        content = {
                            content(windowState)
                        }
                    )
                }

                // Close Action
                if (state.close.value) {
                    exitApplication()
                }
            }
        }
    }
}
package com.michaelflisar.toolbox.app.jewel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ApplicationScope
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.features.appstate.AppState
import com.michaelflisar.toolbox.app.features.appstate.JewelAppState
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState
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
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.styling.TitleBarStyle

@Composable
fun JewelApp(
    content: @Composable () -> Unit,
) {
    val setup = CommonApp.setup
    val desktopSetup = DesktopApp.setup
    val prefs = setup.prefs

    val textStyle = org.jetbrains.jewel.foundation.theme.JewelTheme.createDefaultTextStyle()
    val editorStyle = org.jetbrains.jewel.foundation.theme.JewelTheme.createEditorTextStyle()

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
        styling = ComponentStyling.Companion
            .default()
            .decoratedWindow(
                titleBarStyle = when (theme) {
                    JewelTheme.Light -> TitleBarStyle.Companion.light()
                    JewelTheme.LightWithLightHeader -> TitleBarStyle.Companion.lightWithLightHeader()
                    JewelTheme.Dark -> TitleBarStyle.Companion.dark()
                    JewelTheme.System ->
                        if (theme.isDark()) {
                            TitleBarStyle.Companion.dark()
                        } else {
                            TitleBarStyle.Companion.light()
                        }
                }
            ),
        swingCompatMode = desktopSetup.swingCompatMode
    ) {
        content()
    }
}
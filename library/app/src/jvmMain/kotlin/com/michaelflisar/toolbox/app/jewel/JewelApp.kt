package com.michaelflisar.toolbox.app.jewel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
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
import org.jetbrains.jewel.window.styling.TitleBarStyle

@Composable
internal fun JewelApp(
    content: @Composable () -> Unit,
) {
    val desktopSetup = DesktopAppSetup.get()

    val textStyle = org.jetbrains.jewel.foundation.theme.JewelTheme.createDefaultTextStyle()
    val editorStyle = org.jetbrains.jewel.foundation.theme.JewelTheme.createEditorTextStyle()

    val theme by desktopSetup.prefs.jewelTheme.collectAsStateNotNull()

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
        swingCompatMode = desktopSetup.swingCompatMode
    ) {
        content()
    }
}
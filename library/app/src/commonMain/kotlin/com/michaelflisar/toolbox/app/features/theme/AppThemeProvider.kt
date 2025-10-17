package com.michaelflisar.toolbox.app.features.theme

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.MyTheme
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.rememberComposeTheme
import com.michaelflisar.toolbox.app.platform.UpdateComposeThemeStatusBar

@Composable
fun AppThemeProvider(
    theme: MyTheme,
    activity: Any? = null,
    content: @Composable () -> Unit
) {
    val composeThemeState = Preferences.rememberComposeTheme()
    ComposeTheme(
        state = composeThemeState,
        shapes = theme.shapes,
        typography = theme.typography
    ) {
        MyTheme(
            theme = theme
        ) {
            Platform.UpdateComposeThemeStatusBar(activity, composeThemeState)
            content()
        }
    }
}
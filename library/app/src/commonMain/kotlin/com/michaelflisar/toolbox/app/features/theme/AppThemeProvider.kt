package com.michaelflisar.toolbox.app.features.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.rememberComposeTheme
import com.michaelflisar.toolbox.app.platform.UpdateComposeThemeStatusBar

@Composable
fun AppThemeProvider(
    activity: Any? = null,
    content: @Composable () -> Unit
) {
    val composeThemeState = Preferences.rememberComposeTheme()
    ComposeTheme(
        state = composeThemeState,
        shapes = MaterialTheme.shapes,
        typography = MaterialTheme.typography
    ) {
        Platform.UpdateComposeThemeStatusBar(activity, composeThemeState)
        content()
    }
}
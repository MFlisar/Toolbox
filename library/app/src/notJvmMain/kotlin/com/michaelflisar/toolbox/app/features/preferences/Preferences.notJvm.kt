package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.platform.AppPrefs

actual val Preferences.BaseThemes: List<Preferences.AppBaseTheme>
    get() = Preferences.DefaultBaseThemes

@Composable
actual fun Preferences.collectBaseTheme(prefs: AppPrefs) = Preferences.collectDefaultBaseTheme(prefs)

@Composable
actual fun Preferences.rememberComposeTheme(setup: AppSetup): ComposeTheme.State {
    return rememberComposeThemeDefault(setup)
}
package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme

actual val Preferences.BaseThemes: List<Preferences.AppBaseTheme>
    get() = Preferences.DefaultBaseThemes

@Composable
actual fun Preferences.collectBaseTheme() =
    Preferences.collectDefaultBaseTheme()

@Composable
actual fun Preferences.rememberComposeTheme(): ComposeTheme.State {
    return rememberComposeThemeDefault()
}
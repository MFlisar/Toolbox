package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.platform.AppPrefs

private fun ComposeTheme.BaseTheme.toAppBaseTheme(): Preferences.AppBaseTheme {
    return Preferences.AppBaseTheme(
        text = this.name,
        icon = when (this) {
            ComposeTheme.BaseTheme.System -> CurrentDevice.icon
            ComposeTheme.BaseTheme.Dark -> Icons.Default.DarkMode
            ComposeTheme.BaseTheme.Light -> Icons.Default.LightMode
        },
        data = this
    )
}

object Preferences {

    class AppBaseTheme(
        val text: String,
        val icon: ImageVector,
        val data: Any
    )

    val DefaultBaseThemes = ComposeTheme.BaseTheme.entries.map(ComposeTheme.BaseTheme::toAppBaseTheme)

    @Composable
    fun collectDefaultBaseTheme(prefs: AppPrefs) = prefs.theme.asMutableStateNotNull(
        mapper = { base -> DefaultBaseThemes.find { it.data == base }!! },
        unmapper = { (it.data as ComposeTheme.BaseTheme) }
    )

    @Composable
    fun rememberComposeThemeDefault(setup: AppSetup): ComposeTheme.State {
        val theme = setup.prefs.theme.collectAsStateNotNull()
        val contrast = setup.prefs.contrast.asMutableStateNotNull()
        val dynamic = setup.prefs.dynamicTheme.asMutableStateNotNull()
        val customTheme = setup.prefs.customTheme.asMutableStateNotNull()
        return ComposeTheme.State(theme, contrast, dynamic, customTheme)
    }

}

expect val Preferences.BaseThemes: List<Preferences.AppBaseTheme>

@Composable
expect fun Preferences.collectBaseTheme(prefs: AppPrefs): MutableState<Preferences.AppBaseTheme>

@Composable
expect fun Preferences.rememberComposeTheme(setup: AppSetup): ComposeTheme.State



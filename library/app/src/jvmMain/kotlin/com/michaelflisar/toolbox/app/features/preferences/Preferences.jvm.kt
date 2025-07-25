package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.jewel.JewelBaseTheme
import com.michaelflisar.toolbox.app.jewel.JewelTheme
import com.michaelflisar.toolbox.app.platform.AppPrefs

private fun JewelTheme.toAppBaseTheme(): Preferences.AppBaseTheme {
    return Preferences.AppBaseTheme(
        text = this.label,
        icon = this.imageVector,
        data = this
    )
}

private val AppBaseThemeLight = JewelTheme.Light.toAppBaseTheme()
private val AppBaseThemeLightWithLightHeader = JewelTheme.LightWithLightHeader.toAppBaseTheme()
private val AppBaseThemeDark = JewelTheme.Dark.toAppBaseTheme()
private val AppBaseThemeSystem = JewelTheme.System.toAppBaseTheme()

actual val Preferences.BaseThemes: List<Preferences.AppBaseTheme>
    get() = listOf(AppBaseThemeLight, AppBaseThemeLightWithLightHeader, AppBaseThemeDark, AppBaseThemeSystem)

@Composable
actual fun Preferences.collectBaseTheme(prefs: AppPrefs): MutableState<Preferences.AppBaseTheme> {
    return prefs.jewelTheme.asMutableStateNotNull(
        mapper = { jewelTheme -> BaseThemes.find { it.data == jewelTheme }!! },
        unmapper = { (it.data as JewelTheme) }
    )
}

@Composable
actual fun Preferences.rememberComposeTheme(setup: AppSetup): ComposeTheme.State {
    val jewelTheme by setup.prefs.jewelTheme.collectAsStateNotNull()
    val theme = remember(jewelTheme) {
        mutableStateOf(jewelTheme.baseTheme().let {
            when (it) {
                JewelBaseTheme.Light -> ComposeTheme.BaseTheme.Light
                JewelBaseTheme.Dark -> ComposeTheme.BaseTheme.Dark
                JewelBaseTheme.System -> ComposeTheme.BaseTheme.System
            }
        })
    }
    val contrast = setup.prefs.contrast.asMutableStateNotNull()
    val dynamic = setup.prefs.dynamicTheme.asMutableStateNotNull()
    val customTheme = setup.prefs.customTheme.asMutableStateNotNull()
    return ComposeTheme.State(theme, contrast, dynamic, customTheme)
}
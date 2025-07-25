package com.michaelflisar.toolbox.windows.prefs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.ThemeDefault
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.windows.jewel.JewelBaseTheme
import com.michaelflisar.toolbox.windows.jewel.JewelTheme
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

object DefaultDesktopAppPrefs : SettingsModel(
    storage = DataStoreStorage.create(
        folder = File(Paths.get("").absolutePathString()),
        name = "window"
    )
), DesktopAppPrefs {

    // Window Einstellungen
    override val windowWidth by intPref(1024)
    override val windowHeight by intPref(800)
    override val windowX by intPref(0)
    override val windowY by intPref(0)
    override val windowPlacement by enumPref(WindowPlacement.Floating, WindowPlacement.entries)
    override val alwaysOnTop by boolPref(false)

    // Theme
    override val jewelTheme by enumPref(JewelTheme.System, JewelTheme.entries)
    override val contrast by enumPref(ComposeTheme.Contrast.System, ComposeTheme.Contrast.entries)
    override val theme by stringPref(ThemeDefault.Theme.id)

}

@Composable
fun rememberComposeThemeState(prefs: DesktopAppPrefs): ComposeTheme.State {
    val jewelTheme by prefs.jewelTheme.collectAsStateNotNull()
    val baseTheme = remember(jewelTheme) {
        mutableStateOf(jewelTheme.baseTheme().let {
            when (it) {
                JewelBaseTheme.Light -> ComposeTheme.BaseTheme.Light
                JewelBaseTheme.Dark -> ComposeTheme.BaseTheme.Dark
                JewelBaseTheme.System -> ComposeTheme.BaseTheme.System
            }
        })
    }
    val contrast = prefs.contrast.asMutableStateNotNull()
    val dynamic = remember { mutableStateOf(false) }
    val theme = prefs.theme.asMutableStateNotNull()
    return ComposeTheme.State(baseTheme, contrast, dynamic, theme)
}
package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.WindowPlacement
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.toolbox.app.interfaces.IAppPrefs
import com.michaelflisar.toolbox.app.jewel.JewelBaseTheme
import com.michaelflisar.toolbox.app.jewel.JewelTheme

interface DesktopAppPrefs : IAppPrefs {

    // windows window settings
    val windowWidth: StorageSetting<Int>
    val windowHeight: StorageSetting<Int>
    val windowX: StorageSetting<Int>
    val windowY: StorageSetting<Int>
    val windowPlacement: StorageSetting<WindowPlacement>
    val alwaysOnTop: StorageSetting<Boolean>

    // jewel theme
    val jewelTheme: StorageSetting<JewelTheme>

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
    val customTheme = prefs.customTheme.asMutableStateNotNull()
    return ComposeTheme.State(baseTheme, contrast, dynamic, customTheme)
}


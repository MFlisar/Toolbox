package com.michaelflisar.toolbox.app.features.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.jewel.JewelBaseTheme

@Composable
internal actual fun rememberComposeTheme(setup: AppSetup): ComposeTheme.State {
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
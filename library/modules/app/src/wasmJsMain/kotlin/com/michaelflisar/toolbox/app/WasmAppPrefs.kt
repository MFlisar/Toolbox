package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.interfaces.IAppPrefs

interface WasmAppPrefs  : IAppPrefs {

}

@Composable
fun rememberComposeThemeState(prefs: WasmAppPrefs): ComposeTheme.State {
    val theme = prefs.theme.collectAsStateNotNull()
    val contrast = prefs.contrast.asMutableStateNotNull()
    val dynamic = prefs.dynamicTheme.asMutableStateNotNull()
    val customTheme = prefs.customTheme.asMutableStateNotNull()
    return ComposeTheme.State(theme, contrast, dynamic, customTheme)
}

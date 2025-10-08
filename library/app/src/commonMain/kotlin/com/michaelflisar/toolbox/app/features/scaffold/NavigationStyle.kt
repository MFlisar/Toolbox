package com.michaelflisar.toolbox.app.features.scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState

enum class NavigationStyle {
    Left, Bottom
}

@Composable
fun rememberNavigationStyleAuto(): State<NavigationStyle> {
    val appState = LocalAppState.current
    return remember(appState.landscape) {
        derivedStateOf {
            if (appState.landscape) NavigationStyle.Left else NavigationStyle.Bottom
        }
    }
}
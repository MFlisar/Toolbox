package com.michaelflisar.toolbox.app.platform

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.app.defaults.BaseAppRootContent
import com.michaelflisar.toolbox.app.defaults.BaseAppRootLocals
import com.michaelflisar.toolbox.app.features.appstate.AppState
import com.michaelflisar.toolbox.app.rememberComposeThemeState

@Composable
fun BaseApp(
    appState: AppState,
    prefs: AppPrefs,
    content: @Composable () -> Unit,
) {
    val composeThemeState = rememberComposeThemeState(prefs)
    ComposeTheme(
        state = composeThemeState,
        shapes = MaterialTheme.shapes,
        typography = MaterialTheme.typography
    ) {
        BaseAppRootLocals(appState = appState) {
            BaseAppRootContent(appState = appState) {
                content()
            }
        }
    }
}
package com.michaelflisar.toolbox.app.features.root

import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.app.AppSetup

@androidx.compose.runtime.Composable
internal actual fun rememberComposeTheme(setup: AppSetup): ComposeTheme.State {
    return rememberComposeThemeDefault(setup)
}
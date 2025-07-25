package com.michaelflisar.toolbox.app.features.appstate

import androidx.compose.runtime.compositionLocalOf

val LocalAppState =
    compositionLocalOf<AppState> { throw RuntimeException("AppState not initialised!") }

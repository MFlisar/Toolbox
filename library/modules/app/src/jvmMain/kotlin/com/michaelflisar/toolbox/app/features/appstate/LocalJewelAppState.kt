package com.michaelflisar.toolbox.app.features.appstate

import androidx.compose.runtime.compositionLocalOf
import kotlin.RuntimeException

val LocalJewelAppState =
    compositionLocalOf<JewelAppState> { throw RuntimeException("JewelAppState not initialised!") }
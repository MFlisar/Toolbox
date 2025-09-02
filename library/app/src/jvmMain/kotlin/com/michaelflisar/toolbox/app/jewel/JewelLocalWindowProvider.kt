package com.michaelflisar.toolbox.app.jewel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.awt.ComposeWindow
import com.michaelflisar.toolbox.app.features.appstate.JewelAppState
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState

val LocalJewelWindowState =
    compositionLocalOf<ComposeWindow> { throw RuntimeException("JewelWindowState not initialised!") }
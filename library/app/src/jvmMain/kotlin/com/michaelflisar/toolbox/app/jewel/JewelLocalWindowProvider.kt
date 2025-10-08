package com.michaelflisar.toolbox.app.jewel

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.awt.ComposeWindow

internal val LocalJewelWindowState =
    compositionLocalOf<ComposeWindow> { throw RuntimeException("JewelWindowState not initialised!") }
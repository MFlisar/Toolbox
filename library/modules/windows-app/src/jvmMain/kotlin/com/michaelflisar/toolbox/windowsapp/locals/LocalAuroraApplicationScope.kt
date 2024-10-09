package com.michaelflisar.toolbox.windowsapp.locals

import androidx.compose.runtime.staticCompositionLocalOf
import org.pushingpixels.aurora.window.AuroraApplicationScope

val LocalAuroraApplicationScope = staticCompositionLocalOf<AuroraApplicationScope> {
    error("LocalAuroraApplicationScope not present")
}
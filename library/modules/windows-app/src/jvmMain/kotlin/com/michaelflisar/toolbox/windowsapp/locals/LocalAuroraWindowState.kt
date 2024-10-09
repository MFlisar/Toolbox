package com.michaelflisar.toolbox.windowsapp.locals

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf



val LocalAuroraWindowState = staticCompositionLocalOf {
    mutableStateOf(true)
}
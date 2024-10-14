package com.michaelflisar.toolbox.classes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

enum class Theme {
    System, Dark, Light;

    @Composable
    fun isDark(): Boolean =
        this == Dark || (this == System && isSystemInDarkTheme())

    @Composable
    fun <T> select(dark: T, light: T) = if (isDark()) dark else light
}


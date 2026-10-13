package com.michaelflisar.toolbox.classes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.extensions.isDark

class ThemeColor(
    val color: Color,
    val onLight: Color,
    val onDark: Color
) {
    fun onColor(color: Color) = if (color.isDark()) onDark else onLight

    val onBackground: Color
        @Composable get() = onColor(MaterialTheme.colorScheme.background)

    val onPrimary: Color
        @Composable get() = onColor(MaterialTheme.colorScheme.primary)
}
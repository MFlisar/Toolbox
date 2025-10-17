package com.michaelflisar.toolbox.classes

import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.extensions.isDark

class ThemeColor(
    val color: Color,
    val onLight: Color,
    val onDark: Color
) {
    fun forBackground(isDarkTheme: Boolean) = if (isDarkTheme) onDark else onLight
    fun forColor(color: Color) = if (color.isDark()) onDark else onLight
}
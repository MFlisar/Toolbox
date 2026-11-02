package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val Color.isDark
    get() = luminance() <= 0.5

val Color.isLight
    get() = !isDark

fun Color.lighten(factor: Float): Color {
    require(factor in 0f..1f) { "Factor must be between 0 and 1" }
    val r = red + (1 - red) * factor
    val g = green + (1 - green) * factor
    val b = blue + (1 - blue) * factor
    return Color(r, g, b, alpha)
}

fun Color.darken(factor: Float): Color {
    require(factor in 0f..1f) { "Factor must be between 0 and 1" }
    val r = red * (1 - factor)
    val g = green * (1 - factor)
    val b = blue * (1 - factor)
    return Color(r, g, b, alpha)
}
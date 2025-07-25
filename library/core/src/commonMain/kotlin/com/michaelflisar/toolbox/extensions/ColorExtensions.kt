package com.michaelflisar.toolbox.extensions

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.takeOrElse

@Composable
fun Color.onColor(): Color {
    return MaterialTheme.colorScheme.contentColorFor(this)
        .takeOrElse { if (isLight()) Color.Black else Color.White }
}

fun Color.isLight() = luminance() >= .5f
fun Color.isDark() = luminance() < .5f

fun Color.disabled() = copy(alpha = 0.38f)

fun Color.variant(factor: Float = .8f) = copy(alpha = factor)

/*
 * factor... 0.0f = no change, 1.0f = full change
 */
fun Color.darkenOrLighten(factor: Float = .1f) = if (isDark()) lighten(factor) else darken(factor)

/*
 * factor... 0.0f = no change, 1.0f = full change
 */
fun Color.darken(factor: Float = 0.1f): Color {
    return copy(
        red = (red * (1 - factor)).coerceIn(0f, 1f),
        green = (green * (1 - factor)).coerceIn(0f, 1f),
        blue = (blue * (1 - factor)).coerceIn(0f, 1f)
    )
}

/*
 * factor... 0.0f = no change, 1.0f = full change
 */
fun Color.lighten(factor: Float = 0.1f): Color {
    return copy(
        red = (red + (1 - red) * factor).coerceIn(0f, 1f),
        green = (green + (1 - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1 - blue) * factor).coerceIn(0f, 1f)
    )
}
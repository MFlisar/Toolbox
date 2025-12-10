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

/**
 * @param factorOnDark sollte etwas höher als factorOnLight sein, da der Unterschied auf dunklem Hintergrund weniger auffällt
 * @param factorOnLight sollte etwas niedriger als factorOnDark sein, da der Unterschied auf hellem Hintergrund stärker auffällt
 */
fun Color.variant(
    factorOnDark: Float = .2f,
    factorOnLight: Float = .3f,
) = darkenOrLighten(factor = if (!isDark()) factorOnDark else factorOnLight)

/**
 * in material2 war der factor immer .6f, aber in light (also wenn die Farbe selbst dunkel ist) ist diese Änderung etwas zu stark, daher nutzen wir hier unterschiedliche Werte
 */
fun Color.secondary() = copy(alpha = if (isDark()) .7f else .6f)
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
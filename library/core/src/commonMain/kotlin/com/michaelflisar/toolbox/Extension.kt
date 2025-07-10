package com.michaelflisar.toolbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Color.isLight() = luminance() >= .5f
fun Color.isDark() = luminance() < .5f

fun Color.disabled() = copy(alpha = 0.38f)

fun Color.variant(factor: Float = .8f) = copy(alpha = factor)
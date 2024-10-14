package com.michaelflisar.toolbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Color.disabled() = copy(alpha = 0.38f)
fun Color.isDark() = luminance() <= 2f
package com.michaelflisar.toolbox.app.features.composablesize

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp

@Stable
class SizeClass(
    val widthSizeClass: WidthSizeClass,
    val heightSizeClass: HeightSizeClass,
    val maxWidth: Dp,
    val maxHeight: Dp
)
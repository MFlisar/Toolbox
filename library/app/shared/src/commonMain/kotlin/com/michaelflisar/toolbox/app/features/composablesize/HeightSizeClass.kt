package com.michaelflisar.toolbox.app.features.composablesize

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

/**
 * prüft ob die Höhe eines Composables
 * nutzt dafür die Breakpoints aus [WindowSizeClass] + fügt small hinzu
 */
enum class HeightSizeClass(
    val breakpoint: Dp,
) {
    Compact(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND.dp), // 480
    Medium(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND.dp), // 900
    Large(0.dp)
    ;

    companion object {

        fun from(density: Density, widthPx: Int): HeightSizeClass {
            return from(with(density) { widthPx.toDp() })
        }

        fun from(dpWidth: Dp): HeightSizeClass {
            return when {
                dpWidth < Compact.breakpoint -> Compact
                dpWidth < Medium.breakpoint -> Medium
                else -> Large
            }
        }
    }
}
package com.michaelflisar.toolbox.app.features.composablesize

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

/**
 * prüft ob die Breite
 * nutzt dafür die Breakpoints aus [WindowSizeClass] + fügt small hinzu
 */
enum class WidthSizeClass(
    val breakpoint: Dp
)  {
    Small(480.dp),
    Compact(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND.dp), // 600
    Medium(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND.dp), // 840
    Large(0.dp)
    ;

    companion object {

        fun from(density: Density, widthPx: Int) : WidthSizeClass {
            return from(with(density) { widthPx.toDp() })
        }

        fun from(dpWidth: Dp): WidthSizeClass {
            return when {
                dpWidth < Small.breakpoint -> Small
                dpWidth < Compact.breakpoint -> Compact
                dpWidth < Medium.breakpoint -> Medium
                else -> Large
            }
        }
    }
}
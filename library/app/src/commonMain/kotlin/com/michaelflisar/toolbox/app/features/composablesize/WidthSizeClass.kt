package com.michaelflisar.toolbox.app.features.composablesize

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass

/**
 * prüft ob die Breite
 * nutzt dafür die Breakpoints aus [WindowWidthSizeClass] + fügt small hinzu
 */
enum class WidthSizeClass(
    val breakpoint: Dp
)  {
    Small(480.dp),
    Compact(600.dp),
    Medium(840.dp),
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
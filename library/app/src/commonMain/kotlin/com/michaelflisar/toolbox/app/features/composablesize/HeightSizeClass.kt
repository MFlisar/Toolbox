package com.michaelflisar.toolbox.app.features.composablesize

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass

/**
 * prüft ob die Höhe eines Composables
 * nutzt dafür die Breakpoints aus [WindowHeightSizeClass] + fügt small hinzu
 */
enum class HeightSizeClass(
    val breakpoint: Dp,
) {
    Compact(480.dp),
    Medium(900.dp),
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
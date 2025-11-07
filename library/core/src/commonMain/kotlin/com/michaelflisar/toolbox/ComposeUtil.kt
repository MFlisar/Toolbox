package com.michaelflisar.toolbox

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.IntSize

object ComposeUtil {

    @Composable
    fun MeasureSize(
        composable: @Composable () -> Unit,
        onMeasured: (width: Int, height: Int) -> Unit
    ) {
        MeasureMaxSize(listOf(composable), onMeasured)
    }

    @Composable
    fun MeasureMaxSize(
        composables: List<@Composable () -> Unit>,
        onMeasured: (width: Int, height: Int) -> Unit,
    ) {
        SubcomposeLayout { constraints ->
            var maxWidth = 0
            var maxHeight = 0
            composables.forEach { composable ->
                val placeables = subcompose(composable) {
                    composable()
                }.map { it.measure(constraints) }

                val width = placeables.maxOfOrNull { it.width } ?: 0
                if (width > maxWidth) maxWidth = width
                val height = placeables.maxOfOrNull { it.height } ?: 0
                if (height > maxHeight) maxHeight = height
            }
            onMeasured(maxWidth, maxHeight)
            layout(0, 0) {} // Kein UI sichtbar, nur Messung
        }
    }
}
package com.michaelflisar.toolbox.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp

internal object TableUtil {

    @Composable
    fun <T> MeasureMaxWidth(
        items: List<T>,
        render: @Composable (T) -> Unit,
        onMeasured: (Dp) -> Unit
    ) {
        SubcomposeLayout { constraints ->
            var maxWidth = 0

            items.forEach { item ->
                val placeables = subcompose(item) {
                    render(item)
                }.map { it.measure(constraints) }

                val width = placeables.maxOfOrNull { it.width } ?: 0
                if (width > maxWidth) maxWidth = width
            }

            // Übergib maxWidth in Dp
            onMeasured(maxWidth.toDp())

            layout(0, 0) {} // Kein UI sichtbar, nur Messung
        }
    }
}
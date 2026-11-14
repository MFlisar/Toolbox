package com.michaelflisar.toolbox.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ComposeUtil

internal object TableUtil {

    @Composable
    fun <T> MeasureMaxWidth(
        items: List<T>,
        render: @Composable (T) -> Unit,
        onMeasured: (Dp) -> Unit,
    ) {
        val density = LocalDensity.current
        ComposeUtil.MeasureMaxSize(
            composables = items.map { item -> { render(item) } },
            onMeasured = { width, _ -> onMeasured(with(density) { width.toDp() }) }
        )
    }
}
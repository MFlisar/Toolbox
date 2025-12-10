package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.padding


@Composable
actual fun MyScrollableLazyHorizontalGrid(
    modifier: Modifier,
    gridRows: GridCells,
    verticalArrangement: Arrangement.Vertical,
    horizontalArrangement: Arrangement.Horizontal,
    gridState: LazyGridState,
    overlapScrollbar: Boolean,
    content: LazyGridScope.() -> Unit
) {
    LazyHorizontalGrid(
        modifier = modifier,
        rows = gridRows,
        contentPadding = PaddingValues(MaterialTheme.padding.default),
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        state = gridState
    ) {
        content()
    }
}
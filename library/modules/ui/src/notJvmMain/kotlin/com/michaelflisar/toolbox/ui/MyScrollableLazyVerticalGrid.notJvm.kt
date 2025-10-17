package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.michaelflisar.toolbox.LocalTheme

@Composable
actual fun MyScrollableLazyVerticalGrid(
    modifier: Modifier,
    gridCells: GridCells,
    verticalArrangement: Arrangement.Vertical,
    horizontalArrangement: Arrangement.Horizontal,
    gridState: LazyGridState,
    overlapScrollbar: Boolean,
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = gridCells,
        contentPadding = PaddingValues(LocalTheme.current.padding.default),
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        state = gridState
    ) {
        content()
    }
}
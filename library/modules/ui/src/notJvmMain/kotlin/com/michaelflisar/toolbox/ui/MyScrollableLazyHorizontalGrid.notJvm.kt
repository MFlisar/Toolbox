package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.classes.LocalStyle

@Composable
actual fun MyScrollableLazyHorizontalGrid(
    modifier: Modifier,
    gridRows: GridCells,
    verticalArrangement: Arrangement.Vertical,
    horizontalArrangement: Arrangement.Horizontal,
    gridState: LazyGridState,
    content: LazyGridScope.() -> Unit
) {
    LazyHorizontalGrid(
        modifier = modifier,
        rows = gridRows,
        contentPadding = PaddingValues(LocalStyle.current.paddingDefault),
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        state = gridState
    ) {
        content()
    }
}
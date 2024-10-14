package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MyScrollableLazyHorizontalGrid(
    modifier: Modifier = Modifier,
    gridRows: GridCells = GridCells.Fixed(2),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    gridState: LazyGridState,
    content: LazyGridScope.() -> Unit
)
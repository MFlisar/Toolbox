package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MyScrollableLazyVerticalGrid(
    modifier: Modifier = Modifier,
    gridCells: GridCells = GridCells.Fixed(2),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    gridState: LazyGridState = rememberLazyGridState(),
    overlapScrollbar: Boolean = false,
    content: LazyGridScope.() -> Unit
)
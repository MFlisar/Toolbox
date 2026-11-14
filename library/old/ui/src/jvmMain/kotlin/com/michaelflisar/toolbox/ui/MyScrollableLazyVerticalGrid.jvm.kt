package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.theme.scrollbar


@Composable
actual fun MyScrollableLazyVerticalGrid(
    modifier: Modifier,
    gridCells: GridCells,
    verticalArrangement: Arrangement.Vertical,
    horizontalArrangement: Arrangement.Horizontal,
    gridState: LazyGridState,
    overlapScrollbar: Boolean,
    content: LazyGridScope.() -> Unit,
) {
    val data = rememberScrollableData(gridState, overlapScrollbar)
    Box(modifier = modifier) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = data.paddingForScrollbar),
            columns = gridCells,
            contentPadding = PaddingValues(MaterialTheme.scrollbar.paddingForScrollbar),
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            state = gridState
        ) {
            content()
        }
        if (data.showScrollbar) {
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                    .width(MaterialTheme.scrollbar.size),
                adapter = rememberScrollbarAdapter(gridState)
            )
        }
    }
}
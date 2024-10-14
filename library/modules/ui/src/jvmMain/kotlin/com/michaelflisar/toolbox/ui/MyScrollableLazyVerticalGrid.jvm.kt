package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
actual fun MyScrollableLazyVerticalGrid(
    modifier: Modifier,
    gridCells: GridCells,
    verticalArrangement: Arrangement.Vertical,
    horizontalArrangement: Arrangement.Horizontal,
    gridState: LazyGridState,
    content: LazyGridScope.() -> Unit
) {
    Box(modifier = modifier) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = ToolboxDefaults.SCROLLBAR_SPACE),
            columns = gridCells,
            contentPadding = PaddingValues(ToolboxDefaults.ITEM_SPACING),
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            state = gridState
        ) {
            content()
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(gridState)
        )
    }
}
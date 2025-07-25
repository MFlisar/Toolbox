package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.classes.LocalStyle

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
    Box(modifier = modifier) {
        LazyHorizontalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (overlapScrollbar) 0.dp else LocalStyle.current.paddingForScrollbar),
            rows = gridRows,
            contentPadding = PaddingValues(LocalStyle.current.paddingForScrollbar),
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            state = gridState
        ) {
            content()
        }

        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(LocalStyle.current.scrollbar),
            adapter = rememberScrollbarAdapter(gridState)
        )
    }
}
package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun MyLazyVerticalGrid(
    modifier: Modifier = Modifier,
    gridCells: GridCells = GridCells.Fixed(2),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: LazyGridScope.() -> Unit
) {
    val gridState = rememberLazyGridState()
    Box(modifier = modifier) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            columns = gridCells,
            contentPadding = PaddingValues(AppTheme.ITEM_SPACING),
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
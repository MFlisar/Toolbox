package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun MyScrollableLazyColumn(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = AppTheme.ITEM_SPACING,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            horizontalAlignment = horizontalAlignment,
            state = state
        ) {
            content()
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(state)
        )
    }
}
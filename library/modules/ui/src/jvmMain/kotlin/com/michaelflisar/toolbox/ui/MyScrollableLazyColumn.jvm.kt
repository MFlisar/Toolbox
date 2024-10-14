package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
actual fun MyScrollableLazyColumn(
    modifier: Modifier,
    itemSpacing: Dp,
    horizontalAlignment: Alignment.Horizontal,
    state: LazyListState,
    overlapScrollbar: Boolean,
    content: LazyListScope.() -> Unit
) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = if (overlapScrollbar) 0.dp else ToolboxDefaults.SCROLLBAR_SPACE),
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
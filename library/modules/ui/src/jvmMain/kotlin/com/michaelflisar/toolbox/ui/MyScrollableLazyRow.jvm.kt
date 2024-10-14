package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
actual fun MyScrollableLazyRow(
    modifier: Modifier,
    itemSpacing: Dp,
    verticalAlignment: Alignment.Vertical,
    state: LazyListState,
    content: LazyListScope.() -> Unit
) {
    Box(modifier = modifier) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ToolboxDefaults.SCROLLBAR_SPACE),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalAlignment = verticalAlignment,
            state = state
        ) {
            content()
        }
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(state)
        )
    }
}
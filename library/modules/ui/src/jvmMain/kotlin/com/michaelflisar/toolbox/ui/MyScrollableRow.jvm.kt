package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import com.michaelflisar.toolbox.LocalTheme

@Composable
actual fun MyScrollableRow(
    modifier: Modifier,
    itemSpacing: Dp,
    verticalAlignment: Alignment.Vertical,
    scrollState: ScrollState,
    overlapScrollbar: Boolean,
    content: @Composable RowScope.() -> Unit
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(bottom = if (overlapScrollbar) 0.dp else LocalTheme.current.scrollbar.paddingForScrollbar),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalAlignment = verticalAlignment
        ) {
            content()
        }
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(LocalTheme.current.scrollbar.size),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}
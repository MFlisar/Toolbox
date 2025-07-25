package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.classes.LocalStyle

@Composable
actual fun MyScrollableColumn(
    modifier: Modifier,
    itemSpacing: Dp,
    horizontalAlignment: Alignment.Horizontal,
    scrollState: ScrollState,
    overlapScrollbar: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(end = if (overlapScrollbar) 0.dp else LocalStyle.current.paddingForScrollbar),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            horizontalAlignment = horizontalAlignment
        ) {
            content()
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(LocalStyle.current.scrollbar),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}
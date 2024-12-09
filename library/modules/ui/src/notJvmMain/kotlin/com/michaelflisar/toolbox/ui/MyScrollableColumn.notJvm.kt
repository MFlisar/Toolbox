package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
actual fun MyScrollableColumn(
    modifier: Modifier,
    itemSpacing: Dp,
    horizontalAlignment: Alignment.Horizontal,
    scrollState: ScrollState,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        horizontalAlignment = horizontalAlignment
    ) {
        content()
    }
}
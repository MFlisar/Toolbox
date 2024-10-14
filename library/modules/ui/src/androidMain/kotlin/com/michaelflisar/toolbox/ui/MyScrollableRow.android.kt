package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
actual fun MyScrollableRow(
    modifier: Modifier,
    itemSpacing: Dp,
    verticalAlignment: Alignment.Vertical,
    scrollState: ScrollState,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = verticalAlignment
    ) {
        content()
    }
}
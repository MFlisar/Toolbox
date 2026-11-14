package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
actual fun MyScrollableLazyRow(
    modifier: Modifier,
    itemSpacing: Dp,
    verticalAlignment: Alignment.Vertical,
    state: LazyListState,
    overlapScrollbar: Boolean,
    content: LazyListScope.() -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = verticalAlignment,
        state = state
    ) {
        content()
    }
}
package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
expect fun MyScrollableLazyRow(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = ToolboxDefaults.ITEM_SPACING,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
)
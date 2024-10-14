package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
expect fun MyScrollableRow(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = ToolboxDefaults.ITEM_SPACING,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable RowScope.() -> Unit
)
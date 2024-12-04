package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ToolboxDefaults

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyFlowRow(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = ToolboxDefaults.ITEM_SPACING,
    horizontalArrangement: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Alignment.Vertical = Alignment.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    maxLines: Int = Int.MAX_VALUE,
    overflow: FlowRowOverflow = FlowRowOverflow.Clip,
    content: @Composable FlowRowScope.() -> Unit
) {
    FlowRow (
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing, horizontalArrangement),
        verticalArrangement = Arrangement.spacedBy(itemSpacing, verticalArrangement),
        maxItemsInEachRow = maxItemsInEachRow,
        maxLines = maxLines,
        overflow = overflow,
        content = content
    )
}
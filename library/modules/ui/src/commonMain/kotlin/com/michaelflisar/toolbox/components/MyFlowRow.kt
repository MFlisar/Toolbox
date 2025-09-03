package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.classes.LocalStyle

@Composable
fun MyFlowRow(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = LocalStyle.current.spacingDefault,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    verticalItemAlignment: Alignment.Vertical = Alignment.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    maxLines: Int = Int.MAX_VALUE,
    content: @Composable FlowRowScope.() -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing, horizontalAlignment),
        verticalArrangement = Arrangement.spacedBy(itemSpacing, verticalAlignment),
        itemVerticalAlignment = verticalItemAlignment,
        maxItemsInEachRow = maxItemsInEachRow,
        maxLines = maxLines,
        content = content
    )
}
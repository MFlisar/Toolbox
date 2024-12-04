package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
fun MyRow(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = ToolboxDefaults.ITEM_SPACING,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = verticalArrangement,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing, horizontalAlignment),
        content = content
    )
}
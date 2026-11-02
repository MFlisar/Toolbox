package com.michaelflisar.toolbox.ui.adaptive.implementations.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme


@Composable
fun StyledRow(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = StyledTheme.spacing.medium,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing, horizontalAlignment),
        content = content
    )
}
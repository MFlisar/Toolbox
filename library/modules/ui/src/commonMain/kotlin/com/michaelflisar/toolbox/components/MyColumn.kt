package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

import com.michaelflisar.toolbox.LocalTheme

@Composable
fun MyColumn(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = LocalTheme.current.spacing.default,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.spacedBy(itemSpacing, verticalAlignment),
        content = content
    )
}
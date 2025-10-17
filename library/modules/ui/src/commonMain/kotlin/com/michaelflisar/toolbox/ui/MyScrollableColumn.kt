package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

import com.michaelflisar.toolbox.LocalTheme

@Composable
expect fun MyScrollableColumn(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = LocalTheme.current.spacing.default,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollState: ScrollState = rememberScrollState(),
    overlapScrollbar: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
)
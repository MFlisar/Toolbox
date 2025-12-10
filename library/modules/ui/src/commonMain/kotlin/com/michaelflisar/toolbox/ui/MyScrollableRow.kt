package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.spacing


@Composable
expect fun MyScrollableRow(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = MaterialTheme.spacing.default,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    scrollState: ScrollState = rememberScrollState(),
    overlapScrollbar: Boolean = false,
    content: @Composable RowScope.() -> Unit
)
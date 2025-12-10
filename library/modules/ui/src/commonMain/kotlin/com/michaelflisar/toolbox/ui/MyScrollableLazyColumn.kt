package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.spacing


@Composable
expect fun MyScrollableLazyColumn(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = MaterialTheme.spacing.default,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    state: LazyListState = rememberLazyListState(),
    overlapScrollbar: Boolean = false,
    content: LazyListScope.() -> Unit
)
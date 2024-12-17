package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.classes.LocalStyle

@Composable
expect fun MyScrollableLazyColumn(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = LocalStyle.current.spacingDefault,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    state: LazyListState = rememberLazyListState(),
    overlapScrollbar: Boolean = false,
    content: LazyListScope.() -> Unit
)
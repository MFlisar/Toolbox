package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun MyScrollableColumn(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = AppTheme.ITEM_SPACING,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            horizontalAlignment = horizontalAlignment
        ) {
            content()
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}
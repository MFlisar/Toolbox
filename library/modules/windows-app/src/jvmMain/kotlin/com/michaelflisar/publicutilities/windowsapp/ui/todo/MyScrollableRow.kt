package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun MyScrollableRow(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = AppTheme.ITEM_SPACING,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalAlignment = verticalAlignment
        ) {
            content()
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalScrollbar(
            modifier = Modifier.fillMaxWidth(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}
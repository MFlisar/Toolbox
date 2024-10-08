package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun RowScope.MyVerticalDivider(
    modifier: Modifier = Modifier,
    size: Dp = AppTheme.DIVIDER_SIZE,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    VerticalDivider(modifier = modifier.width(size).fillMaxHeight(), color = color)
}
package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun ColumnScope.MyHorizontalDivider(
    modifier: Modifier = Modifier,
    size: Dp = AppTheme.DIVIDER_SIZE,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    HorizontalDivider(
        modifier = modifier.height(size),//.fillMaxWidth(),
        color = color
    )
}
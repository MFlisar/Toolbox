package com.michaelflisar.toolbox.tasks.ui.parts

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProgressIcon(
    size: Dp = 24.dp,
) {
    CircularProgressIndicator(
        modifier = Modifier.size(size),
        strokeWidth = 2.dp,
        color = LocalContentColor.current,
        trackColor = LocalContentColor.current.copy(alpha = 0.2f)
    )
}
package com.michaelflisar.toolbox.tasks.ui.parts

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.tokens.LocalStyledContentColor
import com.michaelflisar.composestyled.ui.components.StyledCircularProgressIndicator

@Composable
fun ProgressIcon(
    size: Dp = StyledTheme.sizes.iconSize,
) {
    StyledCircularProgressIndicator(
        modifier = Modifier.size(size),
        strokeWidth = 2.dp,
        color = LocalStyledContentColor.current,
        trackColor = LocalStyledContentColor.current.copy(alpha = 0.2f)
    )
}
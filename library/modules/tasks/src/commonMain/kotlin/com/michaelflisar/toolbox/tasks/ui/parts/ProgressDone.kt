package com.michaelflisar.toolbox.tasks.ui.parts

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.tokens.LocalStyledContentColor
import com.michaelflisar.composestyled.ui.components.StyledCircularProgressIndicator
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.icons.StyledIcons

@Composable
fun ProgressDone(
    size: Dp = StyledTheme.sizes.iconSize,
) {
    StyledIcon(
        modifier = Modifier.size(size),
        icon = StyledIcons.Check
    )
}
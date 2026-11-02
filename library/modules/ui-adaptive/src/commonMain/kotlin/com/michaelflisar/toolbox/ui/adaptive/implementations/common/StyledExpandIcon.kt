package com.michaelflisar.toolbox.ui.adaptive.implementations.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme
import com.michaelflisar.toolbox.ui.adaptive.components.StyledIcon
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledContentColor

@Composable
fun StyledExpandIcon(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    sideIsLeft: Boolean = true,
    color: Color = LocalStyledContentColor.current,
    icon: ImageVector = Icons.Default.ArrowDropDown,
    size: Dp? = StyledTheme.sizes.iconSize,
) {
    val rotation by animateFloatAsState(if (expanded) (if (sideIsLeft) -180f else 180f) else 0f)
    StyledIcon(
        modifier = modifier.rotate(rotation),
        imageVector = icon,
        tint = color,
        contentDescription = null,
        size = size
    )
}
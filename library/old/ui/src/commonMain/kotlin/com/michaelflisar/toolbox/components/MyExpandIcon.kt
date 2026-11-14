package com.michaelflisar.toolbox.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.theme.LocalComponentStyles
import com.michaelflisar.toolbox.theme.icon

@Composable
fun MyExpandIcon(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    sideIsLeft: Boolean = true,
    color: Color? = null,
    icon: ImageVector = Icons.Default.ArrowDropDown,
    size: Dp? = MaterialTheme.icon.size
) {
    val rotation by animateFloatAsState(if (expanded) (if (sideIsLeft) -180f else 180f) else 0f)
    MyIcon(
        modifier = modifier.rotate(rotation),
        imageVector = icon,
        tint = color?.takeIf { it != Color.Unspecified } ?: LocalContentColor.current,
        contentDescription = null,
        size = size
    )
}
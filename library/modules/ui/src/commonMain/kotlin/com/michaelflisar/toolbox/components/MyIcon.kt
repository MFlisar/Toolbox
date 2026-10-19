package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.theme.icon

@Composable
fun MyIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp? = MaterialTheme.icon.size,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.then(
            if (size != null) {
                Modifier.size(size)
            } else {
                Modifier
            }
        ),
        tint = tint
    )
}

@Composable
fun MyIcon(
    icon: IconComposable,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp? = MaterialTheme.icon.size,
    tint: Color = LocalContentColor.current,
) {
    com.michaelflisar.toolbox.extensions.Icon(
        icon = icon,
        contentDescription = contentDescription,
        modifier = modifier.then(
            if (size != null) {
                Modifier.size(size)
            } else {
                Modifier
            }
        ),
        tint = tint
    )

}
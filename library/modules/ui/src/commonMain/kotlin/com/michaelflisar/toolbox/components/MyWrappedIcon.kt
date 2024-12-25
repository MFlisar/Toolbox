package com.michaelflisar.toolbox.components

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.classes.WrappedIcon

@Composable
fun MyWrappedIcon(
    icon: WrappedIcon,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current
) {
    when (icon) {
        is WrappedIcon.ImageVector -> Icon(icon.icon, contentDescription, modifier, tint)
        is WrappedIcon.Painter -> Icon(icon.painter, contentDescription, modifier, tint)
    }

}
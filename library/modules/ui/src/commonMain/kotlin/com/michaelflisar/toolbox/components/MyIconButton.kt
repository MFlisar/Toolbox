package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MyIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    tint: Color? = null,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
        enabled = enabled
    ) {
        Icon(
            modifier = Modifier.padding(iconPaddingValues),
            imageVector = icon,
            contentDescription = null,
            tint = tint ?: LocalContentColor.current
        )
    }
}

@Composable
fun MyOutlinedIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    tint: Color? = null,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedIconButton(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
        enabled = enabled
    ) {
        Icon(
            modifier = Modifier.padding(iconPaddingValues),
            imageVector = icon,
            contentDescription = null,
            tint = tint ?: LocalContentColor.current
        )
    }
}
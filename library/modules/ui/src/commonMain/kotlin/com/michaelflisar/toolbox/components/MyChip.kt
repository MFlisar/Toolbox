package com.michaelflisar.toolbox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing


@Composable
fun MyChip(
    title: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    endIcon: (@Composable () -> Unit)? = null,
    containerColor: Color = Color.Unspecified,
    labelColor: Color = Color.Unspecified,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    maxLines: Int = 1,
    onClick: (() -> Unit)? = null
) {
    val m = modifier
        .clip(MaterialTheme.shapes.small)
        .background(color = containerColor.takeIf { it != Color.Unspecified } ?: Color.Transparent)
        .border(1.dp, borderColor, MaterialTheme.shapes.small)
        .then(
            if (onClick != null) Modifier.clickable { onClick() }
            else Modifier
        )
        .padding(MaterialTheme.padding.small)

    CompositionLocalProvider(
        LocalContentColor provides (labelColor.takeIf { it != Color.Unspecified }
            ?: LocalContentColor.current)
    ) {
        Row(
            modifier = m,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.small,
                Alignment.CenterHorizontally
            )
        ) {
            if (icon != null) {
                Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                    icon()
                }

            }
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    maxLines = maxLines,
                    color = labelColor,
                    style = style
                )
            }
            if (endIcon != null) {
                Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                    endIcon()
                }
            }
        }
    }
}

@Composable
fun MyChip(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    endIcon: (@Composable () -> Unit)? = null,
    containerColor: Color = Color.Unspecified,
    labelColor: Color = Color.Unspecified,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val m = modifier
        .clip(MaterialTheme.shapes.small)
        .background(color = containerColor.takeIf { it != Color.Unspecified } ?: Color.Transparent)
        .border(1.dp, borderColor, MaterialTheme.shapes.small)
        .then(
            if (onClick != null) Modifier.clickable { onClick() }
            else Modifier
        )
        .padding(MaterialTheme.padding.small)

    CompositionLocalProvider(
        LocalContentColor provides (labelColor.takeIf { it != Color.Unspecified }
            ?: LocalContentColor.current)
    ) {
        Row(
            modifier = m,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.small,
                Alignment.CenterHorizontally
            )
        ) {
            if (icon != null) {
                Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                    icon()
                }
            }
            content()
            if (endIcon != null) {
                Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                    endIcon()
                }
            }
        }
    }
}
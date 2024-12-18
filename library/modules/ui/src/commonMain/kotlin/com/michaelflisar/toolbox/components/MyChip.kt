package com.michaelflisar.toolbox.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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

@Composable
fun MyChip(
    modifier: Modifier = Modifier,
    title: String,
    icon: (@Composable () -> Unit)? = null,
    color: Color = Color.Unspecified,
    borderColor: Color = MaterialTheme.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    maxLines: Int = 1
) {
    val m = modifier
        .clip(MaterialTheme.shapes.small)
        .border(1.dp, borderColor, MaterialTheme.shapes.small)
        .padding(4.dp)

    if (icon != null) {
        Row(
            modifier = m,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CompositionLocalProvider(LocalContentColor provides (color.takeIf { it != Color.Unspecified } ?: LocalContentColor.current)) {
                icon()
            }
            Text(
                text = title,
                maxLines = maxLines,
                color = color,
                style = style
            )
        }
    } else {
        Text(
            modifier = m,
            text = title,
            maxLines = maxLines,
            color = color,
            style = style
        )
    }
}
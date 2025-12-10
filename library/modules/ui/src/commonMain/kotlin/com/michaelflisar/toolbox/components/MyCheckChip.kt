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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing


@Composable
fun MyCheckChip(
    title: String,
    state: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    color: Color = Color.Unspecified,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    maxLines: Int = 1
) {
    MyCheckChip(title, state.value, modifier, icon, color, borderColor, style, maxLines) {
        state.value = it
    }
}

@Composable
fun MyCheckChip(
    title: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    color: Color = Color.Unspecified,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    maxLines: Int = 1,
    onCheckedChange: (Boolean) -> Unit = {}
) {

    val colorSelected =
        color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary
    val colorSelectedText = MaterialTheme.colorScheme.contentColorFor(colorSelected)

    val colorNotSelected = Color.Unspecified
    val colorNotSelectedText = Color.Unspecified

    val m = modifier
        .clip(MaterialTheme.shapes.small)
        .border(1.dp, borderColor, MaterialTheme.shapes.small)
        .background(if (checked) colorSelected else colorNotSelected)
        .clickable {
            onCheckedChange(!checked)
        }
        //.widthIn(min = 40.dp)
        .padding(MaterialTheme.padding.small)

    if (icon != null) {
        Row(
            modifier = m,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small, Alignment.CenterHorizontally)
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (checked) colorSelectedText else colorNotSelectedText
            ) {
                Box(modifier = Modifier.size(18.dp)) {
                    icon()
                }
            }
            Text(
                text = title,
                maxLines = maxLines,
                color = if (checked) colorSelectedText else colorNotSelectedText,
                style = style
            )
        }
    } else {
        Text(
            modifier = m,
            text = title,
            maxLines = maxLines,
            color = if (checked) colorSelectedText else colorNotSelectedText,
            style = style,
            textAlign = TextAlign.Center
        )
    }
}
package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.disabled

@Composable
fun MyCheckChip(
    modifier: Modifier = Modifier,
    title: String,
    icon: (@Composable () -> Unit)? = null,
    state: MutableState<Boolean>,
    color: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    maxLines: Int = 1
) {
    MyCheckChip(modifier, title, icon, state.value, color, style, maxLines) {
        state.value = it
    }
}

@Composable
fun MyCheckChip(
    modifier: Modifier = Modifier,
    title: String,
    icon: (@Composable () -> Unit)? = null,
    checked: Boolean,
    color: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    maxLines: Int = 1,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    val borderColor = color.takeIf { it != Color.Unspecified }?.disabled()
        ?: MaterialTheme.colorScheme.onSurface.disabled()

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
        .padding(4.dp)

    if (icon != null) {
        Row(
            modifier = m,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CompositionLocalProvider(LocalContentColor provides if (checked) colorSelectedText else colorNotSelectedText) {
                icon()
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
            style = style
        )
    }
}
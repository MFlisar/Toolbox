package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.disabled

@Composable
fun MyCheckChip(
    modifier: Modifier = Modifier,
    title: String,
    state: MutableState<Boolean>,
    color: Color = Color.Unspecified
) {
    MyCheckChip(modifier, title, state.value, color) {
        state.value = it
    }
}

@Composable
fun MyCheckChip(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    color: Color = Color.Unspecified,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    val borderColor = color.takeIf { it != Color.Unspecified }?.disabled()
        ?: MaterialTheme.colorScheme.onSurface.disabled()

    val colorSelected =
        color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary
    val colorSelectedText = MaterialTheme.colorScheme.contentColorFor(colorSelected)

    val colorNotSelected = Color.Unspecified
    val colorNotSelectedText = Color.Unspecified

    Text(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .border(1.dp, borderColor, MaterialTheme.shapes.small)
            .background(if (checked) colorSelected else colorNotSelected)
            .clickable {
                onCheckedChange(!checked)
            }
            .widthIn(min = 40.dp)
            .padding(4.dp),
        text = title,
        maxLines = 1,
        color = if (checked) colorSelectedText else colorNotSelectedText
    )
}
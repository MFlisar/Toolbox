package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    checked: MutableState<Boolean>,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    MyCheckbox(modifier, title, checked.value, color, colorUnselected, fontWeight) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .then(if (onCheckedChange != null) {
                Modifier.clickable { onCheckedChange.invoke(!checked) }
            } else Modifier)
            .padding(AppTheme.ITEM_SPACING_MINI),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.ITEM_SPACING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title.isNotEmpty()) {
            Text(
                modifier = Modifier.weight(1f, false),
                text = title,
                fontWeight = fontWeight
            )
        }
        Icon(
            imageVector = if (checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = if (checked) {
                (color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.secondary)
            } else {
                colorUnselected.takeIf { it != Color.Unspecified }
                    ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            }
        )
    }
}
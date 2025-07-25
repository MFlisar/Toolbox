package com.michaelflisar.toolbox.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.michaelflisar.toolbox.extensions.disabled

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    checked: MutableState<Boolean>,
    maxLines: Int = 1,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    MyCheckboxImpl(
        modifier = modifier,
        title = if (title.isNotEmpty()) {
            { Text(title, maxLines = maxLines) }
        } else null,
        checked = checked.value,
        color = color,
        colorUnselected = colorUnselected,
        style = style
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    checked: MutableState<Boolean>,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    MyCheckbox(
        modifier = modifier,
        title = title,
        checked = checked.value,
        color = color,
        colorUnselected = colorUnselected,
        style = style
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    maxLines: Int = 1,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    MyCheckboxImpl(
        modifier = modifier,
        title = if (title.isNotEmpty()) {
            { Text(title, maxLines = maxLines) }
        } else null,
        checked = checked,
        color = color,
        colorUnselected = colorUnselected,
        style = style,
        onCheckedChange = onCheckedChange
    )
}

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    checked: Boolean,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    MyCheckboxImpl(
        modifier = modifier,
        title = title,
        checked = checked,
        color = color,
        colorUnselected = colorUnselected,
        style = style,
        onCheckedChange = onCheckedChange
    )
}

@Composable
private fun MyCheckboxImpl(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)?,
    checked: Boolean,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    MyBaseCheckableItem(
        modifier = modifier,
        title = title,
        checked = checked,
        style = style,
        onCheckedChange = onCheckedChange
    ) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = if (checked) {
                (color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary)
            } else {
                colorUnselected.takeIf { it != Color.Unspecified }
                    ?: MaterialTheme.colorScheme.onSurface.disabled()
            }
        )
    }
}
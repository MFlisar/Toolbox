package com.michaelflisar.kmpgradletools.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.michaelflisar.kmpgradletools.disabled

@Composable
fun DemoCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    checked: MutableState<Boolean>,
    maxLines: Int = 1,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    DemoCheckboxImpl(
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
fun DemoCheckbox(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    checked: MutableState<Boolean>,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    DemoCheckbox(
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
fun DemoCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    maxLines: Int = 1,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    DemoCheckboxImpl(
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
fun DemoCheckbox(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    checked: Boolean,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    DemoCheckboxImpl(
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
private fun DemoCheckboxImpl(
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

@Composable
internal fun MyBaseCheckableItem(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)?,
    checked: Boolean,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .then(
                if (onCheckedChange != null) {
                    Modifier.clickable { onCheckedChange.invoke(!checked) }
                } else Modifier)
            .padding(8.dp)
            .width(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title != null) {
            Column(
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                CompositionLocalProvider(LocalTextStyle provides style) {
                    title()
                }
            }
        }
        content()
    }
}
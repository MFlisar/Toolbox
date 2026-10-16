package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.michaelflisar.toolbox.components
import com.michaelflisar.toolbox.extensions.disabled
import com.michaelflisar.toolbox.padding

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    checked: MutableState<Boolean>,
    enabled: Boolean = true,
    maxLines: Int = 1,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = MaterialTheme.components.checkbox.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    MyCheckboxImpl(
        modifier = modifier,
        title = if (title.isNotEmpty()) {
            { Text(title, maxLines = maxLines) }
        } else null,
        checked = checked.value,
        enabled = enabled,
        color = color,
        colorUnselected = colorUnselected,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly
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
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = MaterialTheme.components.checkbox.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    MyCheckbox(
        modifier = modifier,
        title = title,
        checked = checked.value,
        enabled = enabled,
        color = color,
        colorUnselected = colorUnselected,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly
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
    enabled: Boolean = true,
    maxLines: Int = 1,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = MaterialTheme.components.checkbox.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit  = {}
) {
    MyCheckboxImpl(
        modifier = modifier,
        title = if (title.isNotEmpty()) {
            { Text(title, maxLines = maxLines) }
        } else null,
        checked = checked,
        enabled = enabled,
        color = color,
        colorUnselected = colorUnselected,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly,
        onCheckedChange = onCheckedChange
    )
}

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    checked: Boolean,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = MaterialTheme.components.checkbox.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    MyCheckboxImpl(
        modifier = modifier,
        title = title,
        checked = checked,
        enabled = enabled,
        color = color,
        colorUnselected = colorUnselected,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly,
        onCheckedChange = onCheckedChange
    )
}

@Composable
private fun MyCheckboxImpl(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)?,
    checked: Boolean,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    colorUnselected: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = MaterialTheme.components.checkbox.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    if (toggleOnIconClickOnly) {
        MyBaseCheckableItemWrapperOnly(
            modifier = modifier,
            title = title,
            style = style
        ) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(enabled = enabled) {
                        onCheckedChange(!checked)
                    }
                    .padding(MaterialTheme.padding.small)
                    .size(MaterialTheme.components.checkbox.size)
                    ,
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
    } else {
        MyBaseCheckableItem(
            modifier = modifier,
            title = title,
            checked = checked,
            enabled = enabled,
            style = style,
            onCheckedChange = onCheckedChange
        ) {
            Icon(
                modifier = Modifier.size(MaterialTheme.components.checkbox.size),
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
}
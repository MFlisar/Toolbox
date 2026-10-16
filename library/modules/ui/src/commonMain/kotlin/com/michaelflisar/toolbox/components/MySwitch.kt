package com.michaelflisar.toolbox.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.TextStyle
import com.michaelflisar.toolbox.components

@Composable
fun MySwitch(
    checked: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = MaterialTheme.components.switch.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MySwitch(
        modifier = modifier,
        title = { Text(title, maxLines = maxLines) },
        checked = checked.value,
        enabled = enabled,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MySwitch(
    checked: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = MaterialTheme.components.switch.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MySwitch(
        modifier = modifier,
        title = title,
        checked = checked.value,
        enabled = enabled,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MySwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = MaterialTheme.components.switch.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MySwitch(
        modifier = modifier,
        title = if (title.isNotEmpty()) {
            { Text(title, maxLines = maxLines) }
        } else null,
        checked = checked,
        enabled = enabled,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly,
        onCheckedChange = onCheckedChange
    )
}

@Composable
fun MySwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = MaterialTheme.components.switch.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    if (toggleOnIconClickOnly) {
        MyBaseCheckableItemWrapperOnly(
            modifier = modifier,
            title = title,
            style = style
        ) {
            Switch(
                modifier = Modifier,
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors()
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
            Switch(
                modifier = Modifier.focusProperties {
                    canFocus = false
                },
                checked = checked,
                onCheckedChange = null,
                colors = SwitchDefaults.colors()
            )
        }
    }

}
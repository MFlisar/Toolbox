package com.michaelflisar.toolbox.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun MySwitch(
    modifier: Modifier = Modifier,
    title: String,
    checked: MutableState<Boolean>,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    MySwitch(
        modifier,
        { Text(title, maxLines = maxLines) },
        checked.value,
        style
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MySwitch(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    checked: MutableState<Boolean>,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    MySwitch(
        modifier,
        title,
        checked.value,
        style
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MySwitch(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    MySwitch(
        modifier,
        { Text(title, maxLines = maxLines) },
        checked,
        style,
        onCheckedChange
    )
}

@Composable
fun MySwitch(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    checked: Boolean,
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors()
        )
    }
}
package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.disabled

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
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .then(if (onCheckedChange != null) {
                Modifier.clickable { onCheckedChange.invoke(!checked) }
            } else Modifier)
            .padding(ToolboxDefaults.ITEM_SPACING)
            .width(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.wrapContentWidth()
        ) {
            CompositionLocalProvider(LocalTextStyle provides style) {
                title()
            }
        }
        Spacer(Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors()
        )
    }
}
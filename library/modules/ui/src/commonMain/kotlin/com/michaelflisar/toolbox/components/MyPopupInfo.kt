package com.michaelflisar.toolbox.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset

@Composable
fun MyPopupInfo(
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    expanded: MutableState<Boolean>,
    title: String? = null,
    text: String
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded.value,
        offset = offset,
        onDismissRequest = {
            expanded.value = false
        }
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun MyPopupInfo(
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    expanded: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded.value,
        offset = offset,
        onDismissRequest = {
            expanded.value = false
        }
    ) {
        content()
    }
}
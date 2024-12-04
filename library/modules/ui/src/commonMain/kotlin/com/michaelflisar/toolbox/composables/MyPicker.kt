package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
fun MyInputPicker(
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    enabled: Boolean = true,
    onStartPicker: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
    ) {
        Text(value, modifier = Modifier.weight(1f), maxLines = maxLines)
        IconButton(
            onClick = onStartPicker,
            enabled = enabled
        ) {
            icon()
        }
    }
}
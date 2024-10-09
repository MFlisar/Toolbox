package com.michaelflisar.toolbox.windowsapp.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.composables.MyInput
import com.michaelflisar.toolbox.ui.MyScrollableLazyColumn
import com.michaelflisar.toolbox.windowsapp.DesktopDialog

@Composable
fun <T> DesktopListDialog(
    title: String,
    visible: MutableState<Boolean>,
    items: List<T>,
    onFilter: (item: T, filter: String) -> Boolean,
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE,
    buttons: DesktopDialog.Buttons = DesktopDialog.Buttons.None,
    onItemSelected: ((item: T) -> Unit)? = null,
    itemRenderer: @Composable (item: T) -> Unit
) {
    val filter = remember { mutableStateOf("") }
    val filteredItems by remember(items, filter.value) {
        derivedStateOf {
            items.filter { onFilter(it, filter.value) }
        }
    }
    DesktopDialog(
        title = title,
        visible = visible,
        size = size,
        buttons = buttons
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.FilterAlt, null)
            MyInput(
                modifier = Modifier.weight(1f),
                title = "Filter",
                value = filter
            )
        }
        Text(
            modifier = Modifier.align(Alignment.End),
            text = if (filteredItems.size == items.size) items.size.toString() else "${filteredItems.size}/${items.size}"
        )
        MyScrollableLazyColumn(
            modifier = Modifier.weight(1f, false),
            itemSpacing = 0.dp
        ) {
            filteredItems.forEach { item ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(onItemSelected?.let {
                                Modifier.clickable { it.invoke(item) }
                            } ?: Modifier)
                            .minimumInteractiveComponentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemRenderer(item)
                    }
                }
            }
        }
    }
}
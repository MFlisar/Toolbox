package com.michaelflisar.toolbox.windows.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyLoading
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.ui.MyScrollableLazyColumn
import com.michaelflisar.toolbox.windows.DesktopDialog

@Composable
fun <T> DesktopListDialog(
    title: String,
    visible: MutableState<Boolean>,
    items: List<T>?,
    onFilter: (item: T, filter: String) -> Boolean,
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE,
    buttons: DesktopDialog.Buttons = DesktopDialog.Buttons.None,
    onItemSelected: ((item: T) -> Unit)? = null,
    placeholderLoading: @Composable () -> Unit = { MyLoading("Liste wird geladen...") },
    placeholderEmpty: @Composable () -> Unit = { Text(ToolboxDefaults.TEXT_EMPTY) },
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    itemRenderer: @Composable (item: T) -> Unit,
) {
    DesktopListDialog(
        title,
        visible.value,
        { visible.value = false },
        items,
        onFilter,
        size,
        buttons,
        onItemSelected,
        placeholderLoading,
        placeholderEmpty,
        header,
        footer,
        itemRenderer
    )
}

@Composable
fun <T> DesktopListDialog(
    title: String,
    visible: Boolean = true,
    onDismiss: () -> Unit,
    items: List<T>?,
    onFilter: (item: T, filter: String) -> Boolean,
    size: DpSize = ToolboxDefaults.DEFAULT_DIALOG_SIZE,
    buttons: DesktopDialog.Buttons = DesktopDialog.Buttons.None,
    onItemSelected: ((item: T) -> Unit)? = null,
    placeholderLoading: @Composable () -> Unit = { MyLoading("Liste wird geladen...") },
    placeholderEmpty: @Composable () -> Unit = { Text(ToolboxDefaults.TEXT_EMPTY) },
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    itemRenderer: @Composable (item: T) -> Unit
) {
    if (visible) {
        val filter = remember { mutableStateOf("") }
        val filteredItems by remember(items, filter.value) {
            derivedStateOf {
                items?.filter { onFilter(it, filter.value) }
            }
        }
        DesktopDialog(
            title = title,
            visible = visible,
            onDismiss = onDismiss,
            size = size,
            buttons = buttons
        ) {
            header?.invoke()
            MyRow(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterAlt, null)
                MyInput(
                    modifier = Modifier.weight(1f),
                    title = "Filter",
                    value = filter
                )
            }
            if (filteredItems != null && items != null) {
                Text(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 4.dp),
                    text = if (filteredItems!!.size == items.size) items.size.toString() else "${filteredItems!!.size}/${items.size}"
                )
            }
            MyScrollableLazyColumn(
                modifier = Modifier.weight(1f, false),
                itemSpacing = 0.dp
            ) {
                if (filteredItems == null) {
                    item("KEY-LOADING") {
                        placeholderLoading()
                    }
                } else if (items?.size == 0) {
                    item("KEY-EMPTY") {
                        placeholderEmpty()
                    }
                } else {
                    filteredItems?.forEach { item ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(onItemSelected?.let {
                                        Modifier
                                            .clip(MaterialTheme.shapes.small)
                                            .clickable { it.invoke(item) }
                                    } ?: Modifier)
                                    .padding(horizontal = LocalStyle.current.paddingSmall)
                                    .minimumInteractiveComponentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                itemRenderer(item)
                            }
                        }
                    }
                }
            }
            footer?.invoke()
        }
    }
}
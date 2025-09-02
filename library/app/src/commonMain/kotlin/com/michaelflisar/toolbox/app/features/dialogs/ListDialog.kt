package com.michaelflisar.toolbox.app.features.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogButtons
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogState
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyLoading
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.ui.MyScrollableLazyColumn

@Composable
fun <T> ListDialog(
    state: DialogState,
    title: String,
    items: List<T>?,
    onFilter: (item: T, filter: String) -> Boolean,
    buttons: DialogButtons = DialogDefaults.buttonsDisabled(),
    onItemSelected: ((item: T) -> Unit)? = null,
    placeholderLoading: @Composable () -> Unit = { MyLoading("Liste wird geladen...") },
    placeholderEmpty: @Composable () -> Unit = { Text("Empty") },
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    itemRenderer: @Composable (item: T) -> Unit
) {
    if (state.visible) {
        val filter = remember { mutableStateOf("") }
        val filteredItems by remember(items, filter.value) {
            derivedStateOf {
                items?.filter { onFilter(it, filter.value) }
            }
        }
        Dialog(
            state = state,
            title = { Text(title) },
            buttons = buttons
        ) {
            Column {
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
                    } else if (items?.isEmpty() == true) {
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
}
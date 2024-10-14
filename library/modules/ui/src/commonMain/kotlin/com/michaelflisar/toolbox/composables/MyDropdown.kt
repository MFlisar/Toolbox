package com.michaelflisar.toolbox.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.disabled

object MyDropdown {
    class Filter<T>(
        val label: String,
        val filter: (filter: String, item: T & Any) -> Boolean
    )

    internal class DropdownFilter<T>(
        val label: String,
        val filter: (filter: String, item: Item<T>) -> Boolean
    )

    internal class Item<T>(
        val item: T & Any,
        val index: Int,
        val text: String
    )
}

@Composable
fun <T> MyDropdown(
    modifier: Modifier = Modifier,
    title: String,
    items: List<T & Any>,
    selected: MutableState<T>,
    mapper: (item: T & Any) -> String,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    onSelectionChanged: ((T & Any) -> Unit)? = null
) {
    val selectedIndex = items.indexOf(selected.value)
    val dropdownItems by remember(items) {
        derivedStateOf {
            items.mapIndexed { index, item -> MyDropdown.Item(mapper(item), index, mapper(item)) }
        }
    }
    val dropdownFilter by remember(items, filter) {
        derivedStateOf {
            filter?.let { f ->
                MyDropdown.DropdownFilter(f.label) { filter: String, item: MyDropdown.Item<String> ->
                    f.filter(filter, items[item.index])
                }
            }
        }
    }
    MyDropdownImpl(
        modifier,
        title,
        dropdownItems,
        selectedIndex,
        enabled,
        color,
        backgroundColor,
        dropdownFilter
    ) { item ->
        val s = items[item.index]
        selected.value = s
        onSelectionChanged?.invoke(s)
    }
}

@Composable
fun <T> MyDropdown(
    modifier: Modifier = Modifier,
    title: String,
    items: List<T & Any>,
    selected: T,
    mapper: (item: T & Any) -> String,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    onSelectionChanged: ((T & Any) -> Unit)? = null
) {
    val selectedIndex = items.indexOf(selected)
    val dropdownItems by remember(items) {
        derivedStateOf {
            items.mapIndexed { index, item -> MyDropdown.Item(mapper(item), index, mapper(item)) }
        }
    }
    val dropdownFilter by remember(items, filter) {
        derivedStateOf {
            filter?.let { f ->
                MyDropdown.DropdownFilter(f.label) { filter: String, item: MyDropdown.Item<String> ->
                    f.filter(filter, items[item.index])
                }
            }
        }
    }
    MyDropdownImpl(
        modifier,
        title,
        dropdownItems,
        selectedIndex,
        enabled,
        color,
        backgroundColor,
        dropdownFilter
    ) { item ->
        onSelectionChanged?.invoke(items[item.index])
    }
}

// Sonderfall: Int Daten (index) + String Werte
@Composable
fun MyDropdown(
    modifier: Modifier = Modifier,
    title: String,
    items: List<String>,
    selected: MutableState<Int>,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<String>? = null,
    onSelectionChanged: ((Int) -> Unit)? = null
) {
    val selectedIndex = selected.value
    val dropdownItems by remember(items) {
        derivedStateOf {
            items.mapIndexed { index, item -> MyDropdown.Item(item, index, item) }
        }
    }
    val dropdownFilter by remember(items, filter) {
        derivedStateOf {
            filter?.let { f ->
                MyDropdown.DropdownFilter(f.label) { filter: String, item: MyDropdown.Item<String> ->
                    f.filter(filter, items[item.index])
                }
            }
        }
    }
    MyDropdownImpl(
        modifier,
        title,
        dropdownItems,
        selectedIndex,
        enabled,
        color,
        backgroundColor,
        dropdownFilter
    ) { item ->
        selected.value = item.index
        onSelectionChanged?.invoke(item.index)
    }
}

@Composable
fun MyDropdown(
    modifier: Modifier = Modifier,
    title: String,
    items: List<String>,
    selected: Int,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<String>? = null,
    onSelectionChanged: ((Int) -> Unit)? = null
) {
    val dropdownItems by remember(items) {
        derivedStateOf {
            items.mapIndexed { index, item -> MyDropdown.Item(item, index, item) }
        }
    }
    val dropdownFilter by remember(items, filter) {
        derivedStateOf {
            filter?.let { f ->
                MyDropdown.DropdownFilter(f.label) { filter: String, item: MyDropdown.Item<String> ->
                    f.filter(filter, items[item.index])
                }
            }
        }
    }
    MyDropdownImpl(modifier, title, dropdownItems, selected, enabled, color, backgroundColor, dropdownFilter) { item ->
        onSelectionChanged?.invoke(item.index)
    }
}

@Composable
private fun <T> MyDropdownImpl(
    modifier: Modifier = Modifier,
    title: String,
    items: List<MyDropdown.Item<T>>,
    selected: Int,
    enabled: Boolean,
    color: Color,
    backgroundColor: Color,
    filter: MyDropdown.DropdownFilter<T>?,
    onSelectionChange: (item: MyDropdown.Item<T>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) -180f else 0f)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val borderColor = color.takeIf { it != Color.Unspecified }?.disabled()
            ?: MaterialTheme.colorScheme.onSurface.disabled()
        val labelColor =
            color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.onSurfaceVariant

        val filterText = remember { mutableStateOf("") }
        val filteredItems = remember(items) { mutableStateOf(items) }

        if (filter != null) {
            LaunchedEffect(filterText.value, filteredItems.value, expanded) {
                if (expanded) {
                    filteredItems.value = items.filter {
                        filter.filter(filterText.value, it)
                    }
                }
            }
            LaunchedEffect(expanded) {
                if (!expanded) {
                    filterText.value = ""
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .border(1.dp, borderColor, MaterialTheme.shapes.small)
                .background(backgroundColor)
                .then(
                    if (enabled) {
                        Modifier.clickable {
                            expanded = !expanded
                        }
                    } else Modifier
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val style1 =
                MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * .8f)
            val style2 = MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize)
            Column(modifier = Modifier.weight(1f)) {
                if (title.isNotEmpty()) {
                    Text(text = title, style = style1, fontWeight = FontWeight.Bold, color = labelColor)
                    //Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = items.find { it.index == selected }?.text ?: "",
                    style = style2,
                    color = color
                )
            }

            Icon(
                modifier = Modifier.rotate(rotation),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = labelColor
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            if (filter != null) {
                OutlinedTextField(
                    value = filterText.value,
                    label = { Text(filter.label) },
                    singleLine = true,
                    onValueChange = { filterText.value = it },
                    modifier = Modifier.fillMaxWidth().padding(all = 8.dp)
                )
            }
            filteredItems.value.forEach {
                DropdownMenuItem(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = {
                        Text(
                            text = it.text,
                            color = if (it.index == selected) MaterialTheme.colorScheme.primary else Color.Unspecified,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        onSelectionChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
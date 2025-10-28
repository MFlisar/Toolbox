package com.michaelflisar.toolbox.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.michaelflisar.toolbox.LocalTheme
import com.michaelflisar.toolbox.extensions.disabled

@Composable
fun <T> MyMultiDropdown(
    items: List<T>,
    selected: MutableState<List<T>>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    onSelectionChange: ((List<T>) -> Unit)? = null
) {
    val texts = items.map { mapper(it) }
    val selectedIndices = selected.value.map { items.indexOf(it) }
    MyMultiDropdownImpl(
        modifier,
        title,
        texts,
        selectedIndices,
        enabled,
        color,
        backgroundColor
    ) { selectedIndices, selectedItems ->
        selected.value = selectedIndices.map { items[it] }
        onSelectionChange?.invoke(selected.value)
    }
}

@Composable
fun <T> MyMultiDropdown(
    items: List<T>,
    selected: List<T>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    onSelectionChange: (List<T>) -> Unit
) {
    val texts = items.map { mapper(it) }
    val selectedIndices = selected.map { items.indexOf(it) }
    MyMultiDropdownImpl(
        modifier,
        title,
        texts,
        selectedIndices,
        enabled,
        color,
        backgroundColor
    ) { selectedIndices, selectedItems ->
        onSelectionChange(selectedIndices.map { items[it] })
    }
}

@Composable
fun <T> MyMultiDropdownIndex(
    items: List<T>,
    selectedIndices: MutableState<List<Int>>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    onSelectionChange: ((List<Int>) -> Unit)? = null
) {
    val texts = items.map { mapper(it) }
    MyMultiDropdownImpl(
        modifier,
        title,
        texts,
        selectedIndices.value,
        enabled,
        color,
        backgroundColor
    ) { selectedIndices2, selectedItems ->
        selectedIndices.value = selectedIndices2
        onSelectionChange?.invoke(selectedIndices2)
    }
}

@Composable
fun <T> MyMultiDropdownIndex(
    items: List<T>,
    selectedIndices: List<Int>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    onSelectionChange: (List<Int>) -> Unit
) {
    val texts = items.map { mapper(it) }
    MyMultiDropdownImpl(
        modifier,
        title,
        texts,
        selectedIndices,
        enabled,
        color,
        backgroundColor
    ) { selectedIndices, selectedItems ->
        onSelectionChange(selectedIndices)
    }
}

// --------------------------------
// Implementation
// --------------------------------

@Composable
private fun MyMultiDropdownImpl(
    modifier: Modifier = Modifier,
    title: String,
    items: List<String>,
    selected: List<Int>,
    enabled: Boolean,
    color: Color,
    backgroundColor: Color,
    onSelectionChange: (indices: List<Int>, items: List<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) -180f else 0f)
    val selectedItems = selected.map { items[it] }
    //val empty = remember(selectedItems) { derivedStateOf { selectedItems.isEmpty() } }
    Box(
        modifier = modifier//.padding(horizontal = 16.dp)
    ) {
        val borderColor = color.takeIf { it != Color.Unspecified }?.disabled()
            ?: MaterialTheme.colorScheme.onSurface.disabled()
        val labelColor =
            color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.onSurfaceVariant
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalTheme.current.spacing.default)
        ) {
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
                    .padding(LocalTheme.current.padding.default)
                //.padding(horizontal = 16.dp, vertical = 8.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val style1 =
                    MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * .8f)
                val style2 =
                    MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize)
                Column(modifier = Modifier.weight(1f)) {
                    if (title.isNotEmpty()) {
                        Text(
                            text = title,
                            style = style1,
                            fontWeight = FontWeight.Bold,
                            color = labelColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(text = selectedItems.joinToString(";"), style = style2, color = color)
                }

                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = labelColor
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            MyRow(
                modifier = Modifier.align(Alignment.End)
            ) {
                MyTooltipBox(
                    tooltip = "Select All",
                    enabled = selectedItems.size != items.size
                ) {
                    MyIconButton(
                        icon = Icons.Default.SelectAll,
                        enabled = selectedItems.size != items.size
                    ) {
                        onSelectionChange(List(items.size) { index -> index }, items)
                    }
                }
                MyTooltipBox(
                    tooltip = "Clear Selection",
                    enabled = selectedItems.isNotEmpty()
                ) {
                    MyIconButton(
                        icon = Icons.Default.Clear,
                        enabled = selectedItems.isNotEmpty()
                    ) {
                        onSelectionChange(emptyList(), emptyList())
                    }
                }
            }
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = {
                        Row {
                            val selected = selectedItems.contains(item)
                            val color = if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            Icon(
                                modifier = Modifier.align(Alignment.CenterVertically)
                                    .padding(end = LocalTheme.current.spacing.default),
                                imageVector = if (selected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = null,
                                tint = color
                            )
                            Text(
                                text = item,
                                color = color,
                                modifier = Modifier.align(Alignment.CenterVertically).fillMaxWidth()
                            )
                        }
                    },
                    onClick = {
                        val selectedNew = selected.toMutableList()
                        val selectedItemsNew = selectedItems.toMutableList()
                        if (selected.contains(index)) {
                            selectedNew.remove(index)
                            selectedItemsNew.remove(item)
                        } else {
                            selectedNew.add(index)
                            selectedItemsNew.add(item)
                        }
                        onSelectionChange(selectedNew, selectedItemsNew)
                        //expanded = false
                    }
                )
            }
        }
    }
}
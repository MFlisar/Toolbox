package com.michaelflisar.toolbox.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

import com.michaelflisar.toolbox.extensions.disabled
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing

object MyMultiDropdown {

    @Composable
    fun shape() = OutlinedTextFieldDefaults.shape

    val formatter: @Composable (selected: List<String>) -> String = { selected ->
        if (selected.isEmpty()) {
            ""
        } else {
            selected.joinToString(";")
        }
    }
}

object MyMultiDropdownDefaults {
    const val sortSelectionByItems = true
}

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
    shape: Shape = MyMultiDropdown.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    formatter: @Composable (selected: List<String>) -> String = MyMultiDropdown.formatter,
    sortSelectionByItems: Boolean = MyMultiDropdownDefaults.sortSelectionByItems,
    maxLines: Int= 1,
    onSelectionChange: ((List<T>) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    val selectedIndices = selected.value.map { items.indexOf(it) }
    MyMultiDropdownImpl(
        modifier = modifier,
        title = title,
        items = texts,
        selected = selectedIndices,
        enabled = enabled,
        maxLines = maxLines,
        color = color,
        backgroundColor = backgroundColor,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        formatter = formatter,
        sortSelectionByItems = sortSelectionByItems,
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
    shape: Shape = MyMultiDropdown.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    formatter: @Composable (selected: List<String>) -> String = MyMultiDropdown.formatter,
    sortSelectionByItems: Boolean = MyMultiDropdownDefaults.sortSelectionByItems,
    maxLines: Int= 1,
    onSelectionChange: (List<T>) -> Unit,
) {
    val texts = items.map { mapper(it) }
    val selectedIndices = selected.map { items.indexOf(it) }
    MyMultiDropdownImpl(
        modifier = modifier,
        title = title,
        items = texts,
        selected = selectedIndices,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        formatter = formatter,
        sortSelectionByItems = sortSelectionByItems,
        maxLines = maxLines
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
    shape: Shape = MyMultiDropdown.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    formatter: @Composable (selected: List<String>) -> String = MyMultiDropdown.formatter,
    sortSelectionByItems: Boolean = MyMultiDropdownDefaults.sortSelectionByItems,
    maxLines: Int= 1,
    onSelectionChange: ((List<Int>) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    MyMultiDropdownImpl(
        modifier = modifier,
        title = title,
        items = texts,
        selected = selectedIndices.value,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        formatter = formatter,
        sortSelectionByItems = sortSelectionByItems,
        maxLines = maxLines
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
    shape: Shape = MyMultiDropdown.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    formatter: @Composable (selected: List<String>) -> String = MyMultiDropdown.formatter,
    sortSelectionByItems: Boolean = MyMultiDropdownDefaults.sortSelectionByItems,
    maxLines: Int= 1,
    onSelectionChange: (List<Int>) -> Unit,
) {
    val texts = items.map { mapper(it) }
    MyMultiDropdownImpl(
        modifier = modifier,
        title = title,
        items = texts,
        selected = selectedIndices,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        formatter = formatter,
        sortSelectionByItems = sortSelectionByItems,
        maxLines = maxLines
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
    shape: Shape = MyMultiDropdown.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    formatter: @Composable (selected: List<String>) -> String = MyMultiDropdown.formatter,
    sortSelectionByItems: Boolean = MyMultiDropdownDefaults.sortSelectionByItems,
    maxLines: Int= 1,
    onSelectionChange: (indices: List<Int>, items: List<String>) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) -180f else 0f)
    val selectedIndices = if (sortSelectionByItems) selected.sorted() else selected
    val selectedItems = selectedIndices.map { items[it] }
    val notifySelectionChange: (List<Int>) -> Unit = { indices ->
        val effectiveIndices = if (sortSelectionByItems) indices.sorted() else indices
        onSelectionChange(effectiveIndices, effectiveIndices.map { items[it] })
    }
    //val empty = remember(selectedItems) { derivedStateOf { selectedItems.isEmpty() } }
    val borderColor = color.takeIf { it != Color.Unspecified }?.disabled()
        ?: MaterialTheme.colorScheme.onSurface.disabled()
    val labelColor =
        color.takeIf { it != Color.Unspecified }
            ?: MaterialTheme.colorScheme.onSurfaceVariant

    MyDropdown.DropdownContainer(
        modifier = modifier,
        title = "",
        enabled = enabled,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
        shape = shape,
        style = MyDropdown.Style.Button(),
        onToggle = { expanded = !expanded },
        content = { contentModifier ->
            Row(
                modifier = contentModifier
                    .fillMaxWidth()
                    .padding(MyDropdown.Style.Button().padding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MyDropdownDefaults.Content(
                    modifier = Modifier.weight(1f),
                    title = title,
                    text = formatter(selectedItems),
                    labelColor = labelColor,
                    color = color,
                    maxLines = maxLines,
                )
                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = labelColor
                )
            }
        },
        popupContent = {
            MyDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                widthConfig = popupWidthConfig,
            ) {
                MyRow(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    MyTooltipBox(
                        tooltip = "Select All",
                        enabled = selectedItems.size != items.size
                    ) {
                        MyIconButton(
                            icon = Icons.Default.SelectAll.toIconComposable(),
                            enabled = selectedItems.size != items.size
                        ) {
                            notifySelectionChange(List(items.size) { index -> index })
                        }
                    }
                    MyTooltipBox(
                        tooltip = "Clear Selection",
                        enabled = selectedItems.isNotEmpty()
                    ) {
                        MyIconButton(
                            icon = Icons.Default.Clear.toIconComposable(),
                            enabled = selectedItems.isNotEmpty()
                        ) {
                            notifySelectionChange(emptyList())
                        }
                    }
                }
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = {
                            Row {
                                val selected = selectedIndices.contains(index)
                                val color =
                                    if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                Icon(
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                        .padding(end = MaterialTheme.spacing.default),
                                    imageVector = if (selected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                    contentDescription = null,
                                    tint = color
                                )
                                Text(
                                    text = item,
                                    color = color,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                        .fillMaxWidth()
                                )
                            }
                        },
                        onClick = {
                            val selectedNew = selected.toMutableList()
                            if (selected.contains(index)) {
                                selectedNew.remove(index)
                            } else {
                                selectedNew.add(index)
                            }
                            notifySelectionChange(selectedNew)
                            //expanded = false
                        }
                    )
                }
            }
        },
    )
}
package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.michaelflisar.toolbox.LocalComponentTexts
import com.michaelflisar.toolbox.components
import com.michaelflisar.toolbox.components.MyDropdown.Style

import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.spacing

object MyDropdownMultiSelectDefaults {

    @Composable
    fun shape() = OutlinedTextFieldDefaults.shape

    val formatter: @Composable (selected: List<String>) -> String = { selected ->
        if (selected.isEmpty()) {
            ""
        } else {
            selected.joinToString(";")
        }
    }

    const val sortSelectionByItems = true
}

@Composable
fun <T> MyDropdownMultiSelect(
    items: List<T>,
    selected: MutableState<List<T>>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = MyDropdownMultiSelectDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    formatter: @Composable (selected: List<String>) -> String = MyDropdownMultiSelectDefaults.formatter,
    sortSelectionByItems: Boolean = MyDropdownMultiSelectDefaults.sortSelectionByItems,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    maxLines: Int= 1,
    onSelectionChange: ((List<T>) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    val selectedIndices = selected.value.map { items.indexOf(it) }
    MyDropdownMultiSelectImpl(
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
        style = style
    ) { selectedIndices, selectedItems ->
        selected.value = selectedIndices.map { items[it] }
        onSelectionChange?.invoke(selected.value)
    }
}

@Composable
fun <T> MyDropdownMultiSelect(
    items: List<T>,
    selected: List<T>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = MyDropdownMultiSelectDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    formatter: @Composable (selected: List<String>) -> String = MyDropdownMultiSelectDefaults.formatter,
    sortSelectionByItems: Boolean = MyDropdownMultiSelectDefaults.sortSelectionByItems,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    maxLines: Int= 1,
    onSelectionChange: (List<T>) -> Unit,
) {
    val texts = items.map { mapper(it) }
    val selectedIndices = selected.map { items.indexOf(it) }
    MyDropdownMultiSelectImpl(
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
        style = style,
        maxLines = maxLines
    ) { selectedIndices, selectedItems ->
        onSelectionChange(selectedIndices.map { items[it] })
    }
}

@Composable
fun <T> MyDropdownMultiSelectIndex(
    items: List<T>,
    selectedIndices: MutableState<List<Int>>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = MyDropdownMultiSelectDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    formatter: @Composable (selected: List<String>) -> String = MyDropdownMultiSelectDefaults.formatter,
    sortSelectionByItems: Boolean = MyDropdownMultiSelectDefaults.sortSelectionByItems,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    maxLines: Int= 1,
    onSelectionChange: ((List<Int>) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    MyDropdownMultiSelectImpl(
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
        style = style,
        maxLines = maxLines
    ) { selectedIndices2, selectedItems ->
        selectedIndices.value = selectedIndices2
        onSelectionChange?.invoke(selectedIndices2)
    }
}

@Composable
fun <T> MyDropdownMultiSelectIndex(
    items: List<T>,
    selectedIndices: List<Int>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = MyDropdownMultiSelectDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    formatter: @Composable (selected: List<String>) -> String = MyDropdownMultiSelectDefaults.formatter,
    sortSelectionByItems: Boolean = MyDropdownMultiSelectDefaults.sortSelectionByItems,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    maxLines: Int= 1,
    onSelectionChange: (List<Int>) -> Unit,
) {
    val texts = items.map { mapper(it) }
    MyDropdownMultiSelectImpl(
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
        style = style,
        maxLines = maxLines
    ) { selectedIndices, selectedItems ->
        onSelectionChange(selectedIndices)
    }
}

// --------------------------------
// Implementation
// --------------------------------

@Composable
private fun MyDropdownMultiSelectImpl(
    modifier: Modifier,
    title: String,
    items: List<String>,
    selected: List<Int>,
    enabled: Boolean,
    color: Color,
    backgroundColor: Color,
    shape: Shape,
    popupWidthConfig: MyDropdownMenu.WidthConfig,
    formatter: @Composable (selected: List<String>) -> String,
    sortSelectionByItems: Boolean,
    style: Style,
    maxLines: Int= 1,
    onSelectionChange: (indices: List<Int>, items: List<String>) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedIndices = if (sortSelectionByItems) selected.sorted() else selected
    val selectedItems = selectedIndices.map { items[it] }
    val notifySelectionChange: (List<Int>) -> Unit = { indices ->
        val effectiveIndices = if (sortSelectionByItems) indices.sorted() else indices
        onSelectionChange(effectiveIndices, effectiveIndices.map { items[it] })
    }
    val borderColor = color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.outline

    val internalTitle = if (style is Style.OutlinedButton) "" else title

    MyDropdown.DropdownContainer(
        modifier = modifier,
        title = title,
        enabled = enabled,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
        shape = shape,
        style = style,
        onToggle = { expanded.value = !expanded.value },
        content = { contentModifier ->
            MyDropdown.DropdownContent(
                modifier = contentModifier,
                style = style,
                expanded = expanded,
                color = color
            ) { contentModifier, labelColor ->
                MyDropdownDefaults.Content(
                    modifier = contentModifier,
                    title = internalTitle,
                    text = formatter(selectedItems),
                    labelColor = labelColor,
                    color = color,
                    maxLines = maxLines,
                )
            }
        },
        popupContent = {
            MyDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = {
                    expanded.value = false
                },
                widthConfig = popupWidthConfig,
            ) {
                MyRow(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    MyTooltipBox(
                        tooltip = LocalComponentTexts.current.selectAll,
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
                        tooltip = LocalComponentTexts.current.clearSelection,
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
                    MyDropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = {
                            MyRow(
                                itemSpacing = MaterialTheme.spacing.default
                            ) {
                                val selected = selectedIndices.contains(index)
                                val color =
                                    if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                MyIcon(
                                    modifier = Modifier,
                                    imageVector = if (selected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                    contentDescription = null,
                                    size = MaterialTheme.components.checkbox.size,
                                    tint = color
                                )
                                Text(
                                    text = item,
                                    color = color,
                                    modifier = Modifier.fillMaxWidth()
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
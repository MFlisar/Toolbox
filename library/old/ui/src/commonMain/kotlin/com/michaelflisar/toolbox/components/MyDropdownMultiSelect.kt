package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import com.michaelflisar.toolbox.components.MyDropdown.RenderContext
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.theme.LocalComponentOptions
import com.michaelflisar.toolbox.theme.LocalComponentTexts
import com.michaelflisar.toolbox.theme.icon

object MyDropdownMultiSelectDefaults {

    @Composable
    fun <T : Any> defaultRenderer(
        mapper: @Composable (item: T) -> String = { it.toString() },
        formatter: @Composable (selected: List<String>) -> String = MyDropdownMultiSelectDefaults.formatter,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        maxLines: Int = 1,
        leadingContent: @Composable () -> Unit = {},
        dropdownMapper: @Composable (item: T) -> String = mapper,
        colorDropdown: Color = Color.Unspecified,
        colorDropdownSelected: Color = MaterialTheme.colorScheme.primary,
        fontWeightDropdown: FontWeight? = null,
        fontWeightDropdownSelected: FontWeight? = FontWeight.Bold,
        maxLinesDropdown: Int = 1,
    ) = MyDropdown.RenderContent(
        content = defaultContent(
            mapper = mapper,
            formatter = formatter,
            color = color,
            fontWeight = fontWeight,
            maxLines = maxLines,
            leadingContent = leadingContent
        ),
        dropdownContent = defaultDropdownContent(
            mapper = dropdownMapper,
            color = colorDropdown,
            colorSelected = colorDropdownSelected,
            fontWeight = fontWeightDropdown,
            fontWeightSelected = fontWeightDropdownSelected,
            maxLines = maxLinesDropdown,
            leadingContent = leadingContent
        )
    )

    @Composable
    fun <T : Any> defaultRenderer(
        content: @Composable (context: RenderContext.Selection<List<T>>) -> Unit = defaultContent(),
        dropdownContent: @Composable (context: RenderContext.DropdownItem<T>) -> Unit = defaultDropdownContent(),
    ) = MyDropdown.RenderContent(
        content = content,
        dropdownContent = dropdownContent
    )

    @Composable
    fun <T> defaultContent(
        mapper: @Composable (item: T) -> String = { it.toString() },
        formatter: @Composable (selected: List<String>) -> String = MyDropdownMultiSelectDefaults.formatter,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        maxLines: Int = 1,
        leadingContent: @Composable () -> Unit = {},
    ): @Composable (RenderContext.Selection<List<T>>) -> Unit = { context ->
        MyRow {
            leadingContent()
            Text(
                text = formatter(context.selection.map { mapper(it) }),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                fontWeight = fontWeight,
                maxLines = maxLines
            )
        }
    }

    @Composable
    fun <T : Any> defaultDropdownContent(
        mapper: @Composable (item: T) -> String = { it.toString() },
        color: Color = Color.Unspecified,
        colorSelected: Color = MaterialTheme.colorScheme.primary,
        fontWeight: FontWeight? = null,
        fontWeightSelected: FontWeight? = FontWeight.Bold,
        maxLines: Int = 1,
        leadingContent: @Composable () -> Unit = {},
    ): @Composable (RenderContext.DropdownItem<T>) -> Unit = { context ->
        MyRow(modifier = Modifier.fillMaxWidth()) {
            MyIcon(
                imageVector = if (context.selected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                size = MaterialTheme.icon.size,
                tint = if (context.selected) colorSelected else color
            )
            leadingContent()
            Text(
                text = mapper(context.item),
                color = if (context.selected) colorSelected else color,
                modifier = Modifier.weight(1f),
                fontWeight = if (context.selected) fontWeightSelected else fontWeight,
                maxLines = maxLines
            )
        }
    }

    @Composable
    fun shape() = LocalComponentOptions.current.textField.outlinedShape

    val formatter: @Composable (selected: List<String>) -> String = { selected ->
        if (selected.isEmpty()) {
            ""
        } else {
            selected.joinToString(";")
        }
    }

    const val sortSelectionByItems = true
}

/**
 * Updates [selected] before notifying [onSelectionChange] with original list indices and items.
 */
@Composable
fun <T : Any> MyDropdownMultiSelect(
    items: List<T>,
    selected: MutableState<List<T>>,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    shape: Shape = MyDropdownMultiSelectDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = LocalComponentOptions.current.dropdown.widthConfig,
    sortSelectionByItems: Boolean = MyDropdownMultiSelectDefaults.sortSelectionByItems,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    content: @Composable (context: MyDropdown.RenderContext<T, List<T>>) -> Unit = MyDropdownMultiSelectDefaults.defaultRenderer(),
    onSelectionChange: ((indices: List<Int>, items: List<T>) -> Unit)? = null,
) {
    val selectedIndices = selected.value.map { items.indexOf(it) }
    MyDropdownMultiSelectImpl(
        modifier = modifier,
        title = title,
        items = items,
        selected = selectedIndices,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        sortSelectionByItems = sortSelectionByItems,
        style = style
    ) { selectedIndices, selectedItems ->
        selected.value = selectedItems
        onSelectionChange?.invoke(selectedIndices, selectedItems)
    }
}

/**
 * Renders selected items through [MyDropdown.RenderContext.Selection] and menu entries through
 * [MyDropdown.RenderContext.DropdownItem]. Item clicks toggle selection without closing the menu.
 */
@Composable
fun <T : Any> MyDropdownMultiSelect(
    items: List<T>,
    selected: List<T>,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    shape: Shape = MyDropdownMultiSelectDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = LocalComponentOptions.current.dropdown.widthConfig,
    sortSelectionByItems: Boolean = MyDropdownMultiSelectDefaults.sortSelectionByItems,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    content: @Composable (context: MyDropdown.RenderContext<T, List<T>>) -> Unit = MyDropdownMultiSelectDefaults.defaultRenderer(),
    onSelectionChange: (indices: List<Int>, items: List<T>) -> Unit,
) {
    val selectedIndices = selected.map { items.indexOf(it) }
    MyDropdownMultiSelectImpl(
        modifier = modifier,
        title = title,
        items = items,
        selected = selectedIndices,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        sortSelectionByItems = sortSelectionByItems,
        style = style,
        onSelectionChange = onSelectionChange
    )
}

/**
 * Updates [selectedIndices] before notifying [onSelectionChange] with original list indices and items.
 */
@Composable
fun <T : Any> MyDropdownMultiSelectIndex(
    items: List<T>,
    selectedIndices: MutableState<List<Int>>,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    shape: Shape = MyDropdownMultiSelectDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = LocalComponentOptions.current.dropdown.widthConfig,
    sortSelectionByItems: Boolean = MyDropdownMultiSelectDefaults.sortSelectionByItems,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    content: @Composable (context: MyDropdown.RenderContext<T, List<T>>) -> Unit = MyDropdownMultiSelectDefaults.defaultRenderer(),
    onSelectionChange: ((indices: List<Int>, items: List<T>) -> Unit)? = null,
) {
    MyDropdownMultiSelectImpl(
        modifier = modifier,
        title = title,
        items = items,
        selected = selectedIndices.value,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        sortSelectionByItems = sortSelectionByItems,
        style = style
    ) { selectedIndices2, selectedItems ->
        selectedIndices.value = selectedIndices2
        onSelectionChange?.invoke(selectedIndices2, selectedItems)
    }
}

/**
 * [selectedIndices] must reference entries in [items].
 * [sortSelectionByItems] orders the rendered selection and callback results by original list index.
 * [filter] affects menu entries only; select-all and clear operate on the entire selection.
 */
@Composable
fun <T : Any> MyDropdownMultiSelectIndex(
    items: List<T>,
    selectedIndices: List<Int>,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    shape: Shape = MyDropdownMultiSelectDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = LocalComponentOptions.current.dropdown.widthConfig,
    sortSelectionByItems: Boolean = MyDropdownMultiSelectDefaults.sortSelectionByItems,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    content: @Composable (context: MyDropdown.RenderContext<T, List<T>>) -> Unit = MyDropdownMultiSelectDefaults.defaultRenderer(),
    onSelectionChange: (indices: List<Int>, items: List<T>) -> Unit,
) {
    MyDropdownMultiSelectImpl(
        modifier = modifier,
        title = title,
        items = items,
        selected = selectedIndices,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        sortSelectionByItems = sortSelectionByItems,
        style = style,
        onSelectionChange = onSelectionChange
    )
}

// --------------------------------
// Implementation
// --------------------------------

@Composable
private fun <T : Any> MyDropdownMultiSelectImpl(
    modifier: Modifier,
    title: String,
    items: List<T>,
    selected: List<Int>,
    enabled: Boolean,
    color: Color,
    backgroundColor: Color,
    filter: MyDropdown.Filter<T>?,
    shape: Shape,
    popupWidthConfig: MyDropdownMenu.WidthConfig,
    content: @Composable (context: MyDropdown.RenderContext<T, List<T>>) -> Unit,
    sortSelectionByItems: Boolean,
    style: MyDropdown.Style,
    onSelectionChange: (indices: List<Int>, items: List<T>) -> Unit,
) {
    require(selected.all { it in items.indices }) {
        "Selected indices must refer to entries in items"
    }
    val selectedIndices = if (sortSelectionByItems) selected.sorted() else selected
    val selectedItems = selectedIndices.map { items[it] }
    val notifySelectionChange: (List<Int>) -> Unit = { indices ->
        val effectiveIndices = if (sortSelectionByItems) indices.sorted() else indices
        onSelectionChange(effectiveIndices, effectiveIndices.map { items[it] })
    }
    MyDropdownBase(
        modifier = modifier,
        title = title,
        items = items,
        selection = selectedItems,
        isSelected = { it in selectedIndices },
        closeOnItemClick = false,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        shape = shape,
        style = style,
        popupWidthConfig = popupWidthConfig,
        content = content,
        popupHeader = {
            MyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
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
        },
        onItemClick = { index, _ ->
            val selectedNew = selected.toMutableList()
            if (index in selectedNew) selectedNew.remove(index) else selectedNew.add(index)
            notifySelectionChange(selectedNew)
        }
    )
}
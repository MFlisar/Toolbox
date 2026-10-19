package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import com.michaelflisar.toolbox.components.MyDropdown.RenderContext
import com.michaelflisar.toolbox.theme.LocalComponentOptions

object MyDropdownDefaults {

    @Composable
    fun <T : Any> defaultRenderer(
        mapper: @Composable (item: T) -> String = { it.toString() },
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        maxLines: Int = 1,
        leadingContent: @Composable () -> Unit = {},
        mapperDropdown: @Composable (item: T) -> String = mapper,
        colorDropdown: Color = Color.Unspecified,
        colorDropdownSelected: Color = MaterialTheme.colorScheme.primary,
        fontWeightDropdown: FontWeight? = null,
        fontWeightDropdownSelected: FontWeight? = FontWeight.Bold,
        maxLinesDropdown: Int = 1,
    ) = MyDropdown.RenderContent(
        content = defaultContent(
            mapper = mapper,
            color = color,
            fontWeight = fontWeight,
            maxLines = maxLines,
            leadingContent = leadingContent
        ),
        dropdownContent = defaultDropdownContent(
            mapper = mapperDropdown,
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
        content: @Composable (context: RenderContext.Selection<T?>) -> Unit = defaultContent(),
        dropdownContent: @Composable (context: RenderContext.DropdownItem<T>) -> Unit = defaultDropdownContent(),
    ) = MyDropdown.RenderContent(
        content = content,
        dropdownContent = dropdownContent
    )

    @Composable
    fun <T> defaultContent(
        mapper: @Composable (item: T) -> String = { it.toString() },
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        maxLines: Int = 1,
        leadingContent: @Composable () -> Unit = {},
    ): @Composable (RenderContext.Selection<T?>) -> Unit = { context ->
        MyRow {
            leadingContent()
            Text(
                text = if (context.selection != null) mapper(context.selection) else "",
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
            leadingContent()
            Text(
                text = mapper(context.item),
                color = if (context.selected) colorSelected else color,
                modifier = Modifier.weight(1f),
                fontWeight = if (context.selected) fontWeightSelected else fontWeight,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = maxLines
            )
        }
    }

    @Composable
    fun styleButton(): MyDropdown.Style = MyDropdown.Style.Button

    @Composable
    fun styleOutlinedButton(): MyDropdown.Style = MyDropdown.Style.OutlinedButton

}

/**
 * Updates [selected] before notifying [onSelectionChange] with the original list index and item.
 */
@Composable
fun <T : Any> MyDropdown(
    items: List<T>,
    selected: MutableState<T>,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    shape: Shape = LocalComponentOptions.current.textField.outlinedShape,
    popupWidthConfig: MyDropdownMenu.WidthConfig = LocalComponentOptions.current.dropdown.widthConfig,
    content: @Composable (context: MyDropdown.RenderContext<T, T?>) -> Unit = MyDropdownDefaults.defaultRenderer(),
    onSelectionChange: ((index: Int, item: T) -> Unit)? = null,
) {
    val selectedIndex = items.indexOf(selected.value)
    MyDropdownIndex(
        items = items,
        selectedIndex = selectedIndex,
        modifier = modifier,
        title = title,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        style = style,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        onSelectionChange = { index, item ->
            selected.value = item
            onSelectionChange?.invoke(index, item)
        }
    )
}

/**
 * Selects the first item equal to [selected], or displays no selection if it is absent.
 * [onSelectionChange] receives the original list index and item.
 */
@Composable
fun <T : Any> MyDropdown(
    items: List<T>,
    selected: T,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    shape: Shape = LocalComponentOptions.current.textField.outlinedShape,
    popupWidthConfig: MyDropdownMenu.WidthConfig = LocalComponentOptions.current.dropdown.widthConfig,
    content: @Composable (context: MyDropdown.RenderContext<T, T?>) -> Unit = MyDropdownDefaults.defaultRenderer(),
    onSelectionChange: (index: Int, item: T) -> Unit,
) {
    val selectedIndex = items.indexOf(selected)
    MyDropdownIndex(
        items = items,
        selectedIndex = selectedIndex,
        modifier = modifier,
        title = title,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        style = style,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        onSelectionChange = onSelectionChange
    )
}

/**
 * Updates [selectedIndex] before notifying [onSelectionChange] with the original list index and item.
 */
@Composable
fun <T : Any> MyDropdownIndex(
    items: List<T>,
    selectedIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    shape: Shape = LocalComponentOptions.current.textField.outlinedShape,
    popupWidthConfig: MyDropdownMenu.WidthConfig = LocalComponentOptions.current.dropdown.widthConfig,
    content: @Composable (context: MyDropdown.RenderContext<T, T?>) -> Unit = MyDropdownDefaults.defaultRenderer(),
    onSelectionChange: ((index: Int, item: T) -> Unit)? = null,
) {
    MyDropdownIndex(
        items = items,
        selectedIndex = selectedIndex.value,
        modifier = modifier,
        title = title,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        style = style,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        onSelectionChange = { index, item ->
            selectedIndex.value = index
            onSelectionChange?.invoke(index, item)
        }
    )
}

/**
 * Displays a selection from [items]. [onSelectionChange] receives the index in the
 * original [items] list (even when filtered) and the selected item.
 * An out-of-range [selectedIndex] displays no selection.
 * [content] receives a [MyDropdown.RenderContext.Selection] for the closed selection or
 * a [MyDropdown.RenderContext.DropdownItem] for each menu entry.
 * The container handles the title and layout. Customize text mapping through
 * [MyDropdownDefaults.defaultRenderer], or render each context separately.
 */
@Composable
fun <T : Any> MyDropdownIndex(
    items: List<T>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    shape: Shape = LocalComponentOptions.current.textField.outlinedShape,
    popupWidthConfig: MyDropdownMenu.WidthConfig = LocalComponentOptions.current.dropdown.widthConfig,
    content: @Composable (context: MyDropdown.RenderContext<T, T?>) -> Unit = MyDropdownDefaults.defaultRenderer(),
    onSelectionChange: (index: Int, item: T) -> Unit,
) {
    MyDropdownBase(
        modifier = modifier,
        title = title,
        items = items,
        selection = items.getOrNull(selectedIndex),
        isSelected = { it == selectedIndex },
        closeOnItemClick = true,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        style = style,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        onItemClick = onSelectionChange
    )
}
package com.michaelflisar.toolbox.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.michaelflisar.toolbox.Components
import com.michaelflisar.toolbox.components
import com.michaelflisar.toolbox.components.MyDropdown.Style
import com.michaelflisar.toolbox.extensions.toSentenceCase
import com.michaelflisar.toolbox.interfaces.ILabel
import com.michaelflisar.toolbox.interfaces.ILabelAndShortLabel
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing

object MyDropdownDefaults {

    @Composable
    fun Content(
        modifier: Modifier,
        title: String,
        text: String,
        labelColor: Color,
        color: Color,
        maxLines: Int,
        leadingContent: @Composable () -> Unit = {},
    ) {
        val styleTitle = MyDropdownDefaults.styleTitle
        val styleText = MyDropdownDefaults.styleText

        MyRow(modifier = modifier) {
            leadingContent()
            Column(modifier = Modifier.weight(1f)) {
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        style = styleTitle,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                        maxLines = 1
                    )
                }
                Text(
                    text = text,
                    style = styleText,
                    color = color,
                    maxLines = maxLines
                )
            }
        }

    }

    @Composable
    fun DropdownContent(
        text: String,
        selected: Boolean,
        leadingContent: @Composable () -> Unit = {},
    ) {
        MyRow(modifier = Modifier.fillMaxWidth()) {
            leadingContent()
            Text(
                text = text,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Unspecified,
                modifier = Modifier.weight(1f)
            )
        }
    }

    @Composable
    fun styleButton(): Style = Style.Button

    @Composable
    fun styleOutlinedButton(): Style = Style.OutlinedButton

    @Composable
    fun shape() = MaterialTheme.components.textField.outlinedShape

    internal val styleTitle: TextStyle
        @Composable get() = LocalTextStyle.current.copy(fontSize = LocalTextStyle.current.fontSize * .75f)//MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * .8f)

    internal val styleText: TextStyle
        @Composable get() = LocalTextStyle.current//MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize)


}

object MyDropdown {

    @Composable
    internal fun DropdownContainer(
        modifier: Modifier,
        title: String,
        enabled: Boolean,
        borderColor: Color,
        backgroundColor: Color,
        shape: Shape,
        style: MyDropdown.Style,
        onToggle: () -> Unit,
        content: @Composable (Modifier) -> Unit,
        popupContent: @Composable MyDropdownMenuContainerScope.() -> Unit,
    ) {
        MyDropdownMenuContainer {
            when (style) {
                is MyDropdown.Style.Button -> {
                    val minHeight = MaterialTheme.components.textField.outlinedMinHeight
                    Box(
                        modifier = modifier
                            .clip(shape)
                            .border(1.dp, borderColor, shape)
                            .background(backgroundColor)
                            .then(
                                if (enabled) Modifier.clickable(onClick = onToggle) else Modifier
                            )
                            .width(IntrinsicSize.Max)
                            .heightIn(min = minHeight),
                        contentAlignment = Alignment.Center,
                    ) {
                        content(Modifier.fillMaxWidth())
                        popupContent()
                    }

                }

                is MyDropdown.Style.OutlinedButton -> {
                    val colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                    )
                    Box(modifier = modifier.width(IntrinsicSize.Max)) {

                        val minHeight = MaterialTheme.components.textField.outlinedMinHeight
                        val minWidth = MaterialTheme.components.textField.outlinedMinWidth
                        val padding = when (MaterialTheme.components.style) {
                            Components.Style.Material3 -> PaddingValues(0.dp)
                            Components.Style.Custom -> MaterialTheme.components.textField.contentPaddingWithoutLabel
                        }

                        MyOutlinedDecoratedContainer(
                            title = title,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = enabled,
                            placeholder = null,
                            leadingIcon = null,
                            trailingIcon = null,
                            colors = colors,
                            shape = shape,
                            contentPadding = padding,
                            onClick = onToggle,
                            minHeight = minHeight,
                            minWidth = minWidth,
                            canFocus = false
                        ) {
                            content(
                                Modifier.fillMaxWidth()
                                // .then(MyOutlinedDecoratedContainer.MODIFIER_CORRECTION)
                            )
                        }
                        popupContent()
                    }
                }
            }
        }
    }


    @Composable
    internal fun DropdownContent(
        modifier: Modifier,
        style: Style,
        expanded: MutableState<Boolean>,
        color: Color,
        content: @Composable (modifier: Modifier, labelColor: Color) -> Unit,
    ) {
        val rotation by animateFloatAsState(if (expanded.value) -180f else 0f)
        val labelColor =
            color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.onSurfaceVariant

        val contentPadding = when (style) {
            Style.Button -> MaterialTheme.components.textField.outlinedButtonStyleContentPadding
            Style.OutlinedButton -> MaterialTheme.components.textField.outlinedContentPadding
        }

        // falls titel sichtbar: content padding um top padding verringern!
        var topPadding = contentPadding.calculateTopPadding()
        val bottomPadding = contentPadding.calculateBottomPadding()
        val startPadding = contentPadding.calculateStartPadding(LocalLayoutDirection.current)
        val endPadding = contentPadding.calculateEndPadding(LocalLayoutDirection.current)
        if (MaterialTheme.components.style == Components.Style.Material3 && style is Style.OutlinedButton) {
            topPadding -= MyOutlinedDecoratedContainer.minimizedLabelHalfHeight()
            topPadding = topPadding.coerceAtLeast(0.dp)
        }

        Row(
            modifier = modifier
                .width(IntrinsicSize.Max)
                .padding(
                    start = startPadding,
                    end = endPadding,
                    top = topPadding,
                    bottom = bottomPadding
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content(Modifier.weight(1f), labelColor)
            Icon(
                modifier = Modifier.rotate(rotation),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = labelColor
            )
        }
    }

    class Filter<T>(
        val label: String,
        val filter: (filter: String, item: T) -> Boolean,
    )

    internal class DropdownFilter<T>(
        val label: String,
        val filter: (filter: String, item: Item<T>) -> Boolean,
    )

    internal class Item<T>(
        val item: T,
        val index: Int,
        val text: String,
        val textDropdown: String,
    )

    sealed class Style {

        data object Button : Style()

        data object OutlinedButton : Style()
    }


    class SimpleData<T : ILabel>(
        val value: T,
        val values: List<T>,
        val onValueChange: (T) -> Unit,
    ) {

        constructor(
            value: MutableState<T>,
            values: List<T>,
        ) : this(
            value = value.value,
            values = values,
            onValueChange = { newValue -> value.value = newValue }
        )
    }
}

@Composable
fun <T : ILabel> MyDropdown(
    data: MyDropdown.SimpleData<T>,
    modifier: Modifier = Modifier,
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
) {
    MyDropdown(
        items = data.values,
        selected = data.value,
        onSelectionChange = { value -> data.onValueChange(value) },
        mapper = { it.label().toSentenceCase() },
        mapperDropdown = { (if (it is ILabelAndShortLabel) it.labelShort() else it.label()).toSentenceCase() },
        modifier = modifier,
        popupWidthConfig = popupWidthConfig
    )
}

@Composable
fun <T> MyDropdown(
    items: List<T>,
    selected: MutableState<T>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    mapperDropdown: @Composable (item: T) -> String = mapper,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: Style = MyDropdownDefaults.styleButton(),
    shape: Shape = MyDropdownDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor, 1)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChange: ((T) -> Unit)? = null,
) {
    val selectedIndex = items.indexOf(selected.value)
    MyDropdownIndex(
        items = items,
        selectedIndex = selectedIndex,
        modifier = modifier,
        title = title,
        mapper = mapper,
        mapperDropdown = mapperDropdown,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        style = style,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        dropdownContent = dropdownContent,
        onSelectionChange = { index ->
            val value = items[index]
            selected.value = value
            onSelectionChange?.invoke(value)
        }
    )
}

@Composable
fun <T> MyDropdown(
    items: List<T>,
    selected: T,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    mapperDropdown: @Composable (item: T) -> String = mapper,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    shape: Shape = MyDropdownDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor, 1)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChange: (T) -> Unit,
) {
    val selectedIndex = items.indexOf(selected)
    MyDropdownIndex(
        items = items,
        selectedIndex = selectedIndex,
        modifier = modifier,
        title = title,
        mapper = mapper,
        mapperDropdown = mapperDropdown,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        style = style,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        dropdownContent = dropdownContent,
        onSelectionChange = { index ->
            val value = items[index]
            onSelectionChange.invoke(value)
        }
    )
}

@Composable
fun <T> MyDropdownIndex(
    items: List<T>,
    selectedIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    mapperDropdown: @Composable (item: T) -> String = mapper,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    shape: Shape = MyDropdownDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor, 1)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChange: ((Int) -> Unit)? = null,
) {
    MyDropdownIndex(
        items = items,
        selectedIndex = selectedIndex.value,
        modifier = modifier,
        title = title,
        mapper = mapper,
        mapperDropdown = mapperDropdown,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = filter,
        style = style,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        dropdownContent = dropdownContent,
        onSelectionChange = { index ->
            selectedIndex.value = index
            onSelectionChange?.invoke(index)
        }
    )
}

@Composable
fun <T> MyDropdownIndex(
    items: List<T>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    mapperDropdown: @Composable (item: T) -> String = mapper,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    shape: Shape = MyDropdownDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor, 1)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChange: ((Int) -> Unit),
) {
    val dropdownItems = items.mapIndexed { index, item ->
        MyDropdown.Item(
            item = mapper(item),
            index = index,
            text = mapper(item),
            textDropdown = mapperDropdown(item)
        )
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
    val content: @Composable (modifier: Modifier, item: MyDropdown.Item<String>?, title: String, titleColor: Color, textColor: Color) -> Unit =
        { modifier, item, title, titleColor, textColor ->
            content(
                modifier,
                item?.index?.takeIf { it != -1 }?.let { items[it] },
                title,
                item?.text ?: "",
                titleColor,
                textColor
            )
        }
    val dropdownContent: @Composable (item: MyDropdown.Item<String>, selected: Boolean) -> Unit =
        { item, selected ->
            dropdownContent(items[item.index], item.textDropdown, selected)
        }
    MyDropdownImpl(
        modifier = modifier,
        title = title,
        items = dropdownItems,
        selected = selectedIndex,
        enabled = enabled,
        color = color,
        backgroundColor = backgroundColor,
        filter = dropdownFilter,
        style = style,
        shape = shape,
        popupWidthConfig = popupWidthConfig,
        content = content,
        dropdownContent = dropdownContent,
        onSelectionChange = { item -> onSelectionChange.invoke(item.index) }
    )
}

// --------------------------------
// Implementation
// --------------------------------

@OptIn(ExperimentalMaterial3Api::class)
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
    style: Style = MyDropdownDefaults.styleButton(),
    shape: Shape = MaterialTheme.shapes.small,
    popupWidthConfig: MyDropdownMenu.WidthConfig = MaterialTheme.components.dropdown.widthConfig,
    content: @Composable (modifier: Modifier, item: MyDropdown.Item<T>?, title: String, titleColor: Color, textColor: Color) -> Unit,
    dropdownContent: @Composable (item: MyDropdown.Item<T>, selected: Boolean) -> Unit,
    onSelectionChange: (item: MyDropdown.Item<T>) -> Unit,
) {
    val filterText = remember { mutableStateOf("") }
    val filteredItems = remember(items) { mutableStateOf(items) }

    val expanded = remember { mutableStateOf(false) }
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

            if (filter != null) {
                LaunchedEffect(filterText.value, filteredItems.value, expanded) {
                    if (expanded.value) {
                        filteredItems.value = items.filter {
                            filter.filter(filterText.value, it)
                        }
                    }
                }
                LaunchedEffect(expanded.value) {
                    if (!expanded.value) {
                        filterText.value = ""
                    }
                }
            }

            MyDropdown.DropdownContent(
                modifier = contentModifier,
                style = style,
                expanded = expanded,
                color = color,
            ) { contentModifier, labelColor ->
                val item = items.find { it.index == selected }
                content(
                    contentModifier,
                    item,
                    internalTitle,
                    labelColor,
                    color
                )
            }
        },
        popupContent = {
            MyDropdownDropdown(
                expanded = expanded,
                filter = filter,
                title = title,
                filterText = filterText,
                filteredItems = filteredItems,
                dropdownContent = dropdownContent,
                selected = selected,
                onSelectionChange = onSelectionChange,
                popupWidthConfig = popupWidthConfig,
            )
        },
    )
}

@Composable
private fun <T> MyDropdownMenuContainerScope.MyDropdownDropdown(
    expanded: MutableState<Boolean>,
    filter: MyDropdown.DropdownFilter<T>?,
    title: String,
    filterText: MutableState<String>,
    filteredItems: MutableState<List<MyDropdown.Item<T>>>,
    dropdownContent: @Composable ((MyDropdown.Item<T>, Boolean) -> Unit),
    selected: Int,
    onSelectionChange: (MyDropdown.Item<T>) -> Unit,
    popupWidthConfig: MyDropdownMenu.WidthConfig,
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(expanded.value) {
        if (!expanded.value) {
            scrollState.scrollTo(0)
        }
    }

    MyDropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        widthConfig = popupWidthConfig,
        scrollState = scrollState
    ) {
        if (filter != null) {
            Column(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = 0,
                            y = scrollState.value
                        )
                    }
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(all = MaterialTheme.padding.default)
                    .zIndex(2f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                MyInput(
                    value = filterText.value,
                    title = filter.label,
                    minLines = 1,
                    maxLines = 1,
                    onValueChange = { filterText.value = it },
                )
            }

        }
        filteredItems.value.forEach {
            MyDropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f),
                text = {
                    dropdownContent(it, it.index == selected)
                },
                onClick = {
                    onSelectionChange(it)
                    expanded.value = false
                }
            )
        }
    }
}
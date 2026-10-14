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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.michaelflisar.toolbox.components.MyDropdownMenu
import com.michaelflisar.toolbox.extensions.toSentenceCase
import com.michaelflisar.toolbox.interfaces.ILabel
import com.michaelflisar.toolbox.interfaces.ILabelAndShortLabel
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing
import com.michaelflisar.toolbox.components.MyDropdownMenu as showMyDropdownMenu

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
    fun style(): MyDropdown.Style = MyDropdown.Style.Button()

    @Composable
    fun shape() = OutlinedTextFieldDefaults.shape

    internal val styleTitle: TextStyle
        @Composable get() = LocalTextStyle.current.copy(fontSize = LocalTextStyle.current.fontSize * .75f)//MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * .8f)

    internal val styleText: TextStyle
        @Composable get() = LocalTextStyle.current//MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize)

    internal val popupWidthConfig: MyDropdownMenu.WidthConfig
        @Composable get() = MyDropdownMenuDefaults.widthConfig

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
                    Box(
                        modifier = modifier
                            .clip(shape)
                            .border(1.dp, borderColor, shape)
                            .background(backgroundColor)
                            .then(
                                if (enabled) Modifier.clickable(onClick = onToggle) else Modifier
                            )
                            .width(IntrinsicSize.Max)
                            .heightIn(min = OutlinedTextFieldDefaults.MinHeight),
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
                        MyOutlinedDecoratedContainer(
                            title = title,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = enabled,
                            placeholder = null,
                            leadingIcon = null,
                            trailingIcon = null,
                            colors = colors,
                            shape = shape,
                            contentPadding = style.padding,
                            onClick = onToggle,
                        ) {
                            content(
                                Modifier.fillMaxWidth()
                                    .then(MyOutlinedDecoratedContainer.MODIFIER_CORRECTION)
                            )
                        }
                        popupContent()
                    }
                }
            }
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
        abstract val padding: PaddingValues

        data class Button(
            override val padding: PaddingValues = PaddingValues(
                horizontal = 16.dp,
                vertical = 5.dp
            ),
        ) : Style()

        data class OutlinedButton(
            override val padding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(), // all 16
        ) : Style()
    }

    class SimpleData<T : ILabel>(
        val value: T,
        val values: List<T>,
        val onValueChanged: (T) -> Unit,
    ) {

        constructor(
            value: MutableState<T>,
            values: List<T>,
        ) : this(
            value = value.value,
            values = values,
            onValueChanged = { newValue -> value.value = newValue }
        )
    }
}

@Composable
fun <T : ILabel> MyDropdown(
    data: MyDropdown.SimpleData<T>,
    modifier: Modifier = Modifier,
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
) {
    MyDropdown(
        items = data.values,
        selected = data.value,
        onSelectionChanged = { value -> data.onValueChanged(value) },
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
    style: MyDropdown.Style = MyDropdownDefaults.style(),
    shape: Shape = MyDropdownDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor, 1)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChanged: ((T) -> Unit)? = null,
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
        onSelectionChanged = { index ->
            val value = items[index]
            selected.value = value
            onSelectionChanged?.invoke(value)
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
    style: MyDropdown.Style = MyDropdownDefaults.style(),
    shape: Shape = MyDropdownDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor, 1)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChanged: (T) -> Unit,
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
        onSelectionChanged = { index ->
            val value = items[index]
            onSelectionChanged.invoke(value)
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
    style: MyDropdown.Style = MyDropdownDefaults.style(),
    shape: Shape = MyDropdownDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor, 1)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChanged: ((Int) -> Unit)? = null,
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
        onSelectionChanged = { index ->
            selectedIndex.value = index
            onSelectionChanged?.invoke(index)
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
    style: MyDropdown.Style = MyDropdownDefaults.style(),
    shape: Shape = MyDropdownDefaults.shape(),
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor, 1)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChanged: ((Int) -> Unit),
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
        onSelectionChange = { item -> onSelectionChanged.invoke(item.index) }
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
    style: MyDropdown.Style = MyDropdownDefaults.style(),
    shape: Shape = MaterialTheme.shapes.small,
    popupWidthConfig: MyDropdownMenu.WidthConfig = MyDropdownDefaults.popupWidthConfig,
    content: @Composable (modifier: Modifier, item: MyDropdown.Item<T>?, title: String, titleColor: Color, textColor: Color) -> Unit,
    dropdownContent: @Composable (item: MyDropdown.Item<T>, selected: Boolean) -> Unit,
    onSelectionChange: (item: MyDropdown.Item<T>) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded.value) -180f else 0f)
    val borderColor = color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.outline

    val filterText = remember { mutableStateOf("") }
    val filteredItems = remember(items) { mutableStateOf(items) }

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
            MyDropdownContent(
                modifier = contentModifier,
                expanded = expanded,
                rotation = rotation,
                title = if (style is MyDropdown.Style.OutlinedButton) "" else title,
                items = items,
                selected = selected,
                color = color,
                filter = filter,
                filterText = filterText,
                filteredItems = filteredItems,
                contentPadding = if (style is MyDropdown.Style.OutlinedButton) PaddingValues(0.dp) else style.padding,
                content = content,
            )
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
private fun <T> MyDropdownContent(
    modifier: Modifier,
    expanded: MutableState<Boolean>,
    rotation: Float,
    title: String,
    items: List<MyDropdown.Item<T>>,
    selected: Int,
    color: Color,
    filter: MyDropdown.DropdownFilter<T>?,
    filterText: MutableState<String>,
    filteredItems: MutableState<List<MyDropdown.Item<T>>>,
    contentPadding: PaddingValues,
    content: @Composable (modifier: Modifier, item: MyDropdown.Item<T>?, title: String, titleColor: Color, textColor: Color) -> Unit,
) {
    val labelColor =
        color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.onSurfaceVariant

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

    Row(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val item = items.find { it.index == selected }
        content(
            Modifier.weight(1f),
            item,
            title,
            labelColor,
            color
        )

        Icon(
            modifier = Modifier.rotate(rotation),
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = labelColor
        )
    }
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

    showMyDropdownMenu(
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
                OutlinedTextField(
                    value = filterText.value,
                    label = { Text(filter.label) },
                    singleLine = true,
                    onValueChange = { filterText.value = it },
                )
            }

        }
        filteredItems.value.forEach {
            DropdownMenuItem(
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
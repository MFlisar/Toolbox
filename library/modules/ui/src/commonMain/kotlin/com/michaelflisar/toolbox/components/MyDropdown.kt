package com.michaelflisar.toolbox.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.michaelflisar.toolbox.Platform.cursor
import com.michaelflisar.toolbox.classes.LocalStyle

object MyDropdownDefaults {

    @Composable
    fun Content(
        modifier: Modifier,
        title: String,
        text: String,
        labelColor: Color,
        color: Color,
        leadingContent: @Composable () -> Unit = {}
    ) {
        val style1 = MaterialTheme.typography.titleSmall
        val style2 = LocalTextStyle.current.merge(fontSize = LocalTextStyle.current.fontSize * .8f)

        MyRow(modifier = modifier) {
            leadingContent()
            Column(modifier = Modifier.weight(1f)) {
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        style = style1,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                        maxLines = 1
                    )
                }
                Text(
                    text = text,
                    style = style2,
                    color = color,
                    maxLines = 1
                )
            }
        }

    }

    @Composable
    fun DropdownContent(
        text: String,
        selected: Boolean,
        leadingContent: @Composable () -> Unit = {}
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
    fun style(): MyDropdown.Style = MyDropdown.Style.Button
}

object MyDropdown {

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

    enum class Style {
        Button,
        OutlinedButton,
    }
}

@Composable
fun <T> MyDropdown(
    items: List<T>,
    selected: MutableState<T>,
    modifier: Modifier = Modifier,
    title: String = "",
    mapper: @Composable (item: T, dropdown: Boolean) -> String,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.style(),
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChanged: ((T) -> Unit)? = null,
) {
    val selectedIndex = items.indexOf(selected.value)
    val dropdownItems = items.mapIndexed { index, item ->
        MyDropdown.Item(
            mapper(item, false),
            index,
            mapper(item, false),
            mapper(item, true)
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
        modifier,
        title,
        dropdownItems,
        selectedIndex,
        enabled,
        color,
        backgroundColor,
        dropdownFilter,
        style,
        content,
        dropdownContent
    ) { item ->
        val s = items[item.index]
        selected.value = s
        onSelectionChanged?.invoke(s)
    }
}

@Composable
fun <T> MyDropdown(
    items: List<T>,
    selected: T,
    modifier: Modifier = Modifier,
    title: String = "",
    mapper: @Composable (item: T, dropdown: Boolean) -> String,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<T>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.style(),
    content: @Composable (modifier: Modifier, item: T?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor)
    },
    dropdownContent: @Composable (item: T, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChanged: ((T) -> Unit)? = null,
) {
    val selectedIndex = items.indexOf(selected)
    val dropdownItems = items.mapIndexed { index, item ->
        MyDropdown.Item(
            mapper(item, false),
            index,
            mapper(item, false),
            mapper(item, true)
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
        modifier,
        title,
        dropdownItems,
        selectedIndex,
        enabled,
        color,
        backgroundColor,
        dropdownFilter,
        style,
        content,
        dropdownContent
    ) { item ->
        onSelectionChanged?.invoke(items[item.index])
    }
}

// Sonderfall: Int Daten (index) + String Werte
@Composable
fun MyDropdown(
    items: List<String>,
    selectedIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<String>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.style(),
    content: @Composable (modifier: Modifier, item: String?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor)
    },
    dropdownContent: @Composable (item: String, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChanged: ((Int) -> Unit)? = null,
) {
    val selectedIndexValue = selectedIndex.value
    val dropdownItems by remember(items) {
        derivedStateOf {
            items.mapIndexed { index, item -> MyDropdown.Item(item, index, item, item) }
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
        modifier,
        title,
        dropdownItems,
        selectedIndexValue,
        enabled,
        color,
        backgroundColor,
        dropdownFilter,
        style,
        content,
        dropdownContent
    ) { item ->
        selectedIndex.value = item.index
        onSelectionChanged?.invoke(item.index)
    }
}

@Composable
fun MyDropdown(
    items: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    filter: MyDropdown.Filter<String>? = null,
    style: MyDropdown.Style = MyDropdownDefaults.style(),
    content: @Composable (modifier: Modifier, item: String?, title: String, text: String, titleColor: Color, textColor: Color) -> Unit = { modifier, item, title, text, titleColor, textColor ->
        MyDropdownDefaults.Content(modifier, title, text, titleColor, textColor)
    },
    dropdownContent: @Composable (item: String, text: String, selected: Boolean) -> Unit = { item, text, selected ->
        MyDropdownDefaults.DropdownContent(text, selected)
    },
    onSelectionChanged: ((Int) -> Unit)? = null,
) {
    val dropdownItems by remember(items) {
        derivedStateOf {
            items.mapIndexed { index, item -> MyDropdown.Item(item, index, item, item) }
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
        modifier,
        title,
        dropdownItems,
        selectedIndex,
        enabled,
        color,
        backgroundColor,
        dropdownFilter,
        style,
        content,
        dropdownContent
    ) { item ->
        onSelectionChanged?.invoke(item.index)
    }
}

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
    content: @Composable (modifier: Modifier, item: MyDropdown.Item<T>?, title: String, titleColor: Color, textColor: Color) -> Unit,
    dropdownContent: @Composable (item: MyDropdown.Item<T>, selected: Boolean) -> Unit,
    onSelectionChange: (item: MyDropdown.Item<T>) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded.value) -180f else 0f)
    val borderColor = color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.outline

    val filterText = remember { mutableStateOf("") }
    val filteredItems = remember(items) { mutableStateOf(items) }

    when (style) {
        MyDropdown.Style.Button -> {
            Box(
                modifier = modifier.clip(MaterialTheme.shapes.small)
                    .border(1.dp, borderColor, MaterialTheme.shapes.small)
                    .background(backgroundColor)
                    .then(
                        if (enabled) {
                            Modifier.clickable {
                                expanded.value = !expanded.value
                            }
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                MyDropdownContent(
                    Modifier.fillMaxWidth().padding(8.dp),
                    expanded,
                    rotation,
                    title,
                    items,
                    selected,
                    color,
                    filter,
                    filterText,
                    filteredItems,
                    content
                )
                MyDropdownDropdown(
                    expanded,
                    filter,
                    title,
                    filterText,
                    filteredItems,
                    dropdownContent,
                    selected,
                    onSelectionChange
                )
            }
        }

        MyDropdown.Style.OutlinedButton -> {
            val colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
            )
            Box(
                modifier = modifier
            ) {
                MyOutlinedDecoratedContainer(
                    title = title,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    placeholder = null,
                    leadingIcon = null,
                    trailingIcon = null,
                    colors = colors,
                    onClick = {
                        expanded.value = !expanded.value
                    }
                ) {
                    MyDropdownContent(
                        // TODO: find out why extra padding is necessary + fix it
                        Modifier.fillMaxWidth().then(MyOutlinedDecoratedContainer.MODIFIER_CORRECTION),
                        expanded,
                        rotation,
                        "",
                        items,
                        selected,
                        color,
                        filter,
                        filterText,
                        filteredItems,
                        content
                    )
                }
                MyDropdownDropdown(
                    expanded,
                    filter,
                    title,
                    filterText,
                    filteredItems,
                    dropdownContent,
                    selected,
                    onSelectionChange
                )
            }
        }
    }
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
    content: @Composable (modifier: Modifier, item: MyDropdown.Item<T>?, title: String, titleColor: Color, textColor: Color) -> Unit
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
        LaunchedEffect(expanded) {
            if (!expanded.value) {
                filterText.value = ""
            }
        }
    }


    Row(
        modifier = modifier,
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
private fun <T> MyDropdownDropdown(
    expanded: MutableState<Boolean>,
    filter: MyDropdown.DropdownFilter<T>?,
    title: String,
    filterText: MutableState<String>,
    filteredItems: MutableState<List<MyDropdown.Item<T>>>,
    dropdownContent: @Composable ((MyDropdown.Item<T>, Boolean) -> Unit),
    selected: Int,
    onSelectionChange: (MyDropdown.Item<T>) -> Unit
) {
    val scrollState = rememberScrollState()
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        scrollState = scrollState
    ) {
        if (filter != null) {
            Column(
                modifier = Modifier
                    .offset(y = with(LocalDensity.current) { scrollState.value.toDp() })
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(all = LocalStyle.current.paddingDefault)
                    .zIndex(2f),
                verticalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall)
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
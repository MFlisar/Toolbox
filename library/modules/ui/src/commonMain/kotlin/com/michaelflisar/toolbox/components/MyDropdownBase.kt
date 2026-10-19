package com.michaelflisar.toolbox.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.michaelflisar.toolbox.theme.LocalComponentOptions
import com.michaelflisar.toolbox.theme.LocalComponentStyles
import com.michaelflisar.toolbox.theme.MyThemeType
import com.michaelflisar.toolbox.theme.padding
import com.michaelflisar.toolbox.theme.spacing
import kotlinx.coroutines.launch

object MyDropdown {

    class Filter<T : Any>(
        val label: String,
        val filter: (filter: String, item: T) -> Boolean,
    )

    internal class Item<T : Any>(
        val item: T,
        val index: Int,
    )

    sealed class Style {

        data object Button : Style()

        data object OutlinedButton : Style()
    }

    sealed interface RenderContext<out T : Any, out S> {

        data class Selection<S>(
            val selection: S,
        ) : RenderContext<Nothing, S>

        data class DropdownItem<T : Any>(
            val item: T,
            val selected: Boolean,
        ) : RenderContext<T, Nothing>
    }

    // Innerhalb von object MyDropdown
    fun <T : Any, S> RenderContent(
        content: @Composable (RenderContext.Selection<S>) -> Unit,
        dropdownContent: @Composable (RenderContext.DropdownItem<T>) -> Unit,
    ): @Composable (RenderContext<T, S>) -> Unit = { context ->
        when (context) {
            is RenderContext.Selection -> content(context)
            is RenderContext.DropdownItem -> dropdownContent(context)
        }
    }

}

// --------------------------------
// base composable for dropdowns
// --------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun <T : Any, S> MyDropdownBase(
    modifier: Modifier = Modifier,
    title: String,
    items: List<T>,
    selection: S,
    isSelected: (index: Int) -> Boolean,
    closeOnItemClick: Boolean,
    enabled: Boolean,
    color: Color,
    backgroundColor: Color,
    filter: MyDropdown.Filter<T>?,
    style: MyDropdown.Style = MyDropdownDefaults.styleButton(),
    shape: Shape = MaterialTheme.shapes.small,
    popupWidthConfig: MyDropdownMenu.WidthConfig = LocalComponentOptions.current.dropdown.widthConfig,
    popupHeader: @Composable () -> Unit = {},
    content: @Composable (context: MyDropdown.RenderContext<T, S>) -> Unit,
    onItemClick: (index: Int, item: T) -> Unit,
) {
    val filterText = remember(filter != null) { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val indexedItems = items.mapIndexed { index, item -> MyDropdown.Item(item, index) }
    val filteredItems = if (filter != null) {
        indexedItems.filter { filter.filter(filterText.value, it.item) }
    } else {
        indexedItems
    }
    val onDismissRequest = {
        expanded.value = false
    }
    LaunchedEffect(enabled) {
        if (!enabled) {
            onDismissRequest()
        }
    }
    val borderColor = color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.outline
    val internalTitle = if (style is MyDropdown.Style.OutlinedButton) "" else title

    DropdownContainer(
        modifier = modifier,
        title = title,
        enabled = enabled,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
        shape = shape,
        style = style,
        onToggle = {
            if (enabled) {
                if (expanded.value) {
                    onDismissRequest()
                } else {
                    // Preserve filter and scroll position throughout the exit animation.
                    scope.launch {
                        scrollState.scrollTo(0)
                        filterText.value = ""
                        expanded.value = true
                    }
                }
            }
        },
        content = { contentModifier ->
            DropdownContent(
                modifier = contentModifier,
                style = style,
                expanded = expanded,
                color = color,
            ) { contentModifier, labelColor ->
                Column(modifier = contentModifier) {
                    if (internalTitle.isNotEmpty()) {
                        Text(
                            text = internalTitle,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = labelColor,
                            maxLines = 1
                        )
                    }
                    CompositionLocalProvider(
                        LocalContentColor provides
                                (color.takeIf { it != Color.Unspecified }
                                    ?: LocalContentColor.current)
                    ) {
                        content(MyDropdown.RenderContext.Selection(selection))
                    }
                }
            }
        },
        popupContent = {
            MyDropdownDropdown(
                expanded = expanded,
                scrollState = scrollState,
                filter = filter,
                title = title,
                filterText = filterText,
                filteredItems = filteredItems,
                content = content,
                isSelected = isSelected,
                closeOnItemClick = closeOnItemClick,
                popupHeader = popupHeader,
                onItemClick = onItemClick,
                onDismissRequest = onDismissRequest,
                popupWidthConfig = popupWidthConfig,
            )
        },
    )
}

@Composable
private fun <T : Any, S> MyDropdownMenuContainerScope.MyDropdownDropdown(
    expanded: MutableState<Boolean>,
    scrollState: ScrollState,
    filter: MyDropdown.Filter<T>?,
    title: String,
    filterText: MutableState<String>,
    filteredItems: List<MyDropdown.Item<T>>,
    content: @Composable (context: MyDropdown.RenderContext<T, S>) -> Unit,
    isSelected: (index: Int) -> Boolean,
    closeOnItemClick: Boolean,
    popupHeader: @Composable () -> Unit,
    onItemClick: (index: Int, item: T) -> Unit,
    onDismissRequest: () -> Unit,
    popupWidthConfig: MyDropdownMenu.WidthConfig,
) {
    MyDropdownMenu(
        expanded = expanded.value,
        onDismissRequest = onDismissRequest,
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
        popupHeader()
        filteredItems.forEach { item ->
            key(item.index) {
                MyDropdownMenuItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f),
                    text = {
                        CompositionLocalProvider(
                            LocalContentColor provides if (isSelected(item.index)) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        ) {
                            content(
                                MyDropdown.RenderContext.DropdownItem(
                                    item.item,
                                    isSelected(item.index)
                                )
                            )
                        }
                    },
                    onClick = {
                        onItemClick(item.index, item.item)
                        if (closeOnItemClick) onDismissRequest()
                    }
                )
            }
        }
    }
}

// --------------------------------
// internal helper composables
// --------------------------------

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
                val minHeight = LocalComponentStyles.current.textField.outlinedMinHeight
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

                    val minHeight = LocalComponentStyles.current.textField.outlinedMinHeight
                    val minWidth = LocalComponentStyles.current.textField.outlinedMinWidth
                    val padding = when (LocalComponentStyles.current.type) {
                        MyThemeType.Material3 -> PaddingValues(0.dp)
                        MyThemeType.Custom -> LocalComponentStyles.current.textField.contentPaddingWithoutLabel
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
    style: MyDropdown.Style,
    expanded: MutableState<Boolean>,
    color: Color,
    content: @Composable (modifier: Modifier, labelColor: Color) -> Unit,
) {
    val rotation by animateFloatAsState(if (expanded.value) -180f else 0f)
    val labelColor =
        color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.onSurfaceVariant

    val contentPadding = when (style) {
        MyDropdown.Style.Button -> LocalComponentStyles.current.textField.outlinedButtonStyleContentPadding
        MyDropdown.Style.OutlinedButton -> LocalComponentStyles.current.textField.outlinedContentPadding
    }

    // falls titel sichtbar: content padding um top padding verringern!
    var topPadding = contentPadding.calculateTopPadding()
    val bottomPadding = contentPadding.calculateBottomPadding()
    val startPadding = contentPadding.calculateStartPadding(LocalLayoutDirection.current)
    val endPadding = contentPadding.calculateEndPadding(LocalLayoutDirection.current)
    if (LocalComponentStyles.current.type == MyThemeType.Material3 && style is MyDropdown.Style.OutlinedButton) {
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
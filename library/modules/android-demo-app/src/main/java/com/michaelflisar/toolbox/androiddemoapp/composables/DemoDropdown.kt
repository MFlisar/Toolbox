package com.michaelflisar.toolbox.androiddemoapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DemoDropdown(
    modifier: Modifier = Modifier,
    label: String = "",
    items: List<String>,
    selected: String,
    enabled: Boolean = true,
    maxLines: Int? = null,
    onItemSelected: (index: Int, item: String) -> Unit = { _, _ -> }
) {
    DemoDropdown(
        modifier,
        label,
        items,
        selected,
        { item, _ -> item },
        null,
        enabled,
        maxLines,
        onItemSelected
    )
}

@Composable
fun DemoDropdown(
    modifier: Modifier = Modifier,
    label: String = "",
    items: List<String>,
    selected: MutableState<String>,
    enabled: Boolean = true,
    maxLines: Int? = null,
    onItemSelected: (index: Int, item: String) -> Unit = { _, _ -> }
) {
    DemoDropdown(
        modifier,
        label,
        items,
        selected,
        { item, _ -> item },
        null,
        enabled,
        maxLines,
        onItemSelected
    )
}

@Composable
fun <T> DemoDropdown(
    modifier: Modifier = Modifier,
    label: String = "",
    items: List<T>,
    selected: MutableState<T>,
    itemToString: @Composable (item: T, dropdown: Boolean) -> String,
    leadingIcon: @Composable ((item: T, dropdown: Boolean) -> Unit)? = null,
    enabled: Boolean = true,
    maxLines: Int? = null,
    onItemSelected: (index: Int, item: T) -> Unit = { _, _ -> }
) {
    DemoDropdown(
        modifier,
        label,
        items,
        selected.value,
        itemToString,
        leadingIcon,
        enabled,
        maxLines
    ) { index, item ->
        selected.value = item
        onItemSelected.invoke(index, item)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DemoDropdown(
    modifier: Modifier = Modifier,
    label: String = "",
    items: List<T>,
    selected: T,
    itemToString: @Composable (item: T, dropdown: Boolean) -> String,
    leadingIcon: @Composable ((item: T, dropdown: Boolean) -> Unit)? = null,
    enabled: Boolean = true,
    maxLines: Int? = null,
    onItemSelected: (index: Int, item: T) -> Unit = { _, _ -> }
) {
    var selected by remember(selected) { mutableStateOf(selected) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier.width(IntrinsicSize.Min),
        expanded = expanded,
        onExpandedChange = {
            if (enabled)
                expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusable(false)
                .menuAnchor(),
            readOnly = true,
            enabled = enabled,
            value = itemToString(selected, false),
            onValueChange = { },
            label = { Text(text = label) },
            leadingIcon = if (leadingIcon != null) {
                { leadingIcon(selected, false) }
            } else null,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            singleLine = maxLines == 1,
            maxLines = maxLines ?: Int.MAX_VALUE
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        if (item != selected) {
                            selected = item
                            onItemSelected.invoke(index, item)
                        }
                        expanded = false
                    },
                    text = {
                        Text(text = itemToString(item, true), fontWeight = if (item == selected) FontWeight.Bold else null)
                    },
                    leadingIcon = if (leadingIcon != null) {
                        { leadingIcon(item, true) }
                    } else null
                )
            }
        }
    }
}

@Composable
fun <T> DemoDropdownButton(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(all = 8.dp),
    items: List<T>,
    selected: MutableState<T>,
    itemToString: @Composable (item: T, dropdown: Boolean) -> String,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    iconSize: Dp = 24.dp,
    enabled: Boolean = true,
    hideIconIfDisabled: Boolean = false,
    countForLazyColumnWorkaround: Int? = null
) {
    val showMenu = rememberDemoMenuState()
    val width = remember {
        mutableStateOf(0.dp)
    }
    DemoDropdownButton(
        modifier = modifier
            .onSizeChanged {
                width.value = it.width.dp
            },
        padding = padding,
        color = color,
        onColor = onColor,
        iconSize = iconSize,
        enabled = enabled,
        hideIconIfDisabled = hideIconIfDisabled,
        onClick = { showMenu.show() }
    ) {
        Text(itemToString(selected.value, false))
        DemoPopupMenu(
            state = showMenu,
            autoDismiss = true,
            largePopupData = LargePopupData(
                countForLazy = countForLazyColumnWorkaround,
                parentWidth = width.value
            )
        ) {
            items.map {
                Item(text = itemToString(it, true), fontWeight = if (it == selected.value) FontWeight.Bold else null) {
                    selected.value = it
                }
            }
        }
    }
}

@Composable
fun <T> DemoDropdownButton(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(all = 8.dp),
    items: List<T>,
    selected: T,
    itemToString: @Composable (item: T, dropdown: Boolean) -> String,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    iconSize: Dp = 24.dp,
    enabled: Boolean = true,
    hideIconIfDisabled: Boolean = false,
    onItemSelected: (item: T) -> Unit
) {
    val showMenu = rememberDemoMenuState()
    DemoDropdownButton(
        modifier = modifier,
        padding = padding,
        color = color,
        onColor = onColor,
        iconSize = iconSize,
        enabled = enabled,
        hideIconIfDisabled = hideIconIfDisabled,
        onClick = { showMenu.show() }
    ) {
        Text(itemToString(selected, false))
        DemoPopupMenu(
            state = showMenu,
            autoDismiss = true
        ) {
            items.map {
                Item(text = itemToString(it, true)) {
                    onItemSelected(it)
                }
            }
        }
    }
}

@Composable
fun <T> DemoDropdownButton(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(all = 8.dp),
    items: List<T>,
    selected: T,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    iconSize: Dp = 24.dp,
    enabled: Boolean = true,
    hideIconIfDisabled: Boolean = false,
    onItemSelected: (item: T) -> Unit,
    content: @Composable (item: T, popup: Boolean) -> Unit
) {
    val showMenu = rememberDemoMenuState()
    DemoDropdownButton(
        modifier = modifier,
        padding = padding,
        color = color,
        onColor = onColor,
        iconSize = iconSize,
        enabled = enabled,
        hideIconIfDisabled = hideIconIfDisabled,
        onClick = { showMenu.show() }
    ) {
        content(selected, false)
        DemoPopupMenu(
            state = showMenu,
            autoDismiss = true
        ) {
            items.map { item ->
                Item(content = { content(item, true) }) {
                    onItemSelected(item)
                }
            }
        }
    }
}

@Composable
fun DemoDropdownButton(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(all = 8.dp),
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    iconSize: Dp = 24.dp,
    enabled: Boolean = true,
    hideIconIfDisabled: Boolean = false,
    content: @Composable () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            //.width(IntrinsicSize.Min)
            .background(
                color,
                MaterialTheme.shapes.extraSmall
            )
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    color = onColor
                ),
                enabled = enabled
            ) { onClick() }
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f, false)) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                LocalContentColor provides onColor
            ) {
                content()
            }
        }
        //Spacer(modifier = Modifier.weight(1f))
        if (enabled || !hideIconIfDisabled) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "",
                tint = onColor
            )
        } else {
            Spacer(modifier = Modifier.height(iconSize))
        }
    }
}
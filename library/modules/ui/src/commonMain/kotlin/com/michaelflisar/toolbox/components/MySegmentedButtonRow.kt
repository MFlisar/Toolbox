package com.michaelflisar.toolbox.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> MySegmentedButtonRow(
    items: List<T>,
    selected: MutableState<T>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    icons: List<@Composable () -> Unit>? = null,
    forceSelection: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    onSelectionChanged: ((T) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    val selectedIndex = items.indexOf(selected.value)
    MySegmentedButtonRowImpl(
        modifier = modifier,
        items = texts,
        icons = icons,
        selected = selectedIndex,
        forceSelection = forceSelection,
        color = color,
        onColor = onColor
    ) { index, item ->
        val s = (if (index >= 0) items[index] else null) as T
        selected.value = s
        onSelectionChanged?.invoke(s)
    }
}

@Composable
fun <T> MySegmentedButtonRow(
    items: List<T>,
    selected: T,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    icons: List<@Composable () -> Unit>? = null,
    forceSelection: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    onSelectionChanged: (T) -> Unit,
) {
    val texts = items.map { mapper(it) }
    val selectedIndex = items.indexOf(selected)
    MySegmentedButtonRowImpl(
        modifier = modifier,
        items = texts,
        icons = icons,
        selected = selectedIndex,
        forceSelection = forceSelection,
        color = color,
        onColor = onColor
    ) { index, item ->
        onSelectionChanged.invoke(items[index])
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> MySegmentedButtonRowIndex(
    items: List<T>,
    selectedIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    icons: List<@Composable () -> Unit>? = null,
    forceSelection: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    onSelectionChanged: ((Int) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    MySegmentedButtonRowImpl(
        modifier = modifier,
        items = texts,
        icons = icons,
        selected = selectedIndex.value,
        forceSelection = forceSelection,
        color = color,
        onColor = onColor
    ) { index, item ->
        selectedIndex.value = index
        onSelectionChanged?.invoke(index)
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> MySegmentedButtonRowIndex(
    items: List<T>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    icons: List<@Composable () -> Unit>? = null,
    forceSelection: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    onSelectionChanged: (Int) -> Unit,
) {
    val texts = items.map { mapper(it) }
    MySegmentedButtonRowImpl(
        modifier = modifier,
        items = texts,
        icons = icons,
        selected = selectedIndex,
        forceSelection = forceSelection,
        color = color,
        onColor = onColor
    ) { index, item ->
        onSelectionChanged.invoke(index)
    }
}

// --------------------------------
// Implementation
// --------------------------------

@Composable
private fun MySegmentedButtonRowImpl(
    modifier: Modifier = Modifier,
    items: List<String>,
    icons: List<@Composable () -> Unit>?,
    selected: Int,
    forceSelection: Boolean,
    color: Color,
    onColor: Color,
    onSelectionChange: (index: Int, item: String?) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            SegmentedButton(
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = color,
                    activeContentColor = onColor,
                    //activeBorderColor = Color.Unspecified,
                    //inactiveContainerColor = Color.Unspecified,
                    //inactiveContentColor = Color.Unspecified,
                    //inactiveBorderColor = Color.Unspecified,
                    //disabledActiveContainerColor = Color.Unspecified,
                    //disabledActiveContentColor = Color.Unspecified,
                    //disabledActiveBorderColor = Color.Unspecified,
                    //disabledInactiveContainerColor = Color.Unspecified,
                    //disabledInactiveContentColor = Color.Unspecified,
                    //disabledInactiveBorderColor = Color.Unspecified,
                ),
                shape = SegmentedButtonDefaults.itemShape(index = index, count = items.size),
                onClick = {
                    if (selected == index) {
                        if (!forceSelection) {
                            onSelectionChange(-1, null)
                        }
                    } else
                        onSelectionChange(index, item)
                },
                selected = index == selected,
                icon = {
                    SegmentedButtonDefaults.Icon(
                        active = index == selected,
                        inactiveContent = icons?.get(index)?.let {
                            { it() }
                        }
                    )
                },
            ) {
                Text(item)
            }
        }
    }
}
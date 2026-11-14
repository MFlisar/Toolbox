package com.michaelflisar.toolbox.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun <T> MyChipsRowMultiSelect(
    items: List<T>,
    selected: MutableState<List<T>>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    singleRow: Boolean = false,
    showClear: Boolean = true,
    showSelectionInfo: Boolean = true,
    onSelectionChange: ((List<T>) -> Unit)? = null,
) {
    val selectedIndizes = selected.value.map { items.indexOf(it) }
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Multi(selectedIndizes) { selectedIndizes, selectedItems ->
            selected.value = selectedIndizes.map { items[it] }
            onSelectionChange?.invoke(selectedItems)
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        singleRow = singleRow,
        showClear = showClear,
        showSelectionInfo = showSelectionInfo
    )
}

@Composable
fun <T> MyChipsRowMultiSelect(
    items: List<T>,
    selected: List<T>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    singleRow: Boolean = false,
    showClear: Boolean = true,
    showSelectionInfo: Boolean = true,
    onSelectionChange: (List<T>) -> Unit,
) {
    val selectedIndizes = selected.map { items.indexOf(it) }
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Multi(selectedIndizes) { selectedIndizes, selectedItems ->
            onSelectionChange(selectedItems)
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        singleRow = singleRow,
        showClear = showClear,
        showSelectionInfo = showSelectionInfo
    )
}

@Composable
fun <T> MyChipsRowMultiSelectIndex(
    items: List<T>,
    selectedIndizes: MutableState<List<Int>>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    singleRow: Boolean = false,
    showClear: Boolean = true,
    showSelectionInfo: Boolean = true,
    onSelectionChange: ((List<T>) -> Unit)? = null,
) {
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Multi(selectedIndizes.value) { selectedIndizes2, selectedItems ->
            selectedIndizes.value = selectedIndizes2
            onSelectionChange?.invoke(selectedItems)
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        singleRow = singleRow,
        showClear = showClear,
        showSelectionInfo = showSelectionInfo
    )
}

@Composable
fun <T> MyChipsRowMultiSelectIndex(
    items: List<T>,
    selectedIndizes: List<Int>,
    onSelectionChange: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    singleRow: Boolean = false,
    showClear: Boolean = true,
    showSelectionInfo: Boolean = true,
) {
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Multi(selectedIndizes) { selectedIndizes2, selectedItems ->
            onSelectionChange.invoke(selectedItems)
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        singleRow = singleRow,
        showClear = showClear,
        showSelectionInfo = showSelectionInfo
    )
}
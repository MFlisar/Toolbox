package com.michaelflisar.toolbox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import com.michaelflisar.toolbox.extensions.disabled
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing
import com.michaelflisar.toolbox.ui.MyScrollableRow

private object MyChipsRow {

    sealed class Selection<T> {

        class Single<T>(
            val selected: Int,
            val onSelectionChange: ((index: Int, item: T) -> Unit)? = null,
        ) : Selection<T>()

        class Multi<T>(
            val selected: List<Int>,
            val onSelectionChange: ((indices: List<Int>, items: List<T>) -> Unit)? = null,
        ) : Selection<T>()
    }
}

@Composable
fun <T> MyChipsSingleRow(
    items: List<T>,
    selected: MutableState<T>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    singleRow: Boolean = false,
    onSelectionChange: ((T) -> Unit)? = null,
) {
    val selectedIndex = items.indexOf(selected.value)
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Single(selectedIndex) { index, item ->
            selected.value = item
            onSelectionChange?.invoke(item)
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        singleRow = singleRow,
        showClear = false,
        showSelectionInfo = false
    )
}

@Composable
fun <T> MyChipsSingleRow(
    items: List<T>,
    selected: T,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    singleRow: Boolean = false,
    onSelectionChange: (T) -> Unit,
) {
    val selectedIndex = items.indexOf(selected)
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Single(selectedIndex) { index, item ->
            onSelectionChange(item)
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        singleRow = singleRow,
        showClear = false,
        showSelectionInfo = false
    )
}

@Composable
fun <T> MyChipsSingleRowIndex(
    items: List<T>,
    selectedIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    singleRow: Boolean = false,
    onSelectionChange: ((Int) -> Unit)? = null,
) {
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Single(selectedIndex.value) { selectedIndizes, selectedItems ->
            selectedIndex.value = selectedIndizes
            onSelectionChange?.invoke(selectedIndizes)
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        singleRow = singleRow,
        showClear = false,
        showSelectionInfo = false
    )
}

@Composable
fun <T> MyChipsSingleRowIndex(
    items: List<T>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    singleRow: Boolean = false,
    onSelectionChange: (Int) -> Unit,
) {
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Single(selectedIndex) { selectedIndizes, selectedItems ->
            onSelectionChange(selectedIndizes)
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        singleRow = singleRow,
        showClear = false,
        showSelectionInfo = false
    )
}

@Composable
fun <T> MyChipsMultiRow(
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
fun <T> MyChipsMultiRow(
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
fun <T> MyChipsMultiRowIndex(
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
fun <T> MyChipsMultiRowIndex(
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

// --------------------------------
// Implementation
// --------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> MyChipsFlowRowImpl(
    modifier: Modifier = Modifier,
    items: List<T>,
    selection: MyChipsRow.Selection<T>,
    mapper: @Composable (item: T) -> String,
    minSegmentWidth: Dp,
    singleRow: Boolean,
    color: Color,
    showClear: Boolean,
    showSelectionInfo: Boolean,
) {
    Column(
        modifier = modifier
    ) {
        val selectedIndices: List<Int> = when (selection) {
            is MyChipsRow.Selection.Multi -> selection.selected
            is MyChipsRow.Selection.Single -> listOf(selection.selected)
        }

        if (showSelectionInfo && selection !is MyChipsRow.Selection.Single) {
            MyLabeledInformationHorizontal(
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.small),
                label = "Selected",
                info = "${selectedIndices.size}/${items.size}"
            )
        }
        if (singleRow) {
            MyScrollableRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                itemSpacing = MaterialTheme.spacing.small,
            ){
                RowContent(
                    items,
                    selection,
                    selectedIndices,
                    mapper,
                    minSegmentWidth,
                    color,
                    showClear,
                    Modifier
                )
            }
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small,
                    Alignment.CenterVertically
                )
            ) {
                RowContent(
                    items,
                    selection,
                    selectedIndices,
                    mapper,
                    minSegmentWidth,
                    color,
                    showClear,
                    Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
private fun <T> RowContent(
    //modifier: Modifier = Modifier,
    items: List<T>,
    selection: MyChipsRow.Selection<T>,
    selectedIndices: List<Int>,
    mapper: @Composable (item: T) -> String,
    minSegmentWidth: Dp,
    color: Color,
    showClear: Boolean,
    modifierAlignCenterVertically: Modifier
) {
    val borderColor = color.takeIf { it != Color.Unspecified }?.disabled()
        ?: MaterialTheme.colorScheme.onSurface.disabled()

    val colorSelected =
        color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary
    val colorSelectedText = MaterialTheme.colorScheme.contentColorFor(colorSelected)

    val colorNotSelected = Color.Unspecified
    val colorNotSelectedText = Color.Unspecified


    val onSelectionChange = when (selection) {
        is MyChipsRow.Selection.Multi -> { indices: List<Int> ->
            selection.onSelectionChange?.invoke(indices, indices.map { items[it] })
        }

        is MyChipsRow.Selection.Single -> { indices: List<Int> ->
            selection.onSelectionChange?.invoke(indices.first(), indices.first().let { items[it] })
        }
    }

    val empty = remember(selectedIndices) { derivedStateOf { selectedIndices.isEmpty() } }

    if (showClear && selection !is MyChipsRow.Selection.Single) {
        val iconSize = 24.dp
        OutlinedButton(
            modifier = Modifier.size(iconSize).then(modifierAlignCenterVertically),
            onClick = {
                onSelectionChange(emptyList())
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = if (empty.value) MaterialTheme.colorScheme.primary.disabled() else MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(all = 0.dp),
            enabled = !empty.value
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
    }

    items.forEachIndexed { index, item ->
        Text(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .border(1.dp, borderColor, MaterialTheme.shapes.small)
                .background(if (selectedIndices.contains(index)) colorSelected else colorNotSelected)
                .clickable {

                    val selectedNew = selectedIndices.toMutableList()
                    if (selection is MyChipsRow.Selection.Single) {
                        selectedNew.clear()
                        selectedNew.add(index)
                    } else {
                        if (selectedIndices.contains(index)) {
                            selectedNew.remove(index)
                        } else {
                            selectedNew.add(index)
                        }
                    }

                    onSelectionChange(selectedNew)
                }
                .widthIn(min = minSegmentWidth)
                .padding(MaterialTheme.padding.small),
            text = mapper(item),
            maxLines = 1,
            color = if (selectedIndices.contains(index)) colorSelectedText else colorNotSelectedText,
            textAlign = TextAlign.Center
        )
    }
}
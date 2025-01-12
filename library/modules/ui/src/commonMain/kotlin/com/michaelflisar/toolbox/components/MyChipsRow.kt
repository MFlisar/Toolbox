package com.michaelflisar.toolbox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.disabled

private object MyChipsRow {

    sealed class Selection<T> {

        class Single<T>(
            val selected: Int,
            val onSelectionChange: ((index: Int, item: T) -> Unit)? = null
        ) : Selection<T>()

        class Multi<T>(
            val selected: List<Int>,
            val onSelectionChange: ((indices: List<Int>, items: List<T>) -> Unit)? = null
        ) : Selection<T>()
    }
}

@Composable
fun <T> MyChipsSingleRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedIndex: MutableState<Int>,
    mapper: (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified
) {
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Single( selectedIndex.value) { selectedIndizes, selectedItems ->
            selectedIndex.value = selectedIndizes
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        showSelectionInfo = false
    )
}

@Composable
fun <T> MyChipsSingleRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedIndex: Int,
    mapper: (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    onSelectionChange: (T) -> Unit
) {
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Single(selectedIndex) { selectedIndizes, selectedItems ->
            onSelectionChange(selectedItems)
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        showSelectionInfo = false
    )
}

@Composable
fun <T> MyChipsMultiRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    selected: MutableState<List<T>>,
    mapper: (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    showSelectionInfo: Boolean = true
) {
    val selectedIndizes = selected.value.map { items.indexOf(it) }
    MyChipsFlowRowImpl(
        modifier = modifier,
        items = items,
        selection = MyChipsRow.Selection.Multi(selectedIndizes) { selectedIndizes, selectedItems ->
            selected.value = selectedIndizes.map { items[it] }
        },
        mapper = mapper,
        minSegmentWidth = minSegmentWidth,
        color = color,
        showSelectionInfo = showSelectionInfo
    )
}

@Composable
fun <T> MyChipsMultiRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    selected: List<T>,
    mapper: (item: T) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    showSelectionInfo: Boolean = true,
    onSelectionChange: (List<T>) -> Unit
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
        showSelectionInfo = showSelectionInfo
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> MyChipsFlowRowImpl(
    modifier: Modifier = Modifier,
    items: List<T>,
    selection: MyChipsRow.Selection<T>,
    mapper: (item: T) -> String,
    minSegmentWidth: Dp,
    color: Color,
    showSelectionInfo: Boolean
) {
    val borderColor = color.takeIf { it != Color.Unspecified }?.disabled()
        ?: MaterialTheme.colorScheme.onSurface.disabled()

    val colorSelected =
        color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary
    val colorSelectedText = MaterialTheme.colorScheme.contentColorFor(colorSelected)

    val colorNotSelected = Color.Unspecified
    val colorNotSelectedText = Color.Unspecified

    val selectedIndices: List<Int> = when (selection) {
        is MyChipsRow.Selection.Multi -> selection.selected
        is MyChipsRow.Selection.Single -> selection.selected?.let { listOf(it) } ?: emptyList()
    }
    val onSelectionChange = when (selection) {
        is MyChipsRow.Selection.Multi -> { indices: List<Int> ->
            selection.onSelectionChange?.invoke(indices, indices.map { items[it] })
        }
        is MyChipsRow.Selection.Single -> { indices: List<Int> ->
                selection.onSelectionChange?.invoke(indices.first(), indices.first().let { items[it] })
        }
    }

    val empty = remember(selectedIndices) { derivedStateOf { selectedIndices.isEmpty() } }

    Column(
        modifier = modifier
    ) {
        if (showSelectionInfo && selection !is MyChipsRow.Selection.Single) {
            MyLabeledInformationHorizontal(
                modifier = Modifier.padding(bottom = LocalStyle.current.spacingSmall),
                label = "Selected",
                info = "${selectedIndices.size}/${items.size}"
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall),
            verticalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall, Alignment.CenterVertically)
        ) {
            if (selection !is MyChipsRow.Selection.Single) {
                val iconSize = 24.dp
                OutlinedButton(
                    modifier = Modifier.size(iconSize).align(Alignment.CenterVertically),
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
                        .widthIn(min =  minSegmentWidth)
                        .padding(LocalStyle.current.paddingSmall),
                    text = mapper(item),
                    maxLines = 1,
                    color = if (selectedIndices.contains(index)) colorSelectedText else colorNotSelectedText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
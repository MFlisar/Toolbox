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

@Composable
fun <T> MyMultiSegmentedControl(
    modifier: Modifier = Modifier,
    items: List<T>,
    selected: MutableState<List<T>>,
    mapper: (item: T) -> String,
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    showSelectionInfo: Boolean = true,
    onSelectionChange: ((List<T>) -> Unit)? = null
) {
    val texts = items.map { mapper(it) }
    val selectedIndizes = selected.value.map { items.indexOf(it) }
    MyMultiSegmentedControlImpl(
        modifier,
        texts,
        selectedIndizes,
        minSegmentWidth,
        color,
        showSelectionInfo
    ) { selectedIndizes, selectedItems ->
        selected.value = selectedIndizes.map { items[it] }
        onSelectionChange?.invoke(selected.value)
    }
}

@Composable
fun <T> MyMultiSegmentedControl(
    modifier: Modifier = Modifier,
    items: List<T>,
    selected: List<T>,
    mapper: (item: T) -> String,
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    showSelectionInfo: Boolean = true,
    onSelectionChange: (List<T>) -> Unit
) {
    val texts = items.map { mapper(it) }
    val selectedIndizes = selected.map { items.indexOf(it) }
    MyMultiSegmentedControlImpl(
        modifier,
        texts,
        selectedIndizes,
        minSegmentWidth,
        color,
        showSelectionInfo
    ) { selectedIndizes, selectedItems ->
        onSelectionChange(selectedIndizes.map { items[it] })
    }
}

@Composable
fun MyMultiSegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: MutableState<List<String>>,
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    showSelectionInfo: Boolean = true,
    onSelectionChange: ((List<String>) -> Unit)? = null
) {
    val texts = items
    val selectedIndizes = selected.value.map { items.indexOf(it) }
    MyMultiSegmentedControlImpl(
        modifier,
        texts,
        selectedIndizes,
        minSegmentWidth,
        color,
        showSelectionInfo
    ) { _, selectedItems ->
        selected.value = selectedItems
        onSelectionChange?.invoke(selected.value)
    }
}

@Composable
fun MyMultiSegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: List<String>,
    color: Color = Color.Unspecified,
    minSegmentWidth: Dp = 40.dp,
    showSelectionInfo: Boolean = true,
    onSelectionChange: (List<String>) -> Unit
) {
    val texts = items
    val selectedIndizes = selected.map { items.indexOf(it) }
    MyMultiSegmentedControlImpl(
        modifier,
        texts,
        selectedIndizes,
        minSegmentWidth,
        color,
        showSelectionInfo
    ) { _, selectedItems ->
        onSelectionChange(selectedItems)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MyMultiSegmentedControlImpl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: List<Int>,
    minSegmentWidth: Dp,
    color: Color,
    showSelectionInfo: Boolean,
    onSelectionChange: (indices: List<Int>, items: List<String>) -> Unit
) {
    val borderColor = color.takeIf { it != Color.Unspecified }?.disabled()
        ?: MaterialTheme.colorScheme.onSurface.disabled()

    val colorSelected =
        color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary
    val colorSelectedText = MaterialTheme.colorScheme.contentColorFor(colorSelected)

    val colorNotSelected = Color.Unspecified
    val colorNotSelectedText = Color.Unspecified

    val selectedItems = selected.map { items[it] }
    val empty = remember(selectedItems) { derivedStateOf { selectedItems.isEmpty() } }

    Column(
        modifier = modifier
    ) {
        if (showSelectionInfo) {
            MyLabeledInformationHorizontal(
                modifier = Modifier.padding(bottom = LocalStyle.current.spacingSmall),
                label = "Selected",
                info = "${selected.size}/${items.size}"
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall),
            verticalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall, Alignment.CenterVertically)
        ) {
            val iconSize = 24.dp
            OutlinedButton(
                modifier = Modifier.size(iconSize).align(Alignment.CenterVertically),
                onClick = {
                    onSelectionChange(emptyList(), emptyList())
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

            items.forEachIndexed { index, item ->
                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .border(1.dp, borderColor, MaterialTheme.shapes.small)
                        .background(if (selected.contains(index)) colorSelected else colorNotSelected)
                        .clickable {

                            val selectedNew = selected.toMutableList()
                            val selectedItemsNew = selectedItems.toMutableList()
                            if (selected.contains(index)) {
                                selectedNew.remove(index)
                                selectedItemsNew.remove(item)
                            } else {
                                selectedNew.add(index)
                                selectedItemsNew.add(item)
                            }

                            onSelectionChange(selectedNew, selectedItemsNew)
                        }
                        .widthIn(min =  minSegmentWidth)
                        .padding(LocalStyle.current.paddingSmall),
                    text = item,
                    maxLines = 1,
                    color = if (selected.contains(index)) colorSelectedText else colorNotSelectedText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
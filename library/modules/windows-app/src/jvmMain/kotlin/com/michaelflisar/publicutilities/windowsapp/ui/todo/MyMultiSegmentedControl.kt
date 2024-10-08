package com.michaelflisar.publicutilities.windowsapp.ui.todo

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.publicutilities.windowsapp.disabled

@Composable
fun <T> MyMultiSegmentedControl(
    modifier: Modifier = Modifier,
    items: List<T>,
    selected: MutableState<List<T>>,
    mapper: (item: T) -> String,
    color: Color = Color.Unspecified,
    showSelectionInfo: Boolean = true,
    onSelectionChange: ((List<T>) -> Unit)? = null
) {
    val texts = items.map { mapper(it) }
    val selectedIndizes = selected.value.map { items.indexOf(it) }
    MyMultiSegmentedControl(
        modifier,
        texts,
        selectedIndizes,
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
    color: Color = Color.Unspecified,
    showSelectionInfo: Boolean = true,
    onSelectionChange: (List<T>) -> Unit
) {
    val texts = items.map { mapper(it) }
    val selectedIndizes = selected.map { items.indexOf(it) }
    MyMultiSegmentedControl(
        modifier,
        texts,
        selectedIndizes,
        color,
        showSelectionInfo
    ) { selectedIndizes, selectedItems ->
        onSelectionChange(selectedIndizes.map { items[it] })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MyMultiSegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: List<Int>,
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

    var selectedItems = selected.map { items[it] }
    val empty = remember(selectedItems) { derivedStateOf { selectedItems.isEmpty() } }

    Column(
        modifier = modifier
    ) {
        if (showSelectionInfo) {
            MyLabeledInformationHorizontal(
                modifier = Modifier.padding(bottom = 4.dp),
                label = "Selected",
                info = "${selected.size}/${items.size}"
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val iconSize = 30.dp
            OutlinedButton(
                modifier = Modifier.size(iconSize),
                onClick = {
                    onSelectionChange(emptyList(), emptyList())
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (empty.value) Color.Transparent else MaterialTheme.colorScheme.background
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
                        .widthIn(min = 40.dp)
                        .padding(4.dp),
                    text = item,
                    maxLines = 1,
                    color = if (selected.contains(index)) colorSelectedText else colorNotSelectedText
                )
            }
        }
    }
}
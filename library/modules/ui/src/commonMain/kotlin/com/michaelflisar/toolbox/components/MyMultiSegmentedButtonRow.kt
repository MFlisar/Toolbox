package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.spacing


@Composable
fun <T> MyMultiSegmentedButtonRow(
    items: List<T>,
    selected: MutableState<List<T>>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    showSelectionInfo: Boolean = true,
    onSelectionChange: ((List<T>) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    val selectedIndizes = selected.value.map { items.indexOf(it) }
    MyMultiSegmentedButtonRowImpl(
        modifier = modifier,
        items = texts,
        selected = selectedIndizes,
        color = color,
        onColor = onColor,
        showSelectionInfo = showSelectionInfo
    ) { selectedIndizes, selectedItems ->
        selected.value = selectedIndizes.map { items[it] }
        onSelectionChange?.invoke(selected.value)
    }
}

@Composable
fun <T> MyMultiSegmentedButtonRow(
    items: List<T>,
    selected: List<T>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    showSelectionInfo: Boolean = true,
    onSelectionChange: (List<T>) -> Unit,
) {
    val texts = items.map { mapper(it) }
    val selectedIndizes = selected.map { items.indexOf(it) }
    MyMultiSegmentedButtonRowImpl(
        modifier = modifier,
        items = texts,
        selected = selectedIndizes,
        color = color,
        onColor = onColor,
        showSelectionInfo = showSelectionInfo
    ) { selectedIndizes, selectedItems ->
        onSelectionChange(selectedIndizes.map { items[it] })
    }
}

@Composable
fun <T> MyMultiSegmentedButtonRowIndex(
    items: List<T>,
    selectedIndizes: MutableState<List<Int>>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    showSelectionInfo: Boolean = true,
    onSelectionChange: ((List<Int>) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    MyMultiSegmentedButtonRowImpl(
        modifier = modifier,
        items = texts,
        selected = selectedIndizes.value,
        color = color,
        onColor = onColor,
        showSelectionInfo = showSelectionInfo
    ) { selectedIndizes2, selectedItems ->
        selectedIndizes.value = selectedIndizes2
        onSelectionChange?.invoke(selectedIndizes2)
    }
}

@Composable
fun <T> MyMultiSegmentedButtonRowIndex(
    items: List<T>,
    selectedIndizes: List<Int>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    showSelectionInfo: Boolean = true,
    onSelectionChange: (List<Int>) -> Unit,
) {
    val texts = items.map { mapper(it) }
    MyMultiSegmentedButtonRowImpl(
        modifier = modifier,
        items = texts,
        selected = selectedIndizes,
        color = color,
        onColor = onColor,
        showSelectionInfo = showSelectionInfo
    ) { selectedIndizes, selectedItems ->
        onSelectionChange.invoke(selectedIndizes)
    }
}

@Composable
private fun MyMultiSegmentedButtonRowImpl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: List<Int>,
    color: Color,
    onColor: Color,
    showSelectionInfo: Boolean,
    onSelectionChange: (indices: List<Int>, items: List<String>) -> Unit,
) {
    val colorSelected =
        color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary

    val selectedItems = selected.map { items[it] }

    Column(
        modifier = modifier
    ) {
        if (showSelectionInfo) {
            MyLabeledInformationHorizontal(
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.small),
                label = "Selected",
                info = "${selected.size}/${items.size}"
            )
        }
        MultiChoiceSegmentedButtonRow(
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
                    onCheckedChange = {
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
                    },
                    checked = index in selected
                ) {
                    Text(item)
                }
            }
        }
    }
}
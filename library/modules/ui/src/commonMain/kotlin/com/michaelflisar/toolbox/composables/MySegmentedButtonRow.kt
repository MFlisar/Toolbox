package com.michaelflisar.toolbox.composables

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
    modifier: Modifier = Modifier,
    items: List<T & Any>,
    selected: MutableState<T>,
    mapper: (item: T & Any) -> String = { it.toString() },
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    onSelectionChanged: ((T) -> Unit)? = null
) {
    val texts = items.map { mapper(it) }
    val selectedIndex = items.indexOf(selected.value)
    MySegmentedButtonRowImpl(modifier, texts, selectedIndex, color, onColor) { index, item ->
        val s = (if (index >= 0) items[index] else null) as T
        selected.value = s
        onSelectionChanged?.invoke(s)
    }
}

@Composable
fun <T> MySegmentedButtonRow(
    modifier: Modifier = Modifier,
    items: List<T & Any>,
    selected: T,
    mapper: (item: T & Any) -> String = { it.toString() },
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    onSelectionChanged: ((T) -> Unit)? = null
) {
    val texts = items.map { mapper(it) }
    val selectedIndex = items.indexOf(selected)
    MySegmentedButtonRowImpl(modifier, texts, selectedIndex, color, onColor) { index, item ->
        onSelectionChanged?.invoke(items[index])
    }
}

// Sonderfall: Int Daten (index) + String Werte
@Composable
fun MySegmentedButtonRow(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: MutableState<Int>,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    onSelectionChanged: ((Int) -> Unit)? = null
) {
    val selectedIndex = selected.value
    MySegmentedButtonRowImpl(modifier, items, selectedIndex, color, onColor) { index, item ->
        selected.value = index
        onSelectionChanged?.invoke(index)
    }
}

@Composable
fun MySegmentedButtonRow(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: Int,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    onSelectionChanged: ((Int) -> Unit)? = null
) {
    MySegmentedButtonRowImpl(modifier, items, selected, color, onColor) { index, item ->
        onSelectionChanged?.invoke(index)
    }
}

@Composable
private fun MySegmentedButtonRowImpl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: Int,
    color: Color,
    onColor: Color,
    onSelectionChange: (index: Int, item: String?) -> Unit
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
                    if (selected == index)
                        onSelectionChange(-1, null)
                    else
                        onSelectionChange(index, item)
                },
                selected = index == selected
            ) {
                Text(item)
            }
        }
    }
}
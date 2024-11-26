package com.michaelflisar.toolbox.table.definitions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.composables.MyDropdown
import com.michaelflisar.toolbox.composables.MyInput
import com.michaelflisar.toolbox.composables.MyMultiDropdown
import com.michaelflisar.toolbox.composables.MyNumericInput
import com.michaelflisar.toolbox.composables.MySegmentedControl

sealed class Filter<Item, CellValue> {

    abstract val state: MutableState<*>
    abstract fun isValid(item: Item, itemToValue: (item: Item) -> CellValue): Boolean
    abstract fun isActive(): Boolean

    @Composable abstract fun render()

    class Text<Item>(
        val filter: (value: String, filter: String) -> Boolean = { value, filter ->
            filter.isEmpty() || value.contains(filter, true)
        }
    ) : Filter<Item, String>() {
        override val state = mutableStateOf("")
        override fun isValid(item: Item, itemToValue: (item: Item) -> String) = filter(itemToValue(item), state.value)
        override fun isActive() = state.value.isNotEmpty()

        @Composable
        override fun render() {
            val focusRequester = remember { FocusRequester() }
            MyInput(
                //title = "Filter",
                modifier = Modifier
                    .padding(start = 32.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = state.value,
                onValueChange = {
                    state.value = it
                }
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }

    class Number<Item, T>(
        val filter: (value: T, filter: T?) -> Boolean = { value, filter ->
            filter == null || value == filter
        }
    ) : Filter<Item, T>() where T: kotlin.Number {
        override val state = mutableStateOf<T?>(null)
        override fun isValid(item: Item, itemToValue: (item: Item) -> T) = filter(itemToValue(item), state.value)
        override fun isActive() = state.value != null

        @Composable
        override fun render() {
            val focusRequester = remember { FocusRequester() }
            MyNumericInput(
                title = "",
                modifier = Modifier
                    .padding(start = 32.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = state.value,
                onValueChange = {
                    state.value = it
                }
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }

    class List<Item, T>(
        val items: kotlin.collections.List<T>,
        val mapper: (T) -> String,
        val filter: (value: T, filter: kotlin.collections.List<T>) -> Boolean = { value, filter ->
            filter.isEmpty() || filter.contains(value)
        },
        val multiSelect: Boolean = false,
        val labelAll: String = "ALL"
    ) : Filter<Item, T>() {
        override val state = mutableStateOf(emptyList<T>())
        override fun isValid(item: Item, itemToValue: (item: Item) -> T) = filter(itemToValue(item), state.value)
        override fun isActive() = state.value.isNotEmpty()

        @Composable
        override fun render() {
            if (multiSelect) {
                MyMultiDropdown(
                    title = "",
                    items = items,
                    selected = state.value,
                    mapper = mapper,
                    onSelectionChange = {
                        state.value = it
                    }
                )
            } else {
                val texts = items.map { mapper(it) }
                MyDropdown(
                    title = "",
                    items = listOf(labelAll) + texts,
                    selected = state.value.firstOrNull()?.let { items.indexOf(it) + 1 } ?: 0,
                    onSelectionChanged = {
                        state.value = it.takeIf { it > 0 }?.let { listOf(items[it - 1]) } ?: emptyList()
                    }
                )
            }

        }
    }

    class Checkmark<Item>(
        val labelAll: String = "ALL",
        val labelChecked: String = "Checked",
        val labelUnchecked: String = "Unchecked"
    ) : Filter<Item, Boolean>() {
        override val state = mutableStateOf<Boolean?>(null)
        override fun isValid(item: Item, itemToValue: (item: Item) -> Boolean) = state.value == null || state.value == itemToValue(item)
        override fun isActive() = state.value != null

        @Composable
        override fun render() {
            MySegmentedControl(
                items = listOf(labelAll, labelChecked, labelUnchecked),
                selected = when (state.value) {
                    true -> 1
                    false -> 2
                    null -> 0
                },
                onSelectionChanged = {
                    when (it) {
                       1 -> state.value = true
                       2 -> state.value = false
                       else -> state.value = null
                    }
                }
            )
        }
    }
}
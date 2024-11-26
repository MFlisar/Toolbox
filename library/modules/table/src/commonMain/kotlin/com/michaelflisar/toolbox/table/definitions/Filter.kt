package com.michaelflisar.toolbox.table.definitions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.composables.MyDropdown
import com.michaelflisar.toolbox.composables.MyInput
import com.michaelflisar.toolbox.composables.MyMultiDropdown
import com.michaelflisar.toolbox.composables.MyNumericInput
import com.michaelflisar.toolbox.composables.MySegmentedControl
import kotlin.enums.EnumEntries

abstract class Filter<Item, CellValue> {

    abstract val state: MutableState<*>
    abstract fun isValid(item: Item, itemToValue: (item: Item) -> CellValue): Boolean
    abstract fun isActive(): Boolean
    abstract fun clear()

    @Composable
    abstract fun render()

    class TextData<Item, CellValue>(
        val cellValueToString: (value: CellValue) -> String,
        val filter: (value: String, filter: String) -> Boolean = { value, filter ->
            filter.isEmpty() || value.contains(filter, true)
        }
    ) : Filter<Item, CellValue>() {

        override val state = mutableStateOf("")
        override fun isValid(item: Item, itemToValue: (item: Item) -> CellValue) =
            filter(cellValueToString(itemToValue(item)), state.value)

        override fun isActive() = state.value.isNotEmpty()
        override fun clear() {
            state.value = ""
        }

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

    class Text<Item>(
        val filter: (value: String, filter: String) -> Boolean = { value, filter ->
            filter.isEmpty() || value.contains(filter, true)
        }
    ) : Filter<Item, String>() {
        override val state = mutableStateOf("")
        override fun isValid(item: Item, itemToValue: (item: Item) -> String) =
            filter(itemToValue(item), state.value)

        override fun isActive() = state.value.isNotEmpty()
        override fun clear() {
            state.value = ""
        }

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

    class Number<Item, CellValue>(
        val filter: (value: CellValue, filter: State<CellValue>) -> Boolean = { value, filter ->
            if (filter.value == null)
                true
            else {
                val valid: Boolean = when (filter.type) {
                    State.Type.Equal -> value == filter.value
                    State.Type.LargerOrEqual -> {
                        when (value) {
                            is Double -> (value as Double) >= (filter.value as Double)
                            is Float -> (value as Float) >= (filter.value as Float)
                            is Int -> (value as Int) >= (filter.value as Int)
                            is Long -> (value as Long) >= (filter.value as Long)
                            is Short -> (value as Short) >= (filter.value as Short)
                            is Byte -> (value as Byte) >= (filter.value as Byte)
                            else -> throw RuntimeException("Type not handled!")
                        }
                    }

                    State.Type.Larger -> {
                        when (value) {
                            is Double -> (value as Double) > (filter.value as Double)
                            is Float -> (value as Float) > (filter.value as Float)
                            is Int -> (value as Int) > (filter.value as Int)
                            is Long -> (value as Long) > (filter.value as Long)
                            is Short -> (value as Short) > (filter.value as Short)
                            is Byte -> (value as Byte) > (filter.value as Byte)
                            else -> throw RuntimeException("Type not handled!")
                        }
                    }

                    State.Type.SmallerOrEqual -> {
                        when (value) {
                            is Double -> (value as Double) <= (filter.value as Double)
                            is Float -> (value as Float) <= (filter.value as Float)
                            is Int -> (value as Int) <= (filter.value as Int)
                            is Long -> (value as Long) <= (filter.value as Long)
                            is Short -> (value as Short) <= (filter.value as Short)
                            is Byte -> (value as Byte) <= (filter.value as Byte)
                            else -> throw RuntimeException("Type not handled!")
                        }
                    }

                    State.Type.Smaller -> {
                        when (value) {
                            is Double -> (value as Double) < (filter.value as Double)
                            is Float -> (value as Float) < (filter.value as Float)
                            is Int -> (value as Int) < (filter.value as Int)
                            is Long -> (value as Long) < (filter.value as Long)
                            is Short -> (value as Short) < (filter.value as Short)
                            is Byte -> (value as Byte) < (filter.value as Byte)
                            else -> throw RuntimeException("Type not handled!")
                        }
                    }
                }

                valid
            }
        }
    ) : Filter<Item, CellValue>() where CellValue : kotlin.Number {

        data class State<CellValue>(
            val value: CellValue? = null,
            val type: Type = Type.Equal
        ) {
            enum class Type(val label: String) {
                Equal("=="),
                LargerOrEqual(">="),
                Larger(">"),
                SmallerOrEqual("<="),
                Smaller("<")
            }
        }

        override val state = mutableStateOf(State<CellValue>())
        override fun isValid(item: Item, itemToValue: (item: Item) -> CellValue) =
            filter(itemToValue(item), state.value)

        override fun isActive() = state.value.value != null
        override fun clear() {
            state.value = state.value.copy(value = null)
        }

        @Composable
        override fun render() {
            val focusRequester = remember { FocusRequester() }
            Row(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MyDropdown(
                    modifier = Modifier.width(64.dp),
                    title = "",
                    items = State.Type.entries,
                    selected = state.value.type,
                    mapper = { it.label },
                    onSelectionChanged = {
                        state.value = state.value.copy(type = it)
                    }
                )
                MyNumericInput(
                    title = "",
                    modifier = Modifier.weight(1f)
                        .focusRequester(focusRequester),
                    value = state.value.value,
                    onValueChange = {
                        state.value = state.value.copy(value = it)
                    }
                )
            }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }

    class List<Item, CellValue>(
        val items: kotlin.collections.List<CellValue>,
        val mapper: (CellValue) -> String,
        val filter: (value: CellValue, filter: kotlin.collections.List<CellValue>) -> Boolean = { value, filter ->
            filter.isEmpty() || filter.contains(value)
        },
        val multiSelect: Boolean = false,
        val labelAll: String = "ALL"
    ) : Filter<Item, CellValue>() {

        override val state = mutableStateOf(emptyList<CellValue>())
        override fun isValid(item: Item, itemToValue: (item: Item) -> CellValue) =
            filter(itemToValue(item), state.value)

        override fun isActive() = state.value.isNotEmpty()
        override fun clear() {
            state.value = emptyList()
        }

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
                        state.value =
                            it.takeIf { it > 0 }?.let { listOf(items[it - 1]) } ?: emptyList()
                    }
                )
            }

        }
    }

    class Enum<Item, CellValue : kotlin.Enum<CellValue>>(
        val items: EnumEntries<CellValue>,
        val filter: (value: CellValue, filter: kotlin.collections.List<CellValue>) -> Boolean = { value, filter ->
            filter.isEmpty() || filter.contains(value)
        },
        val multiSelect: Boolean = false,
        val labelAll: String = "ALL"
    ) : Filter<Item, CellValue>() {

        override val state = mutableStateOf(emptyList<CellValue>())
        override fun isValid(item: Item, itemToValue: (item: Item) -> CellValue) =
            filter(itemToValue(item), state.value)

        override fun isActive() = state.value.isNotEmpty()
        override fun clear() {
            state.value = emptyList()
        }

        @Composable
        override fun render() {
            if (multiSelect) {
                MyMultiDropdown(
                    title = "",
                    items = items,
                    selected = state.value,
                    mapper = { it.name },
                    onSelectionChange = {
                        state.value = it
                    }
                )
            } else {
                val texts = items.map { it.name }
                MyDropdown(
                    title = "",
                    items = listOf(labelAll) + texts,
                    selected = state.value.firstOrNull()?.let { items.indexOf(it) + 1 } ?: 0,
                    onSelectionChanged = {
                        state.value =
                            it.takeIf { it > 0 }?.let { listOf(items[it - 1]) } ?: emptyList()
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
        override fun isValid(item: Item, itemToValue: (item: Item) -> Boolean) =
            state.value == null || state.value == itemToValue(item)

        override fun isActive() = state.value != null
        override fun clear() {
            state.value = null
        }

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
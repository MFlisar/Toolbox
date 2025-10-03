package com.michaelflisar.toolbox.table.definitions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyMultiDropdown
import com.michaelflisar.toolbox.components.MyNumericInput
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.components.MySegmentedControl
import com.michaelflisar.toolbox.components.MyTooltipBox
import com.michaelflisar.toolbox.extensions.disabled
import kotlin.enums.EnumEntries

abstract class Filter<Item, CellValue> {

    sealed class Style {
        class HeaderPopup(val text: String = "Filter") : Style()
        class ChipPopup(val text: String) : Style()
    }

    abstract val state: MutableState<*>
    abstract fun isValid(item: Item, itemToValue: (item: Item) -> CellValue): Boolean
    abstract fun isActive(): Boolean
    abstract fun clear()
    abstract fun info(): String

    enum class FilterType(
        val label: String,
        val info: String,
        val numeric: Boolean = false
    ) {
        Equal("==", "Equal"),
        NotEqual("!=", "Not Equal"),

        // NUMERIC ONLY
        LargerOrEqual(">=", "Greater Or Equal", true),
        Larger(">", "Greater", true),
        SmallerOrEqual("<=", "Smaller Or Equal", true),
        Smaller("<", "Smaller", true),

        StartsWith("|_", "Starts With"),
        EndsWith("_|", "Ends With"),
        Contains("⊂", "Contains"),
        ContainsNot("⊄", "Not Contains")
        ;

        val longLabel: String
            get() = "${label.padEnd(2, ' ')} $info"
    }

    @Composable
    fun Render(
        style: Style
    ) {
        when (style) {
            is Style.HeaderPopup -> {
                MyRow {
                    Icon(Icons.Default.FilterAlt, null)
                    Text(modifier = Modifier.weight(1f), text = style.text, fontWeight = FontWeight.Bold)
                    header()
                }
                Column(
                    modifier = Modifier
                        .padding(start = 32.dp)
                        .fillMaxWidth()
                ) {
                    content()
                }
            }
            is Style.ChipPopup -> {
                MyRow {
                    Icon(Icons.Default.FilterAlt, null)
                    Text(text = style.text, fontWeight = FontWeight.Bold)
                }
                MyRow {
                    header()
                    Column {
                        content()
                    }
                }
            }
        }
    }

    @Composable
    open fun RowScope.header() {
    }

    @Composable
    abstract fun ColumnScope.content()

    class Text<Item, CellValue>(
        val cellValueToString: (value: CellValue) -> String = { it.toString() },
        val filter: (value: String, filter: State) -> Boolean = { value, filter ->
            if (filter.value.isEmpty())
                true
            else {
                when (filter.type) {
                    FilterType.Equal -> value.equals(filter.value, filter.ignoreCase)
                    FilterType.NotEqual -> !value.equals(filter.value, filter.ignoreCase)
                    FilterType.StartsWith -> value.startsWith(filter.value, filter.ignoreCase)
                    FilterType.EndsWith -> value.endsWith(filter.value, filter.ignoreCase)
                    FilterType.Contains -> value.contains(filter.value, filter.ignoreCase)
                    FilterType.ContainsNot -> !value.contains(filter.value, filter.ignoreCase)
                    // numeric => not possible!
                    FilterType.LargerOrEqual,
                    FilterType.Larger,
                    FilterType.SmallerOrEqual,
                    FilterType.Smaller -> throw RuntimeException("Type not valid!")
                }
            }
        },
        initial: String = ""
    ) : Filter<Item, CellValue>() {

        data class State(
            val value: String = "",
            val ignoreCase: Boolean = true,
            val type: FilterType = FilterType.Contains
        )

        override val state = mutableStateOf(State(initial))
        override fun isValid(item: Item, itemToValue: (item: Item) -> CellValue) =
            filter(cellValueToString(itemToValue(item)), state.value)

        override fun isActive() = state.value.value.isNotEmpty()
        override fun clear() {
            state.value = state.value.copy(value = "")
        }

        override fun info(): String {
            if (!isActive()) {
                return ""
            }
            return "${state.value.type.label} ${state.value.value}"
        }

        @Composable
        override fun RowScope.header() {
            MyDropdown(
                modifier = Modifier.width(64.dp),
                title = "",
                items = FilterType.entries.filter { !it.numeric },
                selected = state.value.type,
                mapper = { item, dropwdown -> if (dropwdown) item.longLabel else item.label },
                onSelectionChanged = {
                    state.value = state.value.copy(type = it)
                }
            )

            MyTooltipBox("Case Sensitive") {
                MyIconButton(
                    icon = Icons.Default.TextFields,
                    tint = if (!state.value.ignoreCase) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.disabled()
                ) {
                    state.value = state.value.copy(ignoreCase = !state.value.ignoreCase)
                }
            }
        }

        @Composable
        override fun ColumnScope.content() {
            val focusRequester = remember { FocusRequester() }
            //MyCheckbox(
            //    modifier = Modifier
            //        .fillMaxWidth(),
            //    title = "Case Sensitive",
            //    checked = !state.value.ignoreCase,
            //    onCheckedChange = {
            //        state.value = state.value.copy(ignoreCase = !it)
            //    }
            //)
            MyInput(
                //title = "Filter",
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = state.value.value,
                onValueChanged = {
                    state.value = state.value.copy(value = it)
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
                    FilterType.Equal -> value == filter.value
                    FilterType.NotEqual -> value != filter.value
                    FilterType.LargerOrEqual -> {
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

                    FilterType.Larger -> {
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

                    FilterType.SmallerOrEqual -> {
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

                    FilterType.Smaller -> {
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

                    FilterType.Contains -> value.toString().contains(filter.value.toString())
                    FilterType.ContainsNot -> !value.toString().contains(filter.value.toString())
                    FilterType.StartsWith -> value.toString().startsWith(filter.value.toString())
                    FilterType.EndsWith -> value.toString().endsWith(filter.value.toString())

                }

                valid
            }
        },
        initial: CellValue? = null
    ) : Filter<Item, CellValue>() where CellValue : kotlin.Number {

        data class State<CellValue>(
            val value: CellValue? = null,
            val type: FilterType = FilterType.Contains
        )

        override val state = mutableStateOf(State<CellValue>(initial))
        override fun isValid(item: Item, itemToValue: (item: Item) -> CellValue) =
            filter(itemToValue(item), state.value)

        override fun isActive() = state.value.value != null
        override fun clear() {
            state.value = state.value.copy(value = null)
        }

        override fun info(): String {
            if (!isActive()) {
                return ""
            }
            return "${state.value.type.label} ${state.value.value}"
        }

        @Composable
        override fun RowScope.header() {
            MyDropdown(
                modifier = Modifier.width(64.dp),
                title = "",
                items = FilterType.entries,
                selected = state.value.type,
                mapper = { item, dropwdown -> if (dropwdown) item.longLabel else item.label },
                onSelectionChanged = {
                    state.value = state.value.copy(type = it)
                }
            )
        }

        @Composable
        override fun ColumnScope.content() {
            val focusRequester = remember { FocusRequester() }
            MyNumericInput(
                title = "",
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                value = state.value.value,
                onValueChanged = {
                    state.value = state.value.copy(value = it)
                }
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }

    class List<Item, CellValue>(
        val items: kotlin.collections.List<CellValue>,
        val mapper: (CellValue) -> String = { it.toString() },
        val filter: (value: CellValue, filter: kotlin.collections.List<CellValue>) -> Boolean = { value, filter ->
            filter.isEmpty() || filter.contains(value)
        },
        val multiSelect: Boolean = false,
        val labelAll: String = "ALL",
        initial: kotlin.collections.List<CellValue> = emptyList()
    ) : Filter<Item, CellValue>() {

        override val state = mutableStateOf(initial)
        override fun isValid(item: Item, itemToValue: (item: Item) -> CellValue) =
            filter(itemToValue(item), state.value)

        override fun isActive() = state.value.isNotEmpty()
        override fun clear() {
            state.value = emptyList()
        }

        override fun info(): String {
            if (!isActive()) {
                return ""
            }
            return state.value.joinToString(", ") { mapper(it) }
        }

        @Composable
        override fun ColumnScope.content() {
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
                    selectedIndex = state.value.firstOrNull()?.let { items.indexOf(it) + 1 } ?: 0,
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
        val mapper: (CellValue) -> String = { it.name },
        val filter: (value: CellValue, filter: kotlin.collections.List<CellValue>) -> Boolean = { value, filter ->
            filter.isEmpty() || filter.contains(value)
        },
        val multiSelect: Boolean = false,
        val labelAll: String = "ALL",
        initial: kotlin.collections.List<CellValue> = emptyList()
    ) : Filter<Item, CellValue>() {

        override val state = mutableStateOf(initial)
        override fun isValid(item: Item, itemToValue: (item: Item) -> CellValue) =
            filter(itemToValue(item), state.value)

        override fun isActive() = state.value.isNotEmpty()
        override fun clear() {
            state.value = emptyList()
        }

        override fun info(): String {
            if (!isActive()) {
                return ""
            }
            return state.value.joinToString(", ") { mapper(it) }
        }

        @Composable
        override fun ColumnScope.content() {
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
                    selectedIndex = state.value.firstOrNull()?.let { items.indexOf(it) + 1 } ?: 0,
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
        val labelUnchecked: String = "Unchecked",
        initial: Boolean? = null
    ) : Filter<Item, Boolean>() {
        override val state = mutableStateOf<Boolean?>(initial)
        override fun isValid(item: Item, itemToValue: (item: Item) -> Boolean) =
            state.value == null || state.value == itemToValue(item)

        override fun isActive() = state.value != null
        override fun clear() {
            state.value = null
        }

        override fun info(): String {
            if (!isActive()) {
                return ""
            }
            return when (state.value) {
                true -> labelChecked
                false -> labelUnchecked
                null -> labelAll
            }
        }

        @Composable
        override fun ColumnScope.content() {
            MySegmentedControl(
                items = listOf(labelAll, labelChecked, labelUnchecked),
                selectedIndex = when (state.value) {
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
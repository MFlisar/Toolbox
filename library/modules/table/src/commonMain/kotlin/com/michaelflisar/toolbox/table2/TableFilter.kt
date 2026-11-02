package com.michaelflisar.toolbox.table2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class TableFilterPresentation {
    /** Edits are committed only with Done; dismissal discards them. */
    Dialog,
    /** Anchored editor; valid edits take effect immediately. */
    Popup
}

/** Holds uncommitted input, including invalid values, independently of the active query. */
internal class TableFilterDraft(
    private val capability: TableFilter<*>,
    initialValue: TableFilterValue?
) {
    var value by mutableStateOf(initialValue)
        private set
    val valid: Boolean get() = value == null || capability.accepts(requireNotNull(value))
    val canClear: Boolean get() = when (val current = value) {
        null -> false
        is TableFilterValue.Text -> current != TableFilterValue.Text("")
        is TableFilterValue.Number -> current != TableFilterValue.Number("")
        is TableFilterValue.Choices -> current.keys.isNotEmpty()
        else -> true
    }
    fun change(value: TableFilterValue?) { this.value = value }
    fun edit(value: TableFilterValue?, presentation: TableFilterPresentation, onCommit: (TableFilterValue?) -> Unit) {
        change(value)
        if (presentation == TableFilterPresentation.Popup && valid) commit(onCommit)
    }
    fun commit(onCommit: (TableFilterValue?) -> Unit) {
        check(valid) { "Invalid filter draft" }
        onCommit(when (val current = value) {
            is TableFilterValue.Text -> current.takeIf { it.text.isNotEmpty() }
            is TableFilterValue.Number -> current.takeIf { it.text.isNotEmpty() }
            is TableFilterValue.Choices -> current.takeIf { it.keys.isNotEmpty() }
            else -> current
        })
    }
}

enum class TableFilterOperator {
    Equal, NotEqual, GreaterOrEqual, Greater, LessOrEqual, Less,
    StartsWith, EndsWith, Contains, NotContains
}

sealed interface TableFilterValue {
    data class Text(
        val text: String,
        val operator: TableFilterOperator = TableFilterOperator.Contains,
        val ignoreCase: Boolean = true
    ) : TableFilterValue
    /** Keep the numeric input as text: Long values must not lose precision through Double. */
    data class Number(
        val text: String,
        val operator: TableFilterOperator = TableFilterOperator.Equal
    ) : TableFilterValue
    data class Choices(val keys: Set<String>) : TableFilterValue
    data class BooleanValue(val value: Boolean) : TableFilterValue
    data class Custom(val type: String, val fields: Map<String, String>) : TableFilterValue
}

/** Stateless filter capability; the active value belongs to TableState. */
sealed class TableFilter<Value> {
    abstract fun accepts(value: TableFilterValue): Boolean
    abstract fun matches(value: Value, filter: TableFilterValue): Boolean

    class Text<Value>(val text: (Value) -> String) : TableFilter<Value>() {
        override fun accepts(value: TableFilterValue) =
            value is TableFilterValue.Text && value.operator in textOperators
        override fun matches(value: Value, filter: TableFilterValue): Boolean {
            require(accepts(filter)) { "Invalid text filter" }
            filter as TableFilterValue.Text
            return filter.text.isEmpty() || matchText(text(value), filter.text, filter.operator, filter.ignoreCase)
        }
    }

    class Number<Value : Comparable<Value>>(
        val parse: (String) -> Value?,
        val text: (Value) -> String = { it.toString() }
    ) : TableFilter<Value>() {
        override fun accepts(value: TableFilterValue) =
            value is TableFilterValue.Number && (value.text.isEmpty() || parse(value.text) != null)
        override fun matches(value: Value, filter: TableFilterValue): Boolean {
            require(accepts(filter)) { "Invalid numeric filter" }
            filter as TableFilterValue.Number
            if (filter.text.isEmpty()) return true
            if (filter.operator in stringOnlyOperators) {
                return matchText(text(value), filter.text, filter.operator, false)
            }
            val other = requireNotNull(parse(filter.text))
            val comparison = value.compareTo(other)
            return when (filter.operator) {
                TableFilterOperator.Equal -> comparison == 0
                TableFilterOperator.NotEqual -> comparison != 0
                TableFilterOperator.GreaterOrEqual -> comparison >= 0
                TableFilterOperator.Greater -> comparison > 0
                TableFilterOperator.LessOrEqual -> comparison <= 0
                TableFilterOperator.Less -> comparison < 0
                else -> error("Invalid numeric operator")
            }
        }
    }

    class Choice<Value>(
        val options: List<Value>,
        val key: (Value) -> String,
        val label: (Value) -> String = { it.toString() },
        val multiSelect: Boolean = false
    ) : TableFilter<Value>() {
        init { require(options.map(key).distinct().size == options.size) { "Choice keys must be unique" } }
        override fun accepts(value: TableFilterValue) = value is TableFilterValue.Choices &&
            (multiSelect || value.keys.size <= 1) && value.keys.all { selected -> options.any { key(it) == selected } }
        override fun matches(value: Value, filter: TableFilterValue): Boolean {
            require(accepts(filter)) { "Invalid choice filter" }
            filter as TableFilterValue.Choices
            return filter.keys.isEmpty() || key(value) in filter.keys
        }
        internal fun labels(): Map<String, String> = options.associate { key(it) to label(it) }
    }

    class BooleanFilter : TableFilter<Boolean>() {
        override fun accepts(value: TableFilterValue) = value is TableFilterValue.BooleanValue
        override fun matches(value: Boolean, filter: TableFilterValue): Boolean {
            require(filter is TableFilterValue.BooleanValue)
            return value == filter.value
        }
    }

    /** Custom predicates/editors still produce structured values usable by an external provider. */
    class Custom<Value>(
        val type: String,
        val validate: (Map<String, String>) -> Boolean = { true },
        val predicate: (Value, Map<String, String>) -> Boolean,
        val summary: (Map<String, String>) -> String,
        val content: @Composable TableCustomFilterScope.() -> Unit
    ) : TableFilter<Value>() {
        init { require(type.isNotBlank()) }
        override fun accepts(value: TableFilterValue) =
            value is TableFilterValue.Custom && value.type == type && validate(value.fields)
        override fun matches(value: Value, filter: TableFilterValue): Boolean {
            require(accepts(filter)) { "Invalid custom filter" }
            return predicate(value, (filter as TableFilterValue.Custom).fields)
        }
    }
}

class TableCustomFilterScope internal constructor(
    val value: TableFilterValue.Custom?,
    val texts: TableTexts,
    private val onChange: (Map<String, String>?) -> Unit
) {
    fun setValue(fields: Map<String, String>?) = onChange(fields)
}

fun textFilter(): TableFilter<String> = TableFilter.Text { it }
fun <Value> textFilter(text: (Value) -> String): TableFilter<Value> = TableFilter.Text(text)
fun <Value : Comparable<Value>> numberFilter(parse: (String) -> Value?): TableFilter<Value> =
    TableFilter.Number(parse)

internal val stringOnlyOperators = setOf(
    TableFilterOperator.StartsWith, TableFilterOperator.EndsWith,
    TableFilterOperator.Contains, TableFilterOperator.NotContains
)
internal val textOperators = setOf(TableFilterOperator.Equal, TableFilterOperator.NotEqual) + stringOnlyOperators

private fun matchText(value: String, filter: String, operator: TableFilterOperator, ignoreCase: Boolean): Boolean =
    when (operator) {
        TableFilterOperator.Equal -> value.equals(filter, ignoreCase)
        TableFilterOperator.NotEqual -> !value.equals(filter, ignoreCase)
        TableFilterOperator.StartsWith -> value.startsWith(filter, ignoreCase)
        TableFilterOperator.EndsWith -> value.endsWith(filter, ignoreCase)
        TableFilterOperator.Contains -> value.contains(filter, ignoreCase)
        TableFilterOperator.NotContains -> !value.contains(filter, ignoreCase)
        else -> error("Not a text operator: $operator")
    }

package com.michaelflisar.toolbox.table2

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Shared layout defaults used by all column factories. */
object TableColumnDefaults {
    val Width: ColumnWidth = ColumnWidth.Weight()
    val Padding: PaddingValues = PaddingValues(8.dp)
    val CellContentAlignment: Alignment = Alignment.CenterStart
    val HeaderContentAlignment: Alignment = Alignment.CenterStart
}

sealed interface ColumnWidth {
    data class Fixed(val width: Dp) : ColumnWidth {
        init { require(width.value.isFinite() && width >= 0.dp) }
    }
    data class Weight(val weight: Float = 1f, val minWidth: Dp = 48.dp) : ColumnWidth {
        init { require(weight.isFinite() && weight > 0 && minWidth.value.isFinite() && minWidth >= 0.dp) }
    }
    /** Explicitly measures all local rows, or only cached provider rows. Never triggers fetching. */
    data class Auto(val minWidth: Dp = 48.dp, val maxWidth: Dp = 600.dp) : ColumnWidth {
        init { require(minWidth.value.isFinite() && maxWidth.value.isFinite() && minWidth >= 0.dp && maxWidth >= minWidth) }
    }
}

/**
 * A typed, stateless column. Both header and data geometry follow the model's column order.
 * Header and cell content are aligned independently within their padded cells.
 */
class TableColumn<Item, Value> internal constructor(
    val id: String,
    val title: String,
    val value: (Item) -> Value,
    val width: ColumnWidth = TableColumnDefaults.Width,
    val filter: TableFilter<Value>? = null,
    val comparator: Comparator<Value>? = null,
    val searchText: ((Value) -> String)? = { it.toString() },
    val padding: PaddingValues = TableColumnDefaults.Padding,
    val cellContentAlignment: Alignment = TableColumnDefaults.CellContentAlignment,
    val headerContentAlignment: Alignment = TableColumnDefaults.HeaderContentAlignment,
    val description: String = "",
    val headerContent: @Composable (TableHeaderContent<Item>) -> Unit = { it.Content() },
    val cellContent: @Composable (TableValueCell<Item, Value>) -> Unit = { it.Content() },
    private val defaultCellContent: @Composable (Value) -> Unit = { Text(it.toString()) }
) {
    init { require(id.isNotBlank()) { "Column ID must not be blank" } }
    val sortable: Boolean get() = comparator != null
    internal fun matches(item: Item, filterValue: TableFilterValue): Boolean =
        requireNotNull(filter) { "Column $id has no filter" }.matches(value(item), filterValue)
    internal fun search(item: Item): String? = searchText?.invoke(value(item))
    internal fun compare(left: Item, right: Item): Int = requireNotNull(comparator).compare(value(left), value(right))

    @Composable
    internal fun render(cell: TableCellContext<Item>) {
        cellContent(TableValueCell(cell, value(cell.item), defaultCellContent))
    }
}

fun <Item> textColumn(
    id: String,
    title: String,
    value: (Item) -> String,
    width: ColumnWidth = TableColumnDefaults.Width,
    filter: TableFilter<String>? = null,
    comparator: Comparator<String>? = naturalOrder(),
    searchText: ((String) -> String)? = { it },
    padding: PaddingValues = TableColumnDefaults.Padding,
    cellContentAlignment: Alignment = TableColumnDefaults.CellContentAlignment,
    headerContentAlignment: Alignment = TableColumnDefaults.HeaderContentAlignment,
    description: String = "",
    headerContent: @Composable (TableHeaderContent<Item>) -> Unit = { it.Content() },
    cellContent: @Composable (TableValueCell<Item, String>) -> Unit = { it.Content() }
): TableColumn<Item, String> = TableColumn(
    id, title, value, width, filter, comparator, searchText, padding, cellContentAlignment, headerContentAlignment, description,
    headerContent, cellContent
)

fun <Item, Value> numberColumn(
    id: String,
    title: String,
    value: (Item) -> Value,
    width: ColumnWidth = TableColumnDefaults.Width,
    filter: TableFilter<Value>? = null,
    comparator: Comparator<Value>? = naturalOrder(),
    searchText: ((Value) -> String)? = { it.toString() },
    padding: PaddingValues = TableColumnDefaults.Padding,
    cellContentAlignment: Alignment = TableColumnDefaults.CellContentAlignment,
    headerContentAlignment: Alignment = TableColumnDefaults.HeaderContentAlignment,
    description: String = "",
    headerContent: @Composable (TableHeaderContent<Item>) -> Unit = { it.Content() },
    cellContent: @Composable (TableValueCell<Item, Value>) -> Unit = { it.Content() }
): TableColumn<Item, Value> where Value : Number, Value : Comparable<Value> = TableColumn(
    id, title, value, width, filter, comparator, searchText, padding, cellContentAlignment, headerContentAlignment, description,
    headerContent, cellContent
)

/** Enums use declaration order for sorting and their names for default text and search. */
fun <Item, Value : Enum<Value>> enumColumn(
    id: String,
    title: String,
    value: (Item) -> Value,
    width: ColumnWidth = TableColumnDefaults.Width,
    filter: TableFilter<Value>? = null,
    comparator: Comparator<Value>? = naturalOrder(),
    searchText: ((Value) -> String)? = { it.name },
    padding: PaddingValues = TableColumnDefaults.Padding,
    cellContentAlignment: Alignment = TableColumnDefaults.CellContentAlignment,
    headerContentAlignment: Alignment = TableColumnDefaults.HeaderContentAlignment,
    description: String = "",
    headerContent: @Composable (TableHeaderContent<Item>) -> Unit = { it.Content() },
    cellContent: @Composable (TableValueCell<Item, Value>) -> Unit = { it.Content() }
): TableColumn<Item, Value> = TableColumn(
    id, title, value, width, filter, comparator, searchText, padding, cellContentAlignment, headerContentAlignment, description,
    headerContent, cellContent, defaultCellContent = { Text(it.name) }
)

/** Read-only checkbox by default; override cellContent for an editable control. */
fun <Item> booleanColumn(
    id: String,
    title: String,
    value: (Item) -> Boolean,
    width: ColumnWidth = TableColumnDefaults.Width,
    filter: TableFilter<Boolean>? = null,
    comparator: Comparator<Boolean>? = naturalOrder(),
    searchText: ((Boolean) -> String)? = { it.toString() },
    padding: PaddingValues = TableColumnDefaults.Padding,
    cellContentAlignment: Alignment = TableColumnDefaults.CellContentAlignment,
    headerContentAlignment: Alignment = TableColumnDefaults.HeaderContentAlignment,
    description: String = "",
    headerContent: @Composable (TableHeaderContent<Item>) -> Unit = { it.Content() },
    cellContent: @Composable (TableValueCell<Item, Boolean>) -> Unit = { it.Content() }
): TableColumn<Item, Boolean> = TableColumn(
    id, title, value, width, filter, comparator, searchText, padding, cellContentAlignment, headerContentAlignment, description,
    headerContent, cellContent, defaultCellContent = { Checkbox(it, onCheckedChange = null) }
)

/**
 * Fully configurable column, including nullable values via an explicit nullable comparator.
 * Custom/action columns are neither sortable nor searchable unless configured explicitly.
 */
fun <Item, Value> customColumn(
    id: String,
    title: String,
    value: (Item) -> Value,
    width: ColumnWidth = TableColumnDefaults.Width,
    filter: TableFilter<Value>? = null,
    comparator: Comparator<Value>? = null,
    searchText: ((Value) -> String)? = null,
    padding: PaddingValues = TableColumnDefaults.Padding,
    cellContentAlignment: Alignment = TableColumnDefaults.CellContentAlignment,
    headerContentAlignment: Alignment = TableColumnDefaults.HeaderContentAlignment,
    description: String = "",
    headerContent: @Composable (TableHeaderContent<Item>) -> Unit = { it.Content() },
    cellContent: @Composable (TableValueCell<Item, Value>) -> Unit
): TableColumn<Item, Value> = TableColumn(
    id, title, value, width, filter, comparator, searchText, padding, cellContentAlignment, headerContentAlignment, description,
    headerContent, cellContent
)

fun <Item> applyTableQuery(
    items: List<Item>,
    columns: List<TableColumn<Item, *>>,
    query: TableQuery
): List<Item> {
    val byId = columns.associateBy { it.id }
    query.filters.forEach { (id, value) ->
        require(byId[id]?.filter?.accepts(value) == true) { "Invalid filter for column $id" }
    }
    query.sorts.forEach { require(byId[it.columnId]?.sortable == true) { "Invalid sort column ${it.columnId}" } }
    val filtered = items.filter { item ->
        query.filters.all { (id, value) -> byId.getValue(id).matches(item, value) } &&
            (query.search.isEmpty() || columns.any { it.search(item)?.contains(query.search, ignoreCase = true) == true })
    }
    if (query.sorts.isEmpty()) return filtered
    return filtered.sortedWith { left, right ->
        var result = 0
        for (sort in query.sorts) {
            val column = byId.getValue(sort.columnId)
            result = if (sort.direction == TableSortDirection.Ascending) column.compare(left, right)
                else column.compare(right, left)
            if (result != 0) break
        }
        result
    }
}

internal fun allocateWidths(columns: List<ColumnWidth>, available: Float, auto: List<Float>): List<Float> {
    require(columns.size == auto.size)
    val result = columns.mapIndexed { index, width -> when (width) {
        is ColumnWidth.Fixed -> width.width.value
        is ColumnWidth.Auto -> auto[index].coerceIn(width.minWidth.value, width.maxWidth.value)
        is ColumnWidth.Weight -> width.minWidth.value
    } }.toMutableList()
    val weights = columns.indices.filter { columns[it] is ColumnWidth.Weight }.toMutableSet()
    var remaining = (available - columns.indices.filter { it !in weights }.sumOf { result[it].toDouble() }).toFloat().coerceAtLeast(0f)
    while (weights.isNotEmpty()) {
        val total = weights.sumOf { (columns[it] as ColumnWidth.Weight).weight.toDouble() }
        val constrained = weights.filter { index ->
            remaining.toDouble() * (columns[index] as ColumnWidth.Weight).weight / total < result[index]
        }
        if (constrained.isEmpty()) {
            weights.forEach { result[it] = (remaining.toDouble() * (columns[it] as ColumnWidth.Weight).weight / total).toFloat() }
            break
        }
        constrained.forEach { remaining = (remaining - result[it]).coerceAtLeast(0f); weights.remove(it) }
    }
    return result
}

package com.michaelflisar.toolbox.table2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot

enum class TableSortDirection { Ascending, Descending }

data class TableSort(val columnId: String, val direction: TableSortDirection)

/** An immutable description of global search, column filters and ordered sort priorities. */
data class TableQuery(
    val search: String = "",
    val filters: Map<String, TableFilterValue> = emptyMap(),
    val sorts: List<TableSort> = emptyList()
)

sealed interface TableDisplay {
    data object Continuous : TableDisplay

    data class Paged(
        val pageSize: Int = 25,
        val pageSizes: List<Int> = listOf(10, 25, 50, 100)
    ) : TableDisplay {
        init {
            require(pageSize > 0 && pageSizes.isNotEmpty() && pageSizes.all { it > 0 })
            require(pageSize in pageSizes) { "Page size must be one of pageSizes" }
        }
    }
}

/** Reusable interaction state, independent of the current data and column renderer instances. */
class TableState(
    display: TableDisplay = TableDisplay.Continuous
) {
    var query by mutableStateOf(TableQuery())
        private set
    private var displayValue by mutableStateOf(display)
    val display: TableDisplay get() = displayValue
    private var pageValue by mutableStateOf(0)
    val page: Int get() = pageValue
    var selectedKeys by mutableStateOf<Set<Any>>(emptySet())
        private set
    internal var positionVersion by mutableStateOf(0)
        private set

    fun search(text: String) = updateQuery(query.copy(search = text))
    fun setFilter(columnId: String, value: TableFilterValue?) {
        updateQuery(query.copy(filters = if (value == null) query.filters - columnId else query.filters + (columnId to value)))
    }
    /** Resets all interaction state while preserving display settings and model data. */
    fun reset() = reset(search = true, filters = true, sorts = true, selection = true, position = true)

    /**
     * Resets only the enabled areas in a single snapshot.
     * Query changes also reset the position, even when [position] is false.
     */
    fun reset(
        search: Boolean = false,
        filters: Boolean = false,
        sorts: Boolean = false,
        selection: Boolean = false,
        position: Boolean = false
    ) {
        Snapshot.withMutableSnapshot {
            val nextQuery = query.copy(
                search = if (search) "" else query.search,
                filters = if (filters) emptyMap() else query.filters,
                sorts = if (sorts) emptyList() else query.sorts
            )
            val queryChanged = nextQuery != query
            if (queryChanged) query = nextQuery
            if (selection) selectedKeys = emptySet()
            if (queryChanged || position) {
                pageValue = 0
                positionVersion++
            }
        }
    }

    fun setSort(columnId: String, direction: TableSortDirection?) {
        val index = query.sorts.indexOfFirst { it.columnId == columnId }
        val sorts = query.sorts.toMutableList()
        if (direction == null) {
            sorts.removeAll { it.columnId == columnId }
        } else if (index >= 0) {
            sorts[index] = TableSort(columnId, direction)
        } else {
            sorts.add(TableSort(columnId, direction))
        }
        updateQuery(query.copy(sorts = sorts))
    }

    fun toggleSort(columnId: String) = setSort(columnId, when (query.sorts.find { it.columnId == columnId }?.direction) {
        null -> TableSortDirection.Ascending
        TableSortDirection.Ascending -> TableSortDirection.Descending
        TableSortDirection.Descending -> null
    })

    fun setDisplay(value: TableDisplay) {
        if (display != value) {
            displayValue = value
            pageValue = 0
            positionVersion++
        }
    }
    fun setPage(value: Int) {
        require(value >= 0) { "Page must not be negative" }
        if (page != value) {
            pageValue = value
            positionVersion++
        }
    }
    fun select(key: Any, selected: Boolean = true) {
        selectedKeys = if (selected) selectedKeys + key else selectedKeys - key
    }
    fun toggleSelection(key: Any) = select(key, key !in selectedKeys)

    internal fun retainSelection(keys: Set<Any>) { selectedKeys = selectedKeys.intersect(keys) }
    internal fun reconcileColumns(columns: List<TableColumn<*, *>>) {
        updateQuery(query.forColumns(columns))
    }
    private fun updateQuery(value: TableQuery) {
        if (query != value) {
            query = value
            pageValue = 0
            positionVersion++
        }
    }
}

internal fun TableQuery.forColumns(columns: List<TableColumn<*, *>>): TableQuery {
    val byId = columns.associateBy { it.id }
    return copy(
        filters = filters.filter { (id, value) -> byId[id]?.filter?.accepts(value) == true },
        sorts = sorts.filter { byId[it.columnId]?.sortable == true }
    )
}

@Composable
fun rememberTableState(display: TableDisplay = TableDisplay.Continuous): TableState =
    remember { TableState(display) }

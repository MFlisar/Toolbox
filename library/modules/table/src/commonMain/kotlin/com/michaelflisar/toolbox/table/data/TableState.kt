package com.michaelflisar.toolbox.table.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.michaelflisar.toolbox.table.definitions.Row
import com.michaelflisar.toolbox.table.definitions.TableDefinition

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> rememberTableState(
    items: List<T>,
    definition: TableDefinition<T>,
    //paginationSize: Int? = null,
    sorts: SnapshotStateList<TableSort> = remember { mutableStateListOf() },
    selectedRows: SnapshotStateList<Int> = remember { mutableStateListOf() },
): TableState<T> {
    val data = key(definition) {
        val textFilter = remember(definition) {
            mutableStateOf("")
        }
        val rows = items.map { definition.createRow(it) }
        val filteredList by remember(
            rows,
            definition.columns.mapNotNull { it.filter }.map { it.state.value }.toList(),
            textFilter.value
        ) {
            derivedStateOf {
                rows.filter { row ->
                    var valid = true
                    for (column in definition.columns) {
                        val filter = column.filter
                        if (filter != null) {
                            valid = column.isFilterValid(row.item)
                        }
                        if (valid && textFilter.value.isNotEmpty()) {
                            valid = row.cells.joinToString { it.displayValue() }
                                .contains(textFilter.value, ignoreCase = true)
                        }
                        if (!valid) {
                            break
                        }
                    }
                    valid
                }
            }
        }
        val sortedList by remember(filteredList.toList(), sorts.toList()) {
            derivedStateOf {
                val s = sorts.toList()
                if (s.isEmpty()) {
                    filteredList
                } else {
                    var comparator = s.first().comparator<T>()
                    s.drop(1).forEach { sort ->
                        val c = when (sort.type) {
                            TableSort.Type.Asc -> compareBy { it.cells[sort.columnIndex].sort() }
                            TableSort.Type.Desc -> compareByDescending<Row<T>> { it.cells[sort.columnIndex].sort() }
                        }
                        comparator = comparator.then(c)
                    }

                    filteredList.sortedWith(comparator)
                }
            }
        }
        listOf(textFilter, rows, filteredList, sortedList)
    }

    val filter = data[0] as MutableState<String>
    val allRows = data[1] as List<Row<T>>
    val filteredRows = data[2] as List<Row<T>>
    val sortedRows = data[3] as List<Row<T>>

    //val pages = if (paginationSize != null) {
    //    (filteredList.size + paginationSize - 1) / paginationSize
    //} else {
    //    1
    //}
    //val page = remember(pages) { mutableIntStateOf(0) }


    val textFilterIsActive = remember(filter.value) {
        derivedStateOf {
            filter.value.isNotEmpty()
        }
    }
    val columnFilterIsActive = remember(definition.columns) {
        derivedStateOf {
            definition.columns.map { it.filter?.isActive() ?: false }.contains(true)
        }
    }

    val filterState = remember {
        derivedStateOf {
            TableFilterState(
                textFilterIsActive.value,
                columnFilterIsActive.value
            )
        }
    }

    return TableState(
        items,
        definition,
        filter, /*pages, page,*/
        sorts,
        selectedRows,
        allRows,
        filteredRows,
        sortedRows,
        filterState
    )
}

@Stable
class TableFilterState(
    val isTextFilterActive: Boolean = false,
    val isColumnFilterActive: Boolean = false
) {
    var isActive = isTextFilterActive || isColumnFilterActive
}

class TableState<T>(
    val items: List<T>,
    val definition: TableDefinition<T>,
    val filter: MutableState<String>,
    //val pages: Int,
    //val page: MutableIntState,
    val sorts: SnapshotStateList<TableSort>,
    val selectedRows: SnapshotStateList<Int>,
    val allRows: List<Row<T>>,
    val filteredRows: List<Row<T>>,
    val sortedRows: List<Row<T>>,
    val filterState: State<TableFilterState>
) {
    fun clearFilter(
        textFilter: Boolean,
        columnFilters: Boolean
    ) {
        if (columnFilters)
            definition.clearFilter()
        if (textFilter)
            filter.value = ""
    }
}
package com.michaelflisar.toolbox.table.definitions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.michaelflisar.toolbox.table.data.Sort

class TableDefinition<Item> internal constructor(
    val columns: List<Column<*, Item>>,
    val keyProvider: (item: Item) -> Any,
    val sorts: SnapshotStateList<Sort>,
    val selectedRows: SnapshotStateList<Int>
) {
    @Composable
    fun createRow(item: Item) = Row(
        item = item,
        cells = columns.map { it.createCellForRow(item) }
    )

    fun clearFilter() {
        columns.forEach {
            it.filter?.clear()
        }
    }
}

@Composable
fun <Item> rememberTableDefinition(
    columns: List<Column<*, Item>>,
    keyProvider: (item: Item) -> Any,
    sorts: SnapshotStateList<Sort> = remember { mutableStateListOf() },
    selectedRows: SnapshotStateList<Int> = remember { mutableStateListOf() },
): TableDefinition<Item> = TableDefinition(columns, keyProvider, sorts, selectedRows)
package com.michaelflisar.toolbox.table.definitions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.michaelflisar.toolbox.table.data.TableSort

class TableDefinition<Item> internal constructor(
    val columns: List<Column<*, Item>>,
    val keyProvider: (item: Item) -> Any
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
    keyProvider: (item: Item) -> Any
): TableDefinition<Item> {
    return remember(columns, keyProvider)  {
        TableDefinition(columns, keyProvider)
    }

}
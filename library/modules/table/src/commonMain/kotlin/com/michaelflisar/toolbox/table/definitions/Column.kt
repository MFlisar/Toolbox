package com.michaelflisar.toolbox.table.definitions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.table.data.ColumnWidth

@Composable
fun <Item> rememberTableColumns(
    columns: List<Column<*, Item>>
): List<Column<*, Item>> {
    return remember {
        columns
    }
}

class Column<CellValue, Item>(
    val header: Header,
    val width: ColumnWidth,
    val filter: Filter<Item, CellValue>? = null,
    val sortable: Boolean = true,
    val cellValue: (item: Item) -> CellValue,
    val createCell: @Composable (item: Item, value: CellValue) -> Cell<CellValue>
) {
    @Composable
    internal fun createCellForRow(item: Item): Cell<CellValue> {
        return createCell(item, cellValue(item))
    }

    fun isFilterValid(item: Item): Boolean {
        return filter == null || filter.isValid(item, cellValue)
    }
}




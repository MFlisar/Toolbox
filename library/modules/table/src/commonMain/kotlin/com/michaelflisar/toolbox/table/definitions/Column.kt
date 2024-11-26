package com.michaelflisar.toolbox.table.definitions

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class Column<CellValue, Item>(
    val header: Header,
    val modifier: RowScope.() -> Modifier,
    val filter: Filter<Item, CellValue>? = null,
    val cellValue: (Item) -> CellValue,
    val createCell: @Composable (value: CellValue) -> Cell<CellValue>
) {
    @Composable
    internal fun createCellForRow(item: Item): Cell<CellValue> {
        return createCell(cellValue(item))
    }

    fun isFilterValid(item: Item) : Boolean {
        return filter == null || filter.isValid(item, cellValue)
    }
}


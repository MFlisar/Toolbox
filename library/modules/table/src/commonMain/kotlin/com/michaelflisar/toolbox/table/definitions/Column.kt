package com.michaelflisar.toolbox.table.definitions

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class Column<CellValue, Item>(
    val header: Header,
    val modifier: RowScope.() -> Modifier,
    val filter: Filter<Item, CellValue>? = null,
    val sortable: Boolean = true,
    val cellValue: (Item) -> CellValue,
    val createCell: @Composable (item: Item, value: CellValue) -> Cell<CellValue>
) {
    @Composable
    internal fun createCellForRow(item: Item): Cell<CellValue> {
        return createCell(item, cellValue(item))
    }

    fun isFilterValid(item: Item) : Boolean {
        return filter == null || filter.isValid(item, cellValue)
    }

    // TODO:
    // useful???

    /*
    companion object {

        fun <Item> Checkbox(
            header: Header,
            modifier: RowScope.() -> Modifier,
            filter: Filter<Item, Boolean>? = null,
            cellValue: (Item) -> Boolean
        ) = Column(
            header = header,
            modifier = modifier,
            filter = filter,
            cellValue = cellValue,
            createCell = { item, value ->
                Cell.Checkmark(
                    value,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalCellAlignment = Alignment.CenterVertically
                )
            }
        )
    }
     */
}




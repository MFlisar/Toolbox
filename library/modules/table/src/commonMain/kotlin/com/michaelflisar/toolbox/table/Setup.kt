package com.michaelflisar.toolbox.table

import com.michaelflisar.toolbox.ToolboxDefaults

class Setup<Item>(
    val clickType: ClickType<Item> = ClickType.None(),
    val emptyText: String = ToolboxDefaults.TEXT_EMPTY
) {
    sealed class ClickType<Item> {
        class None<T> : ClickType<T>()
        class Select<T> : ClickType<T>()
        class RowClick<T>(val onRowClicked: (row: Int, item: T) -> Unit) : ClickType<T>()
        class CellClick<T>(val onCellClicked: (row: Int, column: Int, item: T) -> Unit) :
            ClickType<T>()
    }
}
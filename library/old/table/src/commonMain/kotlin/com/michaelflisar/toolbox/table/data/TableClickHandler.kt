package com.michaelflisar.toolbox.table.data

sealed class TableClickHandler<Item> {
    class None<T> : TableClickHandler<T>()
    class Select<T> : TableClickHandler<T>()
    class RowClick<T>(val onRowClicked: (row: Int, item: T) -> Unit) : TableClickHandler<T>()
    class CellClick<T>(val onCellClicked: (row: Int, column: Int, item: T) -> Unit) :
        TableClickHandler<T>()
}
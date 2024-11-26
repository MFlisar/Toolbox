package com.michaelflisar.toolbox.table.data

import com.michaelflisar.toolbox.table.definitions.Row

class Sort(
    val columnIndex: Int,
    val type: Type = Type.Asc
) {
    enum class Type {
        Asc, Desc
    }

    fun <T> comparator() = when (type) {
        Type.Asc -> compareBy { it.cells[columnIndex].sort() }
        Type.Desc -> compareByDescending<Row<T>> { it.cells[columnIndex].sort() }
    }
}
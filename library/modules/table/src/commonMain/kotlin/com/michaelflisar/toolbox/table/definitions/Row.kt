package com.michaelflisar.toolbox.table.definitions

class Row<Item>(
    val item: Item,
    val cells: List<Cell<*>>
)
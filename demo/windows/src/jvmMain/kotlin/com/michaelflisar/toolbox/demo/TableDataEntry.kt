package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.table.MyTable

class TableDataEntry(
    val id: Int,
    val name: String,
    val description: String
) {
    companion object {
        val HEADERS = listOf(
            MyTable.Header("ID", { Modifier.width(96.dp) }),
            MyTable.Header("Name", { Modifier.width(128.dp) }),
            MyTable.Header("Description", { Modifier.weight(1f) }),
        )
    }

    fun createRow() = MyTable.Row(
        this,
        listOf(
            MyTable.Cell.Number(id),
            MyTable.Cell.Text(name),
            MyTable.Cell.Text(description),
        )
    )
}
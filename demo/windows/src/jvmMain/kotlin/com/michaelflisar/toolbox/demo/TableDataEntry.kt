package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.table.MyTable

class TableDataEntry(
    val id: Int,
    val name: String,
    val description: String
) {
    companion object {
        val HEADERS = listOf(
            MyTable.Header.Text("ID sdf aad fasdf sdf a", { Modifier.width(96.dp) }),
            MyTable.Header.Text("Name", { Modifier.width(128.dp) }),
            MyTable.Header.Text("Description", { Modifier.weight(1f) }),
            MyTable.Header.Icon(
                "Setting",
                { Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.secondary) },
                { Modifier.width(128.dp) },
                "Some additional information...",
                //align = Alignment.CenterHorizontally
            )
        )
    }

    @Composable
    fun createRow() = MyTable.Row(
        this,
        listOf(
            MyTable.Cell.Number(id),
            MyTable.Cell.Text(name, textStyle = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, cellAlignment = Alignment.CenterVertically),
            MyTable.Cell.Text(description),
            MyTable.Cell.Text("...")
        )
    )
}
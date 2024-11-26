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
import com.michaelflisar.toolbox.table.definitions.Cell
import com.michaelflisar.toolbox.table.definitions.Column
import com.michaelflisar.toolbox.table.definitions.Filter
import com.michaelflisar.toolbox.table.definitions.Header
import com.michaelflisar.toolbox.table.definitions.TableDefinition
import com.michaelflisar.toolbox.table.definitions.rememberTableDefinition

class TableDataEntry(
    val id: Int,
    val checked: Boolean,
    val name: String,
    val color: ColorEnum,
    val description: String
) {
    enum class ColorEnum {
        Red, Green, Blue
    }

    companion object {

        @Composable
        fun createTableDefinitions(): TableDefinition<TableDataEntry> {
            val columns = listOf<Column<*, TableDataEntry>>(
                Column(
                    header = Header.Text("Checked"),
                    modifier = { Modifier.width(96.dp) },
                    filter = Filter.Checkmark(),
                    cellValue = { it.checked },
                    createCell = { item, value ->
                        Cell.Checkmark(
                            value,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalCellAlignment = Alignment.CenterVertically
                        )
                    }
                ),
                Column(
                    header = Header.Text("ID"),
                    modifier = { Modifier.width(96.dp) },
                    cellValue = { it.id },
                    filter = Filter.Number(),
                    createCell = { item, value ->
                        Cell.Number(
                            value,
                            textStyle = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            verticalCellAlignment = Alignment.CenterVertically
                        )
                    }
                ),
                Column(
                    header = Header.Text("Name"),
                    modifier = { Modifier.width(128.dp) },
                    filter = Filter.Text(),
                    cellValue = { it.name },
                    createCell = { item, value ->
                        Cell.Text(
                            value,
                            textStyle = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            verticalCellAlignment = Alignment.CenterVertically
                        )
                    }
                ),/*
                Column(
                    header = Header.Text("Color"),
                    modifier = { Modifier.width(128.dp) },
                    /*filter = Filter.TextData(
                        cellValueToString = { it.name }
                    ),*/
                    filter = Filter.List(
                        items = ColorEnum.entries.toList(),
                        mapper = { it.name },
                        multiSelect = true
                    ),
                    cellValue = { it.color },
                    createCell = {
                        Cell.Data(
                            value = it,
                            valueToText = { it.name },
                            textStyle = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            verticalCellAlignment = Alignment.CenterVertically
                        )
                    }
                ),*/
                Column(
                    header = Header.Text("Color"),
                    modifier = { Modifier.width(128.dp) },
                    filter = Filter.Enum(
                        items = ColorEnum.entries,
                        multiSelect = true
                    ),
                    cellValue = { it.color },
                    createCell = { item, value ->
                        Cell.Enum(
                            value = value,
                            textStyle = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            verticalCellAlignment = Alignment.CenterVertically
                        )
                    }
                ),
                Column(
                    header = Header.Text("Description"),
                    modifier = { Modifier.weight(1f) },
                    filter = Filter.Text(),
                    cellValue = { it.description },
                    createCell = { item, value ->
                        Cell.Text(value)
                    }
                ),
                Column(
                    header = Header.Icon(
                        "Setting",
                        {
                            Icon(
                                Icons.Default.Settings,
                                null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        "Some additional information..."
                    ),
                    modifier = { Modifier.width(128.dp) },
                    cellValue = { "..." },
                    createCell = { item, value ->
                        Cell.Text("...")
                    }
                )
            )
            return rememberTableDefinition(columns)
        }
    }
}
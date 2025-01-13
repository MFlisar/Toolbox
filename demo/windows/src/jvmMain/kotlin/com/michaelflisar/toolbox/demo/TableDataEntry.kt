package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.form.FormValidator
import com.michaelflisar.toolbox.form.rememberFormFieldCheckbox
import com.michaelflisar.toolbox.form.rememberFormFieldDropdown
import com.michaelflisar.toolbox.form.rememberFormFieldInfo
import com.michaelflisar.toolbox.form.rememberFormFieldNumber
import com.michaelflisar.toolbox.form.rememberFormFieldText
import com.michaelflisar.toolbox.table.definitions.Cell
import com.michaelflisar.toolbox.table.definitions.Column
import com.michaelflisar.toolbox.table.definitions.Filter
import com.michaelflisar.toolbox.table.definitions.Header

class TableDataEntry(
    val id: Int,
    val checked: Boolean,
    val name: String,
    val age: Int,
    val color: ColorEnum,
    val description: String
) {
    enum class ColorEnum(val color: Color) {
        Red(Color.Red),
        Green(Color.Green),
        Blue(Color.Blue)
    }

    companion object {

        fun columns() = listOf<Column<*, TableDataEntry>>(
            Column(
                header = Header.Text("Checked"),
                modifier = { Modifier.width(96.dp) },
                filter = null,
                sortable = false,
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
                sortable = false,
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
            ),
            Column(
                header = Header.Text("Age"),
                modifier = { Modifier.width(96.dp) },
                cellValue = { it.age },
                filter = Filter.Number(),
                sortable = false,
                createCell = { item, value ->
                    Cell.Number(
                        value,
                        textStyle = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        verticalCellAlignment = Alignment.CenterVertically
                    )
                }
            ),
            // Alternative Color Variante als "plain data"
            //Column(
            //    header = Header.Text("Color"),
            //    modifier = { Modifier.width(128.dp) },
            //    cellValue = { it.color },
            //    filter = null,
            //    sortable = false,
            //    createCell = { item, value ->
            //        Cell.Data(
            //            value = value,
            //            valueToText = { it.name },
            //            textStyle = MaterialTheme.typography.bodySmall,
            //            textAlign = TextAlign.Center,
            //            verticalCellAlignment = Alignment.CenterVertically
            //        )
            //    }
            //),
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
                    "Some additional information..."
                ) {
                    Icon(
                        Icons.Default.Settings,
                        null,
                        tint = MaterialTheme.colorScheme.secondary
                    )

                },
                modifier = { Modifier.width(128.dp) },
                cellValue = { "..." },
                createCell = { item, value ->
                    Cell.Text("...")
                }
            )
        )

        @Composable
        fun fieldID(item: TableDataEntry) = rememberFormFieldInfo("ID", item.id.toString())
        @Composable
        fun fieldChecked(item: TableDataEntry) = rememberFormFieldCheckbox("Checked", item.checked)
        @Composable
        fun fieldName(item: TableDataEntry) = rememberFormFieldText("Name", item.name, isValid = FormValidator.isNotEmpty("Name"))
        @Composable
        fun fieldAge(item: TableDataEntry) = rememberFormFieldNumber(
            "Age",
            item.age,
            isValid = FormValidator.isNumberInRange(0, 100)
        )
        @Composable
        fun fieldColor(item: TableDataEntry) = rememberFormFieldDropdown(
            "Color",
            item.color.ordinal,
            ColorEnum.entries.map { it.name })
        @Composable
        fun fieldDescription(item: TableDataEntry) = rememberFormFieldText("Description", item.description)

        fun defaultData(): List<TableDataEntry> {
            return listOf(
                TableDataEntry(
                    1,
                    true,
                    "Michael",
                    39,
                    TableDataEntry.ColorEnum.Red,
                    "Description of Michael..."
                ),
                TableDataEntry(
                    2,
                    false,
                    "Christine",
                    36,
                    TableDataEntry.ColorEnum.Blue,
                    "Description of Christine..."
                ),
                TableDataEntry(
                    3,
                    true,
                    "Benjamin",
                    18,
                    TableDataEntry.ColorEnum.Green,
                    "Description of Benjamin..."
                ),
                TableDataEntry(
                    4,
                    false,
                    "Michael",
                    39,
                    TableDataEntry.ColorEnum.Red,
                    "Description of Michael..."
                ),
                TableDataEntry(
                    5,
                    true,
                    "Christine",
                    36,
                    TableDataEntry.ColorEnum.Blue,
                    "Description of Christine..."
                ),
                TableDataEntry(
                    6,
                    false,
                    "Benjamin",
                    18,
                    TableDataEntry.ColorEnum.Green,
                    "Description of Benjamin..."
                ),
                TableDataEntry(
                    7,
                    true,
                    "Michael",
                    39,
                    TableDataEntry.ColorEnum.Red,
                    "Description of Michael..."
                ),
                TableDataEntry(
                    8,
                    false,
                    "Christine",
                    36,
                    TableDataEntry.ColorEnum.Blue,
                    "Description of Christine..."
                ),
                TableDataEntry(
                    9,
                    true,
                    "Benjamin",
                    18,
                    TableDataEntry.ColorEnum.Green,
                    "Description of Benjamin..."
                ),
                TableDataEntry(
                    10,
                    false,
                    "Michael",
                    39,
                    TableDataEntry.ColorEnum.Red,
                    "Description of Michael..."
                ),
                TableDataEntry(
                    11,
                    true,
                    "Christine",
                    36,
                    TableDataEntry.ColorEnum.Blue,
                    "Description of Christine..."
                ),
                TableDataEntry(
                    12,
                    false,
                    "Benjamin",
                    18,
                    TableDataEntry.ColorEnum.Green,
                    "Description of Benjamin..."
                )
            )
        }
    }
}
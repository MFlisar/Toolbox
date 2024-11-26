package com.michaelflisar.toolbox.table.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.table.Setup
import com.michaelflisar.toolbox.table.definitions.Column
import com.michaelflisar.toolbox.table.definitions.Row

@Composable
internal fun <Item> Row(
    row: Row<Item>,
    line: Int,
    columns: List<Column<*, Item>>,
    selectedRows: SnapshotStateList<Int>,
    setup: Setup<Item>
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(
                when (val clickType = setup.clickType) {
                    is Setup.ClickType.RowClick -> Modifier.clickable {
                        clickType.onRowClicked(
                            line,
                            row.item
                        )
                    }

                    is Setup.ClickType.Select -> Modifier.clickable {
                        if (selectedRows.contains(line)) {
                            selectedRows.remove(line)
                        } else {
                            selectedRows.add(line)
                        }
                    }

                    else -> Modifier
                }
            )
            .then(
                if (selectedRows.contains(line)) {
                    Modifier.background(MaterialTheme.colorScheme.secondary)
                } else Modifier
            )
            .padding(end = ToolboxDefaults.SCROLLBAR_SPACE) // für Scrollbar
    ) {
        columns.forEachIndexed { index, column ->
            val modifier = when (val clickType = setup.clickType) {
                is Setup.ClickType.CellClick -> Modifier.clickable {
                    clickType.onCellClicked(
                        line,
                        index,
                        row.item
                    )
                }

                is Setup.ClickType.None,
                is Setup.ClickType.RowClick,
                is Setup.ClickType.Select -> Modifier
            }
            val cell = row.cells[index]
            cell.render(
                column.modifier(this).align(cell.verticalCellAlignment).then(modifier)
                    .padding(column.header.cellPadding)
            )
            if (index < columns.lastIndex) {
                TableRowSpacer()
            }
        }
    }
}
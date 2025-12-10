package com.michaelflisar.toolbox.table.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.scrollbar

import com.michaelflisar.toolbox.table.data.TableClickHandler
import com.michaelflisar.toolbox.table.data.TableData
import com.michaelflisar.toolbox.table.definitions.Column
import com.michaelflisar.toolbox.table.definitions.Row

@Composable
internal fun TableRow(
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    borderColor: Color = Color.Unspecified,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .background(containerColor, shape = shape)
            .then(
                if (borderColor != Color.Unspecified) {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .height(IntrinsicSize.Min)
            .then(modifier),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor
        ) {
            content()
        }
    }
}

@Composable
internal fun <Item> TableRow(
    tableData: TableData,
    row: Row<Item>,
    line: Int,
    columns: List<Column<*, Item>>,
    selectedRows: SnapshotStateList<Int>,
    clickHandler: TableClickHandler<Item>
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .horizontalScroll(tableData.scrollState)
            .then(
                when (val clickType = clickHandler) {
                    is TableClickHandler.RowClick -> Modifier.clickable {
                        clickType.onRowClicked(
                            line,
                            row.item
                        )
                    }

                    is TableClickHandler.Select -> Modifier.clickable {
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
            .padding(end = MaterialTheme.scrollbar.size) // für Scrollbar
    ) {
        columns.forEachIndexed { index, column ->
            val modifier = when (val clickType = clickHandler) {
                is TableClickHandler.CellClick -> Modifier.clickable {
                    clickType.onCellClicked(
                        line,
                        index,
                        row.item
                    )
                }

                is TableClickHandler.None,
                is TableClickHandler.RowClick,
                is TableClickHandler.Select -> Modifier
            }
            val cell = row.cells[index]
            cell.render(
                Modifier.width(tableData.widths.value[index])
                //column.width.modifier(this)
                    .align(cell.verticalCellAlignment).then(modifier)
                    .padding(column.header.cellPadding)
            )
            if (index < columns.lastIndex) {
                TableRowSpacer()
            }
        }
    }
}
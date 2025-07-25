package com.michaelflisar.toolbox.table.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.table.data.TableData
import com.michaelflisar.toolbox.table.data.TableSort
import com.michaelflisar.toolbox.table.definitions.Column

@Composable
internal fun Header(
    tableData: TableData,
    columns: List<Column<*, *>>,
    sorts: SnapshotStateList<TableSort>,
    showFilterOnHeaderClick: Boolean
) {
    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                    .horizontalScroll(tableData.scrollState)
                    .padding(end = LocalStyle.current.scrollbar) // für Scrollbar
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurface
                ) {
                    columns.forEachIndexed { index, column ->
                        TableHeaderCell(index, column, tableData.widths.value[index], sorts, showFilterOnHeaderClick)
                        if (index != columns.size - 1) {
                            TableRowSpacer()
                        }
                    }
                }
            }
        }
    }
}
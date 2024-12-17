package com.michaelflisar.toolbox.table.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
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
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.table.data.Sort
import com.michaelflisar.toolbox.table.definitions.Column

@Composable
internal fun Header(
    columns: List<Column<*, *>>,
    sorts: SnapshotStateList<Sort>
) {
    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Box {
            androidx.compose.foundation.layout.Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(end = LocalStyle.current.scrollbar) // für Scrollbar
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurface
                ) {
                    columns.forEachIndexed { index, column ->
                        TableHeaderCell(index, column, sorts)
                        if (index != columns.size - 1) {
                            TableRowSpacer()
                        }
                    }
                }
            }
        }
    }
}
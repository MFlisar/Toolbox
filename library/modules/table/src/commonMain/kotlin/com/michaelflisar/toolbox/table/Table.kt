package com.michaelflisar.toolbox.table

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.scrollbar

import com.michaelflisar.toolbox.table.data.ColumnWidth
import com.michaelflisar.toolbox.table.data.TableClickHandler
import com.michaelflisar.toolbox.table.data.TableData
import com.michaelflisar.toolbox.table.data.TableSort
import com.michaelflisar.toolbox.table.data.TableState
import com.michaelflisar.toolbox.table.definitions.Row
import com.michaelflisar.toolbox.table.ui.Header
import com.michaelflisar.toolbox.table.ui.TableColumnDivider
import com.michaelflisar.toolbox.table.ui.TableRow
import com.michaelflisar.toolbox.ui.MyScrollableLazyColumn

object TableDefaults {

    @Composable
    fun searchBarColors(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onPrimary,
        borderColor: Color = Color.Unspecified
    ): TableSearchBarColors {
        return TableSearchBarColors(containerColor, contentColor, borderColor)
    }

    @Composable
    fun headerColors(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onPrimary
    ): TableHeaderColors {
        return TableHeaderColors(containerColor, contentColor)
    }

    @Composable
    fun footerColors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor: Color = MaterialTheme.colorScheme.onSurface
    ): TableFooterColors {
        return TableFooterColors(containerColor, contentColor)
    }
}

@Composable
fun <Item> Table(
    state: TableState<Item>,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    clickHandler: TableClickHandler<Item> = TableClickHandler.None(),
    showFilterOnHeaderClick: Boolean = true,
    onFilterChanged: (filtered: List<Row<Item>>) -> Unit = {},
    onSortingChanged: (sorts: List<TableSort>) -> Unit = {},
    header: @Composable (() -> Unit)? = { TableHeader(state) },
    footer: @Composable (() -> Unit)? = { TableFooter(state) }
) {
    // if definition changes -> restart with a new composition
    key(state.definition) {

        val definition = state.definition
        val filteredList = state.filteredRows
        val sortedList = state.sortedRows

        LaunchedEffect(filteredList) {
            onFilterChanged(filteredList)
        }
        LaunchedEffect(state.sorts.toList()) {
            onSortingChanged(state.sorts.toList())
        }


        val density = LocalDensity.current
        val tableWidth = remember { mutableStateOf(0.dp) }
        val scrollState = rememberScrollState()
        val tableData = remember {
            TableData(scrollState, mutableStateOf(definition.columns.map { Dp.Unspecified }))
        }
        val scrollBarWidth = MaterialTheme.scrollbar.size

        LaunchedEffect(tableWidth.value, tableData.widths.value.toList()) {

            var sumOfFixedWidths = 0.dp
            var sumOfWeights = 0f
            var foundNotMeasuredAutoColumn: Boolean = false

            definition.columns.map { it.width }.forEachIndexed { index, w ->
                when (w) {
                    is ColumnWidth.Fixed -> {
                        sumOfFixedWidths += w.width
                    }
                    is ColumnWidth.Weight -> {
                        sumOfWeights += w.weight
                    }
                    is ColumnWidth.Auto -> {
                        if (tableData.widths.value[index] != Dp.Unspecified) {
                            sumOfFixedWidths += tableData.widths.value[index]
                        } else {
                            foundNotMeasuredAutoColumn = true
                        }
                    }
                }
            }
            if (foundNotMeasuredAutoColumn) {
                // if there is an auto column which has not been measured yet, we cannot calculate the widths yet
                return@LaunchedEffect
            }

            val availableWidth = tableWidth.value - scrollBarWidth - sumOfFixedWidths - 1.dp * (definition.columns.size - 1) // 1.dp for each divider

            tableData.widths.value = definition.columns.mapIndexed { index, column ->
                when (column.width) {
                    is ColumnWidth.Fixed -> column.width.width
                    is ColumnWidth.Weight -> {
                        val fraction = column.width.weight / sumOfWeights
                        val fractionedWidth = availableWidth * fraction
                        if (fractionedWidth < column.width.minWidth) {
                            column.width.minWidth
                        } else {
                            fractionedWidth
                        }
                    }
                    is ColumnWidth.Auto ->  tableData.widths.value[index]
                }
            }
        }

        definition.columns.forEachIndexed { index, column ->
            if (column.width is ColumnWidth.Auto && tableData.widths.value[index] == Dp.Unspecified) {
                TableUtil.MeasureMaxWidth(
                    items = state.allRows,
                    render = { it.cells[index].render(Modifier .padding(column.header.cellPadding)) },
                    onMeasured = { w ->
                        tableData.widths.value = tableData.widths.value.toMutableList().also {
                            it[index] = w
                        }
                    }
                )
            }
        }

        Column(
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = shape
                )
                .clip(shape)
                .onSizeChanged {
                    tableWidth.value = with(density) { it.width.toDp() }
                }
        ) {
            if (header != null) {
                TableColumnDivider()
                header()
            }

            // Column Header
            Header(tableData, definition.columns, state.sorts, showFilterOnHeaderClick)
            TableColumnDivider()

            // Scrollable Content
            MyScrollableLazyColumn(
                modifier = Modifier.weight(1f),
                itemSpacing = 0.dp,
                overlapScrollbar = true
            ) {
                sortedList.forEachIndexed { index, item ->
                    item(key = definition.keyProvider(item.item)) {
                        TableRow(tableData, item, index, definition.columns, state.selectedRows, clickHandler)
                    }
                    if (index < sortedList.lastIndex) {
                        item(key = "DIVIDER-$index") {
                            TableColumnDivider()
                        }
                    }
                }
            }

            TableColumnDivider()

            if (footer != null) {
                footer()
                TableColumnDivider()
            }
        }
    }
}
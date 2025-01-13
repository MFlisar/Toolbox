package com.michaelflisar.toolbox.table

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.table.data.Sort
import com.michaelflisar.toolbox.table.definitions.Row
import com.michaelflisar.toolbox.table.definitions.TableDefinition
import com.michaelflisar.toolbox.table.ui.Header
import com.michaelflisar.toolbox.table.ui.Row
import com.michaelflisar.toolbox.table.ui.TableColumnDivider
import com.michaelflisar.toolbox.ui.MyScrollableLazyColumn

@Composable
fun <Item> Table(
    definition: TableDefinition<Item>,
    items: List<Item>,
    setup: Setup<Item>,
    modifier: Modifier = Modifier,
    onFilterChanged: (filtered: List<Row<Item>>) -> Unit = {},
    onSortingChanged: (sorts: List<Sort>) -> Unit = {}
) {
    // if definition changes -> restart with a new composition
    key(definition) {
        val rows = items.map { definition.createRow(it) }
        val filteredList by remember(
            rows,
            definition.columns.mapNotNull { it.filter }.map { it.state.value }.toList()
        ) {
            derivedStateOf {
                rows.filter { row ->
                    var valid = true
                    for (column in definition.columns) {
                        val filter = column.filter
                        if (filter != null) {
                            //val value = row.cells[index].value
                            valid = column.isFilterValid(row.item)
                        }
                        if (!valid) {
                            break
                        }
                    }
                    valid
                }
            }
        }

        val sortedList by remember(filteredList.toList(), definition.sorts.toList()) {
            derivedStateOf {
                val s = definition.sorts.toList()
                if (s.isEmpty()) {
                    filteredList
                } else {
                    var comparator = s.first().comparator<Item>()
                    s.drop(1).forEach { sort ->
                        val c = when (sort.type) {
                            Sort.Type.Asc -> compareBy { it.cells[sort.columnIndex].sort() }
                            Sort.Type.Desc -> compareByDescending<Row<Item>> { it.cells[sort.columnIndex].sort() }
                        }
                        comparator = comparator.then(c)
                    }

                    filteredList.sortedWith(comparator)
                }
            }
        }

        LaunchedEffect(filteredList) {
            onFilterChanged(filteredList)
        }
        LaunchedEffect(definition.sorts.toList()) {
            onSortingChanged(definition.sorts.toList())
        }

        Column(
            modifier = modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground
            )
        ) {

            // Header
            Header(definition.columns, definition.sorts)
            TableColumnDivider()

            // Scrollable Content
            MyScrollableLazyColumn(
                itemSpacing = 0.dp,
                overlapScrollbar = true
            ) {
                if (sortedList.isEmpty()) {
                    item(key = "EMPTY-KEY") {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            text = setup.emptyText,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                sortedList.forEachIndexed { index, item ->
                    item(key = definition.keyProvider(item.item)) {
                        Row(item, index, definition.columns, definition.selectedRows, setup)
                    }
                    if (index <= sortedList.lastIndex) {
                        item(key = "DIVIDER-$index") {
                            TableColumnDivider()
                        }
                    }
                }
            }
        }
    }
}
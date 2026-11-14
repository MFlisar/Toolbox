package com.michaelflisar.toolbox.table2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** All built-in text is replaceable; column titles and choice labels belong to column definitions. */
@Stable
data class TableTexts(
    val search: String,
    val filter: String,
    val resetFilters: String,
    val resetSort: String,
    val noData: String,
    val noMatches: String,
    val retry: String,
    val done: String,
    val all: String,
    val selectAll: String,
    val deselectAll: String,
    val checked: String,
    val unchecked: String,
    val caseSensitive: String,
    val invalidNumber: String,
    val rowsPerPage: String,
    val previousPage: String,
    val nextPage: String,
    val firstPage: String,
    val lastPage: String,
    val ascending: String,
    val descending: String,
    val clear: String,
    val selectedCount: (Int) -> String,
    val itemCount: (Int?, Int?) -> String,
    val pageRange: (Int, Int, Int) -> String,
    val operatorLabel: (TableFilterOperator) -> String,
    val sortDescription: (TableSortDirection, Int) -> String,
    val errorMessage: (Throwable) -> String
) {
    companion object {
        val English = TableTexts(
            search = "Search",
            filter = "Filter",
            resetFilters = "Reset filters",
            resetSort = "Reset sorting",
            noData = "No data",
            noMatches = "No matching rows",
            retry = "Retry",
            done = "Done",
            all = "All",
            selectAll = "Select all",
            deselectAll = "Deselect all",
            checked = "Checked",
            unchecked = "Unchecked",
            caseSensitive = "Case sensitive",
            invalidNumber = "Invalid number",
            rowsPerPage = "Rows per page",
            previousPage = "Previous page",
            nextPage = "Next page",
            firstPage = "First page",
            lastPage = "Last page",
            ascending = "Ascending",
            descending = "Descending",
            clear = "Clear",
            selectedCount = { "$it selected" },
            itemCount = { filtered, total ->
                if (filtered == null) "\u2026" else if (total != null && total != filtered) "$filtered / $total" else "$filtered"
            },
            pageRange = { first, last, total -> "$first\u2013$last / $total" },
            errorMessage = { it.message ?: it.toString() },
            operatorLabel = { operator ->
                when (operator) {
                    TableFilterOperator.Equal -> "Equal"
                    TableFilterOperator.NotEqual -> "Not equal"
                    TableFilterOperator.GreaterOrEqual -> "Greater than or equal"
                    TableFilterOperator.Greater -> "Greater than"
                    TableFilterOperator.LessOrEqual -> "Less than or equal"
                    TableFilterOperator.Less -> "Less than"
                    TableFilterOperator.StartsWith -> "Starts with"
                    TableFilterOperator.EndsWith -> "Ends with"
                    TableFilterOperator.Contains -> "Contains"
                    TableFilterOperator.NotContains -> "Does not contain"
                }
            },
            sortDescription = { direction, priority ->
                "${if (direction == TableSortDirection.Ascending) "Ascending" else "Descending"}, priority $priority"
            }
        )

        val German = TableTexts(
            search = "Suchen",
            filter = "Filter",
            resetFilters = "Filter zurücksetzen",
            resetSort = "Sortierung zurücksetzen",
            noData = "Keine Daten",
            noMatches = "Keine passenden Einträge",
            retry = "Erneut versuchen",
            done = "Fertig",
            all = "Alle",
            selectAll = "Alle auswählen",
            deselectAll = "Alle abwählen",
            checked = "Aktiviert",
            unchecked = "Nicht aktiviert",
            caseSensitive = "Groß-/Kleinschreibung beachten",
            invalidNumber = "Ungültige Zahl",
            rowsPerPage = "Zeilen pro Seite",
            previousPage = "Vorherige Seite",
            nextPage = "Nächste Seite",
            firstPage = "Erste Seite",
            lastPage = "Letzte Seite",
            ascending = "Aufsteigend",
            descending = "Absteigend",
            clear = "Löschen",
            selectedCount = { "$it ausgewählt" },
            itemCount = { filtered, total ->
                if (filtered == null) "\u2026" else if (total != null && total != filtered) "$filtered / $total" else "$filtered"
            },
            pageRange = { first, last, total -> "$first\u2013$last / $total" },
            errorMessage = { it.message ?: it.toString() },
            operatorLabel = { operator ->
                when (operator) {
                    TableFilterOperator.Equal -> "Gleich"
                    TableFilterOperator.NotEqual -> "Ungleich"
                    TableFilterOperator.GreaterOrEqual -> "Größer oder gleich"
                    TableFilterOperator.Greater -> "Größer als"
                    TableFilterOperator.LessOrEqual -> "Kleiner oder gleich"
                    TableFilterOperator.Less -> "Kleiner als"
                    TableFilterOperator.StartsWith -> "Beginnt mit"
                    TableFilterOperator.EndsWith -> "Endet mit"
                    TableFilterOperator.Contains -> "Enthält"
                    TableFilterOperator.NotContains -> "Enthält nicht"
                }
            },
            sortDescription = { direction, priority ->
                "${if (direction == TableSortDirection.Ascending) "Aufsteigend" else "Absteigend"}, Priorität $priority"
            }
        )
    }
}

@Composable
fun <Item> TableScope<Item>.TableToolbar(
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable RowScope.() -> Unit
) {
    Surface(color = containerColor, contentColor = contentColor) {
        Row(modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, content = content)
    }
}

@Composable
fun <Item> TableScope<Item>.TableResetSortButton() {
    TextButton(onClick = { state.reset(sorts = true) }, enabled = state.query.sorts.isNotEmpty()) { Text(texts.resetSort) }
}

/** Visibility affects controls only, never the active query or column capabilities. */
@Composable
fun <Item> TableScope<Item>.TableSearchBar(
    modifier: Modifier = Modifier,
    showTextSearch: Boolean = true,
    showColumnFilters: Boolean = true,
    filterPresentation: TableFilterPresentation = TableFilterPresentation.Dialog
) {
    if (!showTextSearch && !showColumnFilters) {
        HorizontalDivider(modifier.fillMaxWidth())
        return
    }

    Column(modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (showTextSearch) OutlinedTextField(
            value = state.query.search,
            onValueChange = state::search,
            label = { Text(texts.search) },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (showColumnFilters) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                table.columns.forEachIndexed { columnIndex, column ->
                    if (column.filter == null) return@forEachIndexed
                    val current = state.query.filters[column.id]
                    var open by remember(column.id) { mutableStateOf(false) }
                    Box {
                        InputChip(
                            selected = current != null,
                            onClick = { open = true },
                            label = { Text(column.title + (current?.let { ": ${filterSummary(column.filter, it, texts)}" } ?: "")) },
                            trailingIcon = if (current != null) {{
                                IconButton(onClick = { state.setFilter(column.id, null) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Clear, texts.clear, Modifier.size(16.dp))
                                }
                            }} else null
                        )
                        if (open) TableHeaderContext(table, column, columnIndex, texts).FilterEditor(filterPresentation) { open = false }
                    }
                }
                if (state.query.search.isNotEmpty() || state.query.filters.isNotEmpty()) {
                    TextButton(onClick = { state.reset(search = true, filters = true) }) { Text(texts.resetFilters) }
                }
            }
        }
    }
}

@Composable
fun <Item> TableHeaderContext<Item>.Label() { Text(column.title, maxLines = 1) }

@Composable
fun <Item> TableHeaderContext<Item>.SortIndicator() {
    val index = state.query.sorts.indexOfFirst { it.columnId == column.id }
    if (index >= 0) {
        val sort = state.query.sorts[index]
        Icon(
            if (sort.direction == TableSortDirection.Ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
            texts.sortDescription(sort.direction, index + 1), Modifier.size(16.dp)
        )
        if (state.query.sorts.size > 1) Text("${index + 1}", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun <Item> TableHeaderContext<Item>.FilterButton() {
    if (showFilter && column.filter != null) {
        var open by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { open = true }, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.FilterAlt, "${texts.filter}: ${column.title}", Modifier.size(20.dp),
                    tint = if (column.id in state.query.filters) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
            if (open) FilterEditor(filterPresentation) { open = false }
        }
    }
}

@Composable
internal fun <Item> TableHeaderContext<Item>.DefaultHeaderContent() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.weight(1f), contentAlignment = column.headerContentAlignment) { Label() }
        SortIndicator()
        FilterButton()
    }
}

@Composable
internal fun <Item> TableHeaderContext<Item>.HeaderMenu(onDismiss: () -> Unit) {
    var filterOpen by remember { mutableStateOf(false) }
    if (filterOpen) {
        FilterEditor(filterPresentation, onDismiss)
    } else DropdownMenu(expanded = true, onDismissRequest = onDismiss) {
        if (column.sortable) {
            val direction = state.query.sorts.find { it.columnId == column.id }?.direction
            DropdownMenuItem(
                text = { Text(texts.ascending) },
                leadingIcon = { Icon(Icons.Default.ArrowUpward, contentDescription = null) },
                trailingIcon = if (direction == TableSortDirection.Ascending) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null,
                onClick = {
                    state.setSort(column.id, TableSortDirection.Ascending); onDismiss()
                }
            )
            DropdownMenuItem(
                text = { Text(texts.descending) },
                leadingIcon = { Icon(Icons.Default.ArrowDownward, contentDescription = null) },
                trailingIcon = if (direction == TableSortDirection.Descending) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null,
                onClick = {
                    state.setSort(column.id, TableSortDirection.Descending); onDismiss()
                }
            )
            DropdownMenuItem(
                text = { Text(texts.resetSort) },
                leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null) },
                onClick = {
                    state.setSort(column.id, null); onDismiss()
                }
            )
        }
        if (showFilter && column.filter != null) {
            DropdownMenuItem(
                text = { Text(texts.filter) },
                leadingIcon = { Icon(Icons.Default.FilterAlt, contentDescription = null) },
                onClick = { filterOpen = true }
            )
        }
    }
}

@Composable
internal fun <Item> TableHeaderContext<Item>.FilterEditor(
    presentation: TableFilterPresentation,
    onDismiss: () -> Unit
) {
    val capability = requireNotNull(column.filter)
    val current = state.query.filters[column.id]
    val draft = if (presentation == TableFilterPresentation.Dialog) {
        remember(column.id, capability) { TableFilterDraft(capability, current) }
    } else {
        remember(column.id, capability, current) { TableFilterDraft(capability, current) }
    }
    fun change(value: TableFilterValue?) {
        draft.edit(value, presentation) { state.setFilter(column.id, it) }
    }
    val editor: @Composable () -> Unit = {
        Column(Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterFields(capability, draft.value, draft.valid, ::change)
        }
    }
    val clear: @Composable () -> Unit = {
        TextButton(onClick = { change(null) }, enabled = draft.canClear) { Text(texts.clear) }
    }
    val done: @Composable () -> Unit = {
        TextButton(
            enabled = draft.valid,
            onClick = {
                draft.commit { state.setFilter(column.id, it) }
                onDismiss()
            }
        ) { Text(texts.done) }
    }
    if (presentation == TableFilterPresentation.Dialog) AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${texts.filter}: ${column.title}") },
        confirmButton = done,
        dismissButton = clear,
        text = editor
    ) else DropdownMenu(expanded = true, onDismissRequest = onDismiss) {
        Column(Modifier.widthIn(min = 240.dp, max = 360.dp).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("${texts.filter}: ${column.title}", style = MaterialTheme.typography.titleMedium)
            editor()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                clear()
            }
        }
    }
}

@Composable
private fun <Item> TableHeaderContext<Item>.FilterFields(
    capability: TableFilter<*>,
    current: TableFilterValue?,
    valid: Boolean,
    onChange: (TableFilterValue?) -> Unit
) {
    when (capability) {
        is TableFilter.Text -> {
            val filter = current as? TableFilterValue.Text ?: TableFilterValue.Text("")
            ChoiceMenu(
                options = textOperators.toList(), selected = filter.operator, label = texts.operatorLabel
            ) { onChange(filter.copy(operator = it)) }
            OutlinedTextField(filter.text, onValueChange = {
                onChange(filter.copy(text = it))
            }, label = { Text(texts.filter) }, singleLine = true)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(!filter.ignoreCase, onCheckedChange = { onChange(filter.copy(ignoreCase = !it)) })
                Text(texts.caseSensitive)
            }
        }
        is TableFilter.Number -> {
            val filter = current as? TableFilterValue.Number ?: TableFilterValue.Number("")
            ChoiceMenu(TableFilterOperator.entries, filter.operator, texts.operatorLabel) {
                onChange(filter.copy(operator = it))
            }
            OutlinedTextField(
                filter.text, onValueChange = {
                    onChange(filter.copy(text = it))
                }, label = { Text(texts.filter) }, isError = !valid, singleLine = true,
                supportingText = { if (!valid) Text(texts.invalidNumber) }
            )
        }
        is TableFilter.Choice -> {
            val selected = (current as? TableFilterValue.Choices)?.keys.orEmpty()
            val labels = capability.labels()
            if (capability.multiSelect) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = { onChange(TableFilterValue.Choices(labels.keys)) },
                        enabled = labels.keys.any { it !in selected }
                    ) {
                        Icon(Icons.Default.SelectAll, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Text(texts.selectAll)
                    }
                    TextButton(onClick = { onChange(null) }, enabled = selected.isNotEmpty()) {
                        Icon(Icons.Default.Deselect, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Text(texts.deselectAll)
                    }
                }
            } else {
                TextButton(onClick = { onChange(null) }, enabled = selected.isNotEmpty()) { Text(texts.all) }
            }
            labels.forEach { (key, label) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val change: () -> Unit = {
                        val keys = if (capability.multiSelect) {
                            if (key in selected) selected - key else selected + key
                        } else setOf(key)
                        onChange(TableFilterValue.Choices(keys))
                    }
                    if (capability.multiSelect) Checkbox(key in selected, onCheckedChange = { change() })
                    else RadioButton(key in selected, onClick = change)
                    TextButton(onClick = change) { Text(label) }
                }
            }
        }
        is TableFilter.BooleanFilter -> {
            val selected = (current as? TableFilterValue.BooleanValue)?.value
            listOf(null to texts.all, true to texts.checked, false to texts.unchecked).forEach { (value, label) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected == value, onClick = {
                        onChange(value?.let { TableFilterValue.BooleanValue(it) })
                    })
                    Text(label)
                }
            }
        }
        is TableFilter.Custom -> {
            val editorScope = TableCustomFilterScope(current as? TableFilterValue.Custom, texts) { fields ->
                onChange(fields?.let { TableFilterValue.Custom(capability.type, it) })
            }
            capability.content(editorScope)
        }
    }
}

@Composable
internal fun <Value> ChoiceMenu(options: List<Value>, selected: Value, label: (Value) -> String, onSelect: (Value) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) { Text(label(selected)) }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(label(option)) }, onClick = { onSelect(option); expanded = false })
            }
        }
    }
}

private fun filterSummary(filter: TableFilter<*>?, value: TableFilterValue, texts: TableTexts): String = when (value) {
    is TableFilterValue.Text -> "${texts.operatorLabel(value.operator)} ${value.text}"
    is TableFilterValue.Number -> "${texts.operatorLabel(value.operator)} ${value.text}"
    is TableFilterValue.BooleanValue -> if (value.value) texts.checked else texts.unchecked
    is TableFilterValue.Choices -> {
        val labels = (filter as? TableFilter.Choice)?.labels().orEmpty()
        value.keys.joinToString { labels[it] ?: it }
    }
    is TableFilterValue.Custom -> (filter as? TableFilter.Custom)?.summary?.invoke(value.fields).orEmpty()
}

@Composable
fun <Item> TableScope<Item>.TableFooter(
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable RowScope.() -> Unit = {
        TableItemCount()
        if (state.selectedKeys.isNotEmpty()) { Spacer(Modifier.width(8.dp)); TableSelectionCount() }
        Spacer(Modifier.weight(1f))
        TablePagination()
    }
) = TableToolbar(modifier, containerColor, contentColor, content)

@Composable
fun <Item> TableScope<Item>.TableItemCount() {
    Text(texts.itemCount(table.totalCount, (table as? LocalTableModel)?.items?.size))
}

@Composable
fun <Item> TableScope<Item>.TableSelectionCount() { Text(texts.selectedCount(state.selectedKeys.size)) }

@Composable
fun <Item> TableScope<Item>.TablePagination(modifier: Modifier = Modifier) {
    val display = state.display as? TableDisplay.Paged ?: return
    val total = table.totalCount ?: return
    FlowRow(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(texts.rowsPerPage, Modifier.align(Alignment.CenterVertically))
        ChoiceMenu(display.pageSizes, display.pageSize, { "$it" }) { state.setDisplay(display.copy(pageSize = it)) }
        Text(
            texts.pageRange(if (total == 0) 0 else table.firstVisibleIndex + 1, table.firstVisibleIndex + table.visibleCount, total),
            Modifier.align(Alignment.CenterVertically)
        )
        IconButton(onClick = { table.goToPage(0) }, enabled = table.currentPage > 0) {
            Icon(Icons.Default.FirstPage, texts.firstPage)
        }
        IconButton(onClick = { table.goToPage(table.currentPage - 1) }, enabled = table.currentPage > 0) {
            Icon(Icons.Default.ChevronLeft, texts.previousPage)
        }
        IconButton(onClick = { table.goToPage(table.currentPage + 1) }, enabled = table.currentPage + 1 < table.pageCount) {
            Icon(Icons.Default.ChevronRight, texts.nextPage)
        }
        IconButton(onClick = { table.goToPage(table.pageCount - 1) }, enabled = table.currentPage + 1 < table.pageCount) {
            Icon(Icons.Default.LastPage, texts.lastPage)
        }
    }
}

@Composable
fun <Item> TableScope<Item>.TableEmptyContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val filtered = state.query.search.isNotEmpty() || state.query.filters.isNotEmpty()
        Text(if (filtered) texts.noMatches else texts.noData)
        if (filtered) TextButton(onClick = { state.reset(search = true, filters = true) }) { Text(texts.resetFilters) }
    }
}

@Composable
fun <Item> TableScope<Item>.TableErrorContent(error: TableError) {
    Column {
        Text(texts.errorMessage(error.error), color = MaterialTheme.colorScheme.error)
        TextButton(onClick = error::retry) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(texts.retry)
        }
    }
}

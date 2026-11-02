package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyDropdownDefaults
import com.michaelflisar.toolbox.components.MyDropdownIndex
import com.michaelflisar.toolbox.components.MyTooltipBox
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.table2.ColumnWidth
import com.michaelflisar.toolbox.table2.TableFooter
import com.michaelflisar.toolbox.table2.TableEmptyContent
import com.michaelflisar.toolbox.table2.TableErrorContent
import com.michaelflisar.toolbox.table2.TableItemCount
import com.michaelflisar.toolbox.table2.TablePagination
import com.michaelflisar.toolbox.table2.ProviderTableModel
import com.michaelflisar.toolbox.table2.TableResetSortButton
import com.michaelflisar.toolbox.table2.TableSearchBar
import com.michaelflisar.toolbox.table2.TableSelectionCount
import com.michaelflisar.toolbox.table2.Table
import com.michaelflisar.toolbox.table2.TableCache
import com.michaelflisar.toolbox.table2.TableColumn
import com.michaelflisar.toolbox.table2.TableDataProvider
import com.michaelflisar.toolbox.table2.TableDisplay
import com.michaelflisar.toolbox.table2.TableFilter
import com.michaelflisar.toolbox.table2.TableFilterPresentation
import com.michaelflisar.toolbox.table2.TableHeaderInteraction
import com.michaelflisar.toolbox.table2.TableInteraction
import com.michaelflisar.toolbox.table2.TableLazyConfig
import com.michaelflisar.toolbox.table2.TableModel
import com.michaelflisar.toolbox.table2.TableQuery
import com.michaelflisar.toolbox.table2.TableState
import com.michaelflisar.toolbox.table2.TableTexts
import com.michaelflisar.toolbox.table2.TableToolbar
import com.michaelflisar.toolbox.table2.applyTableQuery
import com.michaelflisar.toolbox.table2.booleanColumn
import com.michaelflisar.toolbox.table2.customColumn
import com.michaelflisar.toolbox.table2.enumColumn
import com.michaelflisar.toolbox.table2.numberColumn
import com.michaelflisar.toolbox.table2.numberFilter
import com.michaelflisar.toolbox.table2.rememberTable
import com.michaelflisar.toolbox.table2.rememberTableState
import com.michaelflisar.toolbox.table2.textColumn
import com.michaelflisar.toolbox.table2.textFilter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Parcelize
object PageTestTable2 : NavScreen() {
    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "Table 2 Test",
        icon = Icons.Default.TableRows.toIconComposable()
    )

    @Composable
    override fun Screen() {
        Table2Page()
    }
}

@Composable
private fun Table2Page() {
    val providerMode = remember { mutableStateOf(false) }
    key(providerMode.value) {
        Table2SourcePage(providerMode)
    }
}

@Composable
private fun Table2SourcePage(
    providerMode: MutableState<Boolean>,
) {

    val appState = LocalAppState.current

    // 1) setting states
    val autoWidth = remember { mutableStateOf(false) }
    val headerInteractionMode = remember { mutableStateOf(1) }
    val interactionMode = remember { mutableStateOf(0) }
    val message = remember { mutableStateOf("") }
    val failNextCount = remember { mutableStateOf(false) }
    val failNextLoad = remember { mutableStateOf(false) }
    val showTextSearch = remember { mutableStateOf(true) }
    val showToolbarFilters = remember { mutableStateOf(true) }
    val showHeaderFilters = remember { mutableStateOf(true) }
    val toolbarFilterPresentation = remember { mutableStateOf(TableFilterPresentation.Dialog) }
    val headerFilterPresentation = remember { mutableStateOf(TableFilterPresentation.Dialog) }
    val customEmpty = remember { mutableStateOf(false) }
    val customErrors = remember { mutableStateOf(false) }

    // 2) table states

    val state = rememberTableState(TableDisplay.Paged())
    val columns = table2Columns(autoWidth.value)

    val table: TableModel<Table2Item> = if (providerMode.value) {
        val currentColumns = rememberUpdatedState(columns)
        val provider = remember {
            val generation = ++table2Generation
            DemoTableProvider(
                size = 1000,
                createItems = { start, count -> table2Items(start, count, generation) },
                failNextCount = failNextCount,
                failNextLoad = failNextLoad
            ) { currentColumns.value }
        }
        rememberTable(
            provider = provider,
            columns = columns,
            state = state,
            lazyConfig = TableLazyConfig(cache = TableCache.Lru(5))
        ) { it.id }
    } else {
        rememberTable(
            initialItems = { table2Items(0, 1000) },
            columns = columns,
            state = state
        ) { it.id }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ----------------------
        // content above the table
        // ----------------------

        SetupRow(
            table = table,
            state = state,
            providerMode = providerMode,
            autoWidth = autoWidth,
            headerInteractionMode = headerInteractionMode,
            interactionMode = interactionMode,
            failNextCount = failNextCount,
            failNextLoad = failNextLoad,
            showTextSearch = showTextSearch,
            showToolbarFilters = showToolbarFilters,
            showHeaderFilters = showHeaderFilters,
            toolbarFilterPresentation = toolbarFilterPresentation,
            headerFilterPresentation = headerFilterPresentation,
            customErrors = customErrors,
            customEmpty = customEmpty
        )

        // ----------------------
        // table
        // ----------------------

        Table(
            table = table,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            interaction = when (interactionMode.value) {
                1 -> TableInteraction.Select()
                2 -> TableInteraction.RowClick {
                    appState.showSnackbar("Row clicked", "Row: ${it.name}")
                }

                3 -> TableInteraction.CellClick { item, column ->
                    appState.showSnackbar(
                        "Cell clicked",
                        "Row: ${item.name}, Column: ${column.title}"
                    )
                }

                else -> TableInteraction.None()
            },
            texts = TableTexts.English,
            showHeaderFilters = showHeaderFilters.value,
            headerFilterPresentation = headerFilterPresentation.value,
            emptyContent = {
                Column {
                    if (customEmpty.value)
                        Text("Provider empty")
                    TableEmptyContent()
                }
            },
            errorContent = { error ->
                Column {
                    if (customErrors.value)
                        Text("Provider error: ${error.kind}${error.index?.let { " at row $it" }.orEmpty()}")
                    TableErrorContent(error)
                }
            },
            headerInteraction = when (headerInteractionMode.value) {
                1 -> TableHeaderInteraction.SortOnClick
                2 -> TableHeaderInteraction.MenuOnClick
                3 -> TableHeaderInteraction.Click { column ->
                    appState.showSnackbar("Header clicked", "Column: ${column.title}")
                }

                else -> TableHeaderInteraction.None
            },
            header = {

                TableToolbar {
                    Text(
                        text = "Table 2 - Table title",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.weight(1f))
                    TableResetSortButton()
                }

                TableSearchBar(
                    showTextSearch = showTextSearch.value,
                    showColumnFilters = showToolbarFilters.value,
                    filterPresentation = toolbarFilterPresentation.value
                )
            },
            footer = {
                TableFooter {

                    TableItemCount()
                    Spacer(Modifier.width(8.dp))
                    Text("|")
                    Spacer(Modifier.width(8.dp))
                    TableSelectionCount()

                    Spacer(Modifier.weight(1f))
                    TablePagination()
                }
            },
            headerCell = { cell ->
                if (cell.columnIndex in listOf(5, 6)) {
                    MyTooltipBox(
                        tooltip = {
                            Text(cell.column.title)
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            cell.Content()
                        }
                    }
                } else {
                    cell.Content()
                }
            },
            cell = { it.Content() }
        )

        // ----------------------
        // content below the table
        // ----------------------

        Text(
            "Selected keys: ${
                state.selectedKeys.take(10).joinToString()
            } | Cached selected items: ${table.loadedSelectedItems.size} | message: $message",
        )
    }
}

@Composable
private fun SetupRow(
    table: TableModel<Table2Item>,
    state: TableState,
    providerMode: MutableState<Boolean>,
    autoWidth: MutableState<Boolean>,
    headerInteractionMode: MutableState<Int>,
    interactionMode: MutableState<Int>,
    failNextCount: MutableState<Boolean>,
    failNextLoad: MutableState<Boolean>,
    showTextSearch: MutableState<Boolean>,
    showToolbarFilters: MutableState<Boolean>,
    showHeaderFilters: MutableState<Boolean>,
    toolbarFilterPresentation: MutableState<TableFilterPresentation>,
    headerFilterPresentation: MutableState<TableFilterPresentation>,
    customErrors: MutableState<Boolean>,
    customEmpty: MutableState<Boolean>,
) {
    val scope = rememberCoroutineScope()
    val appState = LocalAppState.current
    var replacing by remember { mutableStateOf(false) }
    fun replaceData(empty: Boolean) {
        scope.launch {
            replacing = true
            try {
                if (empty) table.clear() else table.replaceAll(table2Items(0, 1000))
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                appState.showSnackbar("Data replacement failed", error.message ?: error.toString())
            } finally {
                replacing = false
            }
        }
    }

    MyColumn {
        Text("Table settings and test actions", style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            CheckableChip(
                title = "Suspend provider",
                checked = providerMode.value,
                onCheckedChange = { providerMode.value = it }
            )
            CheckableChip(
                title = "Paged",
                checked = state.display is TableDisplay.Paged,
                onCheckedChange = { paged ->
                    state.setDisplay(if (paged) TableDisplay.Paged() else TableDisplay.Continuous)
                }
            )
            CheckableChip(
                title = "Auto name width",
                checked = autoWidth.value,
                onCheckedChange = { autoWidth.value = it }
            )
            MyDropdownIndex(
                title = "Header interaction",
                items = listOf("None", "Sort on click", "Menu on click", "Header click"),
                selectedIndex = headerInteractionMode,
                style = MyDropdownDefaults.styleOutlinedButton()
            )
            MyDropdownIndex(
                title = "Cell Click",
                items = listOf("No clicks", "Select", "Row click", "Cell click"),
                selectedIndex = interactionMode,
                style = MyDropdownDefaults.styleOutlinedButton()
            )

            OutlinedButton(
                onClick = { replaceData(false) },
                enabled = !replacing
            ) { Text("New data") }
            OutlinedButton(
                onClick = { replaceData(true) },
                enabled = !replacing
            ) { Text("Empty data") }

            if (table is ProviderTableModel) {
                OutlinedButton(
                    onClick = table::refresh
                ) { Text("Refresh") }
                OutlinedButton(
                    onClick = {
                        failNextCount.value = true
                        table.refresh()
                    }
                ) { Text("Fail count") }
                OutlinedButton(onClick = {
                    failNextLoad.value = true
                    table.refresh()
                }) { Text("Fail range") }
            }
        }
        Text("State resets", style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = { state.reset() }) { Text("Reset state (all)") }
            OutlinedButton(onClick = { state.reset(search = true) }) { Text("Reset search") }
            OutlinedButton(onClick = { state.reset(filters = true) }) { Text("Reset filters") }
            OutlinedButton(onClick = { state.reset(sorts = true) }) { Text("Reset sorts") }
            OutlinedButton(onClick = { state.reset(selection = true) }) { Text("Reset selection") }
            OutlinedButton(onClick = { state.reset(position = true) }) { Text("Reset position") }
        }

        Text("Options", style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            CheckableChip("Text search", showTextSearch.value) { showTextSearch.value = it }
            CheckableChip("Toolbar filters", showToolbarFilters.value) {
                showToolbarFilters.value = it
            }
            CheckableChip("Header filters", showHeaderFilters.value) {
                showHeaderFilters.value = it
            }
            CheckableChip("Custom errors", customErrors.value) { customErrors.value = it }
            CheckableChip("Custom empty", customEmpty.value) { customEmpty.value = it }
            MyDropdown(
                title = "Toolbar filters",
                items = TableFilterPresentation.entries,
                selected = toolbarFilterPresentation.value,
                style = MyDropdownDefaults.styleOutlinedButton(),
                onSelectionChange = { _, value -> toolbarFilterPresentation.value = value }
            )
            MyDropdown(
                title = "Header filters",
                items = TableFilterPresentation.entries,
                selected = headerFilterPresentation.value,
                style = MyDropdownDefaults.styleOutlinedButton(),
                onSelectionChange = { _, value -> headerFilterPresentation.value = value }
            )
        }
    }
}

// ------------------------------
// Klassen und Daten für die Demo
// ------------------------------

private data class Table2Item(
    val id: Int,
    val name: String,
    val description: String,
    val type: Table2Type,
    val checked: MutableState<Boolean>,
)

private enum class Table2Type { First, Second, Third }

private var table2Generation = 0L

private fun table2Items(
    from: Int,
    size: Int,
    generation: Long = ++table2Generation,
): List<Table2Item> {
    require(from >= 0 && size >= 0 && from.toLong() + size <= Int.MAX_VALUE.toLong() + 1)
    return List(size) { offset ->
        val id = from + offset
        Table2Item(
            id,
            "Item $id - dataset $generation",
            "Description of item $id from dataset $generation",
            Table2Type.entries[id % Table2Type.entries.size],
            mutableStateOf(id % 2 == 0)
        )
    }
}

@Composable
private fun table2Columns(
    autoWidth: Boolean,
): List<TableColumn<Table2Item, *>> {
    val columns = remember(autoWidth) {
        listOf<TableColumn<Table2Item, *>>(
            numberColumn(
                id = "id",
                title = "ID",
                value = { it.id },
                width = ColumnWidth.Fixed(80.dp),
                filter = numberFilter(String::toIntOrNull)
            ),
            textColumn(
                id = "name",
                title = "Name",
                value = { it.name },
                width = if (autoWidth) ColumnWidth.Auto() else ColumnWidth.Weight(minWidth = 160.dp),
                filter = textFilter()
            ),
            textColumn(
                id = "description",
                title = "Description",
                value = { it.description },
                width = ColumnWidth.Weight(minWidth = 200.dp),
                filter = textFilter()
            ),
            enumColumn(
                id = "type",
                title = "Type",
                value = { item: Table2Item -> item.type },
                width = ColumnWidth.Fixed(150.dp),
                comparator = compareBy { it.ordinal },
                filter = TableFilter.Choice(Table2Type.entries, { it.name }, multiSelect = true),
                cellContent = { Text(it.value.name) }
            ),
            booleanColumn(
                id = "checked",
                title = "Checked",
                value = { item: Table2Item -> item.checked.value },
                width = ColumnWidth.Fixed(130.dp),
                comparator = naturalOrder(),
                filter = TableFilter.BooleanFilter(),
                searchText = null, //{ if (it) "Checked 1" else "Unchecked 1" },
                cellContent = { cell -> Checkbox(cell.value, onCheckedChange = { cell.item.checked.value = it }) },
                cellContentAlignment = Alignment.Center,
            ),
            customColumn(
                id = "icon",
                title = "Icon",
                value = { _: Table2Item -> Icons.Default.Info },
                width = ColumnWidth.Fixed(64.dp), searchText = null,
                headerContent = { Icon(Icons.Default.Info, "Icon") },
                cellContent = { Icon(it.value, null) },
                headerContentAlignment = Alignment.Center,
                cellContentAlignment = Alignment.Center,
            ),
            customColumn(
                id = "delete",
                title = "Delete",
                value = { item: Table2Item -> item.id },
                width = ColumnWidth.Fixed(64.dp), searchText = null,
                headerContent = { Icon(Icons.Default.Delete, "Delete") },
                cellContent = { cell ->
                    val scope = rememberCoroutineScope()
                    val appState = LocalAppState.current
                    IconButton(onClick = {
                        scope.launch {
                            try {
                                cell.table.remove(cell.value)
                            } catch (error: CancellationException) {
                                throw error
                            } catch (error: Exception) {
                                appState.showSnackbar(
                                    "Delete failed",
                                    error.message ?: error.toString()
                                )
                            }
                        }
                    }) {
                        Icon(Icons.Default.Delete, "Delete ${cell.item.name}")
                    }
                },
                headerContentAlignment = Alignment.Center,
                cellContentAlignment = Alignment.Center,
            )
        )
    }
    return columns
}

/** The generator must return stable values for each absolute ID throughout this source lifetime. */
private class DemoTableProvider(
    size: Int,
    private val createItems: (start: Int, count: Int) -> List<Table2Item>,
    val failNextCount: MutableState<Boolean>,
    val failNextLoad: MutableState<Boolean>,
    val columns: () -> List<TableColumn<Table2Item, *>>,
) : TableDataProvider<Table2Item> {

    private var generatedSize = size
    private val removed = mutableSetOf<Int>()
    private val overrides = linkedMapOf<Int, Table2Item>()
    private val checkedStates = mutableMapOf<Int, MutableState<Boolean>>()

    init {
        require(size >= 0)
    }

    private fun contains(id: Int) =
        id in overrides || (id in 0 until generatedSize && id !in removed)

    private fun ids(): List<Int> =
        (0 until generatedSize).filter { it !in removed } +
                overrides.keys.filter { it !in 0 until generatedSize }

    private fun itemsFor(ids: List<Int>): List<Table2Item> {
        val result = ArrayList<Table2Item>(ids.size)
        var position = 0
        while (position < ids.size) {
            val id = ids[position]
            val replacement = overrides[id]
            if (replacement != null) {
                result += replacement
                position++
            } else {
                var end = position + 1
                while (end < ids.size && ids[end] == ids[end - 1] + 1 && ids[end] !in overrides) end++
                val generated = createItems(id, end - position)
                require(generated.size == end - position && generated.indices.all { generated[it].id == ids[position + it] }) {
                    "Generator must return the requested number of items with absolute index IDs"
                }
                result += generated.map { item ->
                    item.copy(checked = checkedStates.getOrPut(item.id) { item.checked })
                }
                position = end
            }
        }
        return result
    }

    override suspend fun insert(item: Table2Item) {
        require(!contains(item.id))
        overrides[item.id] = item
        removed.remove(item.id)
    }

    override suspend fun update(key: Any, item: Table2Item) {
        require(key == item.id && contains(item.id))
        overrides[item.id] = item
    }

    override suspend fun remove(key: Any) {
        require(key is Int && contains(key))
        overrides.remove(key)
        checkedStates.remove(key)
        if (key in 0 until generatedSize) removed += key
    }

    override suspend fun replaceAll(items: List<Table2Item>) {
        require(items.map { it.id }.distinct().size == items.size)
        generatedSize = 0
        removed.clear()
        checkedStates.clear()
        overrides.clear()
        items.forEach { overrides[it.id] = it }
    }

    override suspend fun count(query: TableQuery): Long {
        delay(250.milliseconds)
        if (failNextCount.value) {
            failNextCount.value = false
            error("Simulated count failure")
        }
        val ids = ids()
        return if (query.search.isEmpty() && query.filters.isEmpty()) ids.size.toLong()
        else applyTableQuery(
            itemsFor(ids),
            columns(),
            query.copy(sorts = emptyList())
        ).size.toLong()
    }

    override suspend fun load(query: TableQuery, offset: Long, limit: Int): List<Table2Item> {
        delay(350.milliseconds)
        if (failNextLoad.value) {
            failNextLoad.value = false
            error("Simulated range failure")
        }
        require(offset >= 0 && offset <= Int.MAX_VALUE && limit > 0)
        val ids = ids()
        return if (query.search.isEmpty() && query.filters.isEmpty() && query.sorts.isEmpty()) {
            itemsFor(ids.drop(offset.toInt()).take(limit))
        } else {
            // Arbitrary Kotlin filters/comparators require global evaluation in this demo source.
            applyTableQuery(itemsFor(ids), columns(), query).drop(offset.toInt()).take(limit)
        }
    }
}

@Composable
private fun CheckableChip(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ElevatedFilterChip(
        selected = checked,
        onClick = { onCheckedChange(!checked) },
        label = { Text(title) },
        leadingIcon = if (checked) {
            {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null
    )
}

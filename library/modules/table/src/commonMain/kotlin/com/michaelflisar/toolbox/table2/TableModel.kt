package com.michaelflisar.toolbox.table2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Global query semantics must be implemented by the provider, including deterministic tie ordering.
 * count/load are not a transactional snapshot. Call refresh after external mutations.
 * Implementations own their backing source and must cooperate with coroutine cancellation.
 * Mutations must persist before returning, and throw on failure. They must not merely edit a cache.
 */
interface TableDataProvider<Item> {
    suspend fun count(query: TableQuery): Long
    suspend fun load(query: TableQuery, offset: Long, limit: Int): List<Item>
    /** Persist the mutation, or throw. Updating must preserve the item's stable key. */
    suspend fun insert(item: Item)
    suspend fun update(key: Any, item: Item)
    suspend fun remove(key: Any)
    /** Replace the entire backing source, not just loaded or filtered rows. Persist or throw. */
    suspend fun replaceAll(items: List<Item>)
}

/**
 * Bound data and interaction state. Indices refer to the full filtered/sorted result, not a page.
 * The local model owns its initial list; a provider model delegates mutations to its source.
 * Provider models expose only cached items; use selectedKeys for a complete provider selection.
 * One state/model represents one data source; do not share a state between independent tables.
 */
abstract class TableModel<Item>(
    columns: List<TableColumn<Item, *>>,
    val state: TableState,
    val itemKey: (Item) -> Any
) {
    private var columnValues by mutableStateOf(columns)
    val columns: List<TableColumn<Item, *>> get() = columnValues
    internal fun updateColumns(value: List<TableColumn<Item, *>>) {
        require(value.map { it.id }.distinct().size == value.size) { "Column IDs must be unique" }
        columnValues = value
    }
    init { require(columns.map { it.id }.distinct().size == columns.size) { "Column IDs must be unique" } }
    abstract val totalCount: Int?
    /** Loaded items only for providers; all query results for local lists. */
    abstract val loadedItems: List<Item>
    abstract val isLoading: Boolean
    abstract val error: Throwable?
    abstract fun itemAt(index: Int): Item?
    /** Request an inclusive absolute range. Local models already have all items. */
    abstract fun requestRange(first: Int, last: Int)
    abstract fun rangeError(index: Int): Throwable?
    internal abstract fun retryLoad()
    /** Mutations complete after the source accepts them. Failures propagate to the caller. */
    abstract suspend fun insert(item: Item)
    abstract suspend fun update(key: Any, item: Item)
    abstract suspend fun remove(key: Any)
    /** Replace all source rows, preserving query/display settings and selection of retained keys. */
    abstract suspend fun replaceAll(items: List<Item>)
    suspend fun clear() = replaceAll(emptyList())
    internal fun validateItems(items: List<Item>) {
        require(items.all { it != null }) { "Items must not be null" }
        require(items.map(itemKey).distinct().size == items.size) { "Item keys must be unique" }
    }
    internal abstract val dataVersion: Int
    val pageCount: Int get() {
        val size = (state.display as? TableDisplay.Paged)?.pageSize ?: return 1
        val count = totalCount ?: return 0
        return if (count == 0) 0 else (count - 1) / size + 1
    }
    val currentPage: Int get() = state.page.coerceAtMost((pageCount - 1).coerceAtLeast(0))
    val firstVisibleIndex: Int get() = when (val display = state.display) {
        is TableDisplay.Continuous -> 0
        is TableDisplay.Paged -> currentPage * display.pageSize
    }
    val visibleCount: Int get() {
        val total = totalCount ?: return 0
        return when (val display = state.display) {
            is TableDisplay.Continuous -> total
            is TableDisplay.Paged -> minOf(display.pageSize, total - firstVisibleIndex)
        }
    }
    val loadedSelectedItems: List<Item> get() = loadedItems.filter { itemKey(it) in state.selectedKeys }
    fun goToPage(page: Int) = state.setPage(page.coerceIn(0, (pageCount - 1).coerceAtLeast(0)))
}

class LocalTableModel<Item> internal constructor(
    items: List<Item>,
    columns: List<TableColumn<Item, *>>,
    state: TableState,
    itemKey: (Item) -> Any
) : TableModel<Item>(columns, state, itemKey) {
    var items by mutableStateOf(items.toList())
        private set
    init {
        require(items.all { it != null }) { "Items must not be null; null represents an unloaded provider row" }
        require(items.map(itemKey).distinct().size == items.size) { "Item keys must be unique" }
    }
    val filteredItems by derivedStateOf {
        dataVersion
        applyTableQuery(this.items, this.columns, state.query.forColumns(this.columns).copy(sorts = emptyList()))
    }
    val sortedItems by derivedStateOf {
        dataVersion
        applyTableQuery(this.items, this.columns, state.query.forColumns(this.columns))
    }
    val selectedItems: List<Item> get() = items.filter { itemKey(it) in state.selectedKeys }
    override val loadedItems: List<Item> get() = sortedItems
    override val totalCount: Int get() = sortedItems.size
    override val isLoading: Boolean get() = false
    override val error: Throwable? get() = null
    override var dataVersion by mutableStateOf(0)
        private set
    override fun itemAt(index: Int): Item? = sortedItems.getOrNull(index)
    override fun requestRange(first: Int, last: Int) = Unit
    override fun rangeError(index: Int): Throwable? = null
    internal override fun retryLoad(): Unit = error("Local tables have no load failures to retry")
    override suspend fun insert(item: Item) {
        require(item != null)
        require(items.none { itemKey(it) == itemKey(item) }) { "Item key already exists" }
        items = items + item
        reconcileMutation()
    }
    override suspend fun update(key: Any, item: Item) {
        require(item != null && itemKey(item) == key) { "Updating must preserve the item key" }
        require(items.any { itemKey(it) == key }) { "Unknown item key: $key" }
        items = items.map { if (itemKey(it) == key) item else it }
        reconcileMutation()
    }
    override suspend fun remove(key: Any) {
        require(items.any { itemKey(it) == key }) { "Unknown item key: $key" }
        items = items.filterNot { itemKey(it) == key }
        state.select(key, false)
        reconcileMutation()
    }
    override suspend fun replaceAll(items: List<Item>) {
        val replacement = items.toList()
        validateItems(replacement)
        this.items = replacement
        state.retainSelection(replacement.map(itemKey).toSet())
        reconcileMutation()
    }
    private fun reconcileMutation() {
        dataVersion++
        if (state.page != currentPage) state.setPage(currentPage)
    }
}

/**
 * Initial items are evaluated once per model/state lifetime, not when columns or UI recompose.
 * Change data using insert/update/remove. Use a new composition key/state to switch data sources.
 * Keep item keys stable throughout this model's lifetime.
 */
@Composable
fun <Item> rememberTable(
    initialItems: () -> List<Item>,
    columns: List<TableColumn<Item, *>>,
    state: TableState = rememberTableState(),
    itemKey: (Item) -> Any
): LocalTableModel<Item> {
    val latestKey = androidx.compose.runtime.rememberUpdatedState(itemKey)
    val table = remember(state) {
        LocalTableModel(initialItems(), columns, state) { latestKey.value(it) }
    }
    SideEffect {
        table.updateColumns(columns)
        state.reconcileColumns(columns)
        state.retainSelection(table.items.map(itemKey).toSet())
        if (state.page != table.currentPage) state.setPage(table.currentPage)
    }
    return table
}

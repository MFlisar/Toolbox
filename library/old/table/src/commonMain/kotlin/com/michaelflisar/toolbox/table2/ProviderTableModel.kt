package com.michaelflisar.toolbox.table2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A counted, suspend-backed table. The provider owns global filtering and sorting; cached data is
 * only a bounded window into that result. [dispose] cancels this model's jobs, never its scope.
 * Providers must not return null items, even when `Item` is declared nullable; null denotes an
 * unloaded placeholder to table consumers.
 */
class ProviderTableModel<Item> internal constructor(
    private val provider: TableDataProvider<Item>,
    columns: List<TableColumn<Item, *>>,
    state: TableState,
    itemKey: (Item) -> Any,
    private val scope: CoroutineScope,
    lazyConfig: TableLazyConfig = TableLazyConfig()
) : TableModel<Item>(columns, state, itemKey) {

    private data class Entry<Item>(val items: List<Item>, val lastAccess: Long)

    private var observedQuery by mutableStateOf<TableQuery?>(null)
    private var observedDisplay by mutableStateOf<TableDisplay?>(null)
    private var lazyConfigValue by mutableStateOf(lazyConfig)
    val lazyConfig: TableLazyConfig get() = lazyConfigValue
    private var observedLazyConfig by mutableStateOf<TableLazyConfig?>(null)

    internal fun updateLazyConfig(value: TableLazyConfig) { lazyConfigValue = value }
    private var generation = 0
    private var layoutGeneration = 0
    private var requestId = 0L
    private var accessClock = 0L
    private var reconciledShortResult = false
    private var countJob: Job? = null
    private var observerJob: Job? = null
    private val rangeJobs = mutableMapOf<Int, Job>()
    private var protectedUnits: Set<Int> = emptySet()
    private var lastRequestedRange: IntRange? = null
    private var lastDirection = 1
    private var disposed = false
    private val mutationMutex = Mutex()

    override suspend fun insert(item: Item) = mutate {
        require(item != null) { "Item must not be null" }
        provider.insert(item)
    }

    override suspend fun update(key: Any, item: Item) = mutate {
        require(item != null && itemKey(item) == key) { "Updating must preserve the item key" }
        provider.update(key, item)
    }

    override suspend fun remove(key: Any) = mutate {
        provider.remove(key)
        state.select(key, false)
    }

    override suspend fun replaceAll(items: List<Item>) = mutate {
        val replacement = items.toList()
        validateItems(replacement)
        provider.replaceAll(replacement)
        state.retainSelection(replacement.map(itemKey).toSet())
    }

    private suspend fun mutate(action: suspend () -> Unit) {
        mutationMutex.withLock {
            check(!disposed) { "Table model is disposed" }
            action()
            refresh()
        }
    }

    private var entries by mutableStateOf<Map<Int, Entry<Item>>>(emptyMap())
    private var rangeErrors by mutableStateOf<Map<Int, Throwable>>(emptyMap())
    private var countValue by mutableStateOf<Int?>(null)
    private var countError by mutableStateOf<Throwable?>(null)
    private var counting by mutableStateOf(false)
    private var loadingRanges by mutableStateOf(false)

    override val totalCount: Int? get() = if (stateIsObserved()) countValue else null
    override val loadedItems: List<Item>
        get() = if (stateIsObserved()) entries.entries.sortedBy { it.key }.flatMap { it.value.items } else emptyList()
    override val isLoading: Boolean get() = counting || loadingRanges
    override val error: Throwable? get() = if (stateIsObserved()) countError else null
    override var dataVersion by mutableStateOf(0)
        private set

    init {
        observerJob = scope.launch {
            snapshotFlow { Triple(state.query, state.display, this@ProviderTableModel.lazyConfig) }.collect { (query, display, config) ->
                observe(query, display, config)
            }
        }
    }

    override fun itemAt(index: Int): Item? {
        if (!stateIsObserved() || index < 0 || index >= (countValue ?: return null)) return null
        val unit = index / unitSize(state.display)
        val entry = entries[unit] ?: return null
        return entry.items.getOrNull(index - unit * unitSize(state.display))
    }

    override fun requestRange(first: Int, last: Int) {
        if (disposed || !stateIsObserved() || first < 0 || last < first) return
        val count = countValue ?: return
        if (count == 0 || first >= count) {
            protectedUnits = emptySet()
            return
        }
        val end = minOf(last, count - 1)
        val range = first..end
        lastRequestedRange?.let {
            if (range.first != it.first) lastDirection = if (range.first > it.first) 1 else -1
        }
        lastRequestedRange = range

        val size = unitSize(state.display)
        val firstUnit = first / size
        val lastUnit = end / size
        val visible = (firstUnit..lastUnit).toSet()
        protectedUnits = visible
        val requested = visible.toMutableList()
        val prefetch = prefetchCount(state.display)
        val finalUnit = (count - 1) / size
        for (distance in 1..prefetch) {
            val candidate = if (lastDirection >= 0) lastUnit.toLong() + distance
                else firstUnit.toLong() - distance
            if (candidate >= 0L && candidate <= finalUnit.toLong()) requested += candidate.toInt()
        }
        for (distance in 1..prefetch) {
            val candidate = if (lastDirection >= 0) firstUnit.toLong() - distance
                else lastUnit.toLong() + distance
            if (candidate >= 0L && candidate <= finalUnit.toLong()) requested += candidate.toInt()
        }

        requested.distinct().forEach { unit ->
            val cached = entries[unit]
            if (cached != null) {
                if (unit in visible) touch(unit, cached)
            } else if (unit !in rangeJobs && unit !in rangeErrors) {
                loadUnit(unit, generation, layoutGeneration, state.query, state.display)
            }
        }
        trimCache()
    }

    override fun rangeError(index: Int): Throwable? {
        if (!stateIsObserved() || index < 0) return null
        return rangeErrors[index / unitSize(state.display)]
    }

    internal override fun retryLoad() = retry()

    /** Retry failed requests without discarding successfully cached ranges. */
    fun retry() {
        if (disposed || !stateIsObserved()) return
        countError = null
        if (countValue == null) {
            startCount(generation, state.query)
            return
        }
        lastRequestedRange?.let { range ->
            val size = unitSize(state.display)
            val firstUnit = range.first / size
            val lastUnit = range.last / size
            rangeErrors = rangeErrors - (firstUnit..lastUnit).toSet()
        }
        lastRequestedRange?.let { requestRange(it.first, it.last) }
    }

    /** Invalidate cached results and recount the current source, preserving query settings. */
    fun refresh() {
        if (disposed) return
        dataVersion++
        invalidateRanges()
        countValue = null
        countError = null
        reconciledShortResult = false
        startCount(generation, state.query)
    }

    /** Cancels only jobs started by this model. Safe to call more than once. */
    fun dispose() {
        if (disposed) return
        disposed = true
        observerJob?.cancel()
        observerJob = null
        countJob?.cancel()
        countJob = null
        rangeJobs.values.forEach { it.cancel() }
        rangeJobs.clear()
        counting = false
        updateLoading()
    }

    private fun observe(query: TableQuery, display: TableDisplay, config: TableLazyConfig) {
        if (disposed) return
        val oldQuery = observedQuery
        val oldDisplay = observedDisplay
        val oldConfig = observedLazyConfig
        observedQuery = query
        observedDisplay = display
        observedLazyConfig = config
        if (oldQuery == null) {
            beginQuery(query)
            return
        }
        if (query != oldQuery) {
            dataVersion++
            invalidateRanges()
            countValue = null
            countError = null
            reconciledShortResult = false
            beginQuery(query, countAlreadyInvalidated = true)
        } else if (display != oldDisplay || config != oldConfig) {
            layoutGeneration++
            cancelRanges()
            rangeErrors = emptyMap()
            protectedUnits = if (display is TableDisplay.Paged) setOf(state.page) else emptySet()
            lastRequestedRange = null
            entries = when {
                oldDisplay is TableDisplay.Paged && display is TableDisplay.Paged &&
                    oldDisplay.pageSize != display.pageSize ->
                    convertPages(entries, countValue, oldDisplay, display)
                oldDisplay is TableDisplay.Paged && display is TableDisplay.Paged -> entries
                oldDisplay is TableDisplay.Continuous && display is TableDisplay.Continuous &&
                    oldConfig?.blockSize == config.blockSize -> entries
                else -> emptyMap()
            }
            trimCache()
        }
    }

    private fun beginQuery(query: TableQuery, countAlreadyInvalidated: Boolean = false) {
        if (!countAlreadyInvalidated) invalidateRanges()
        countValue = null
        countError = null
        reconciledShortResult = false
        startCount(generation, query)
    }

    private fun invalidateRanges() {
        generation++
        layoutGeneration++
        cancelRanges()
        countJob?.cancel()
        countJob = null
        counting = false
        entries = emptyMap()
        rangeErrors = emptyMap()
        protectedUnits = emptySet()
        lastRequestedRange = null
        updateLoading()
    }

    private fun cancelRanges() {
        rangeJobs.values.forEach { it.cancel() }
        rangeJobs.clear()
        loadingRanges = false
    }

    private fun startCount(expectedGeneration: Int, query: TableQuery) {
        countJob?.cancel()
        val id = ++requestId
        counting = true
        countError = null
        updateLoading()
        countJob = scope.launch {
            try {
                val result = provider.count(query)
                currentCoroutineContext().ensureActive()
                if (expectedGeneration != generation || disposed || id != requestId) return@launch
                require(result in 0..Int.MAX_VALUE.toLong()) {
                    "Provider count $result is outside the supported range 0..${Int.MAX_VALUE}"
                }
                reconcileCount(result.toInt())
                countError = null
                lastRequestedRange?.let { requestRange(it.first, it.last) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Throwable) {
                if (expectedGeneration == generation && !disposed && id == requestId) {
                    countError = exception
                }
            } finally {
                if (id == requestId) {
                    counting = false
                    countJob = null
                    updateLoading()
                }
            }
        }
    }

    private fun loadUnit(
        unit: Int,
        expectedGeneration: Int,
        expectedLayoutGeneration: Int,
        query: TableQuery,
        display: TableDisplay
    ) {
        val config = lazyConfig
        val offset = unitOffset(unit, display)
        val count = countValue ?: return
        if (offset >= count) return
        val limit = minOf(unitSize(display), count - offset)
        var activeLayoutGeneration = expectedLayoutGeneration
        val job = scope.launch(start = CoroutineStart.LAZY) {
            try {
                val result = provider.load(query, offset.toLong(), limit)
                currentCoroutineContext().ensureActive()
                if (expectedGeneration != generation || activeLayoutGeneration != layoutGeneration ||
                    disposed || state.query != query || state.display != display || lazyConfig != config
                ) {
                    return@launch
                }
                require(result.size <= limit) {
                    "Provider returned ${result.size} items for a requested limit of $limit"
                }
                require(result.none { it == null }) {
                    "Provider returned a null item; null is reserved for unloaded placeholders"
                }
                val expectedSize = minOf(limit, ((countValue ?: count) - offset).coerceAtLeast(0))
                val accepted = result
                if (result.size < expectedSize) {
                    if (!reconciledShortResult) {
                        reconciledShortResult = true
                        val reconciledCount = provider.count(query)
                        currentCoroutineContext().ensureActive()
                        if (expectedGeneration != generation || activeLayoutGeneration != layoutGeneration ||
                            disposed || state.query != query || state.display != display || lazyConfig != config
                        ) return@launch
                        require(reconciledCount in 0..Int.MAX_VALUE.toLong()) {
                            "Provider count $reconciledCount is outside the supported range 0..${Int.MAX_VALUE}"
                        }
                        val shrunk = reconcileCount(
                            reconciledCount.toInt(),
                            currentCoroutineContext()[Job]
                        )
                        if (shrunk) activeLayoutGeneration = layoutGeneration
                    }
                    val reconciledExpected = minOf(
                        unitSize(display),
                        ((countValue ?: count) - offset).coerceAtLeast(0)
                    )
                    if (result.size != reconciledExpected) {
                        throw ProviderConsistencyException(
                            "Provider returned ${result.size} items at offset $offset; " +
                                "the reconciled count requires $reconciledExpected"
                        )
                    }
                    if (reconciledExpected == 0) return@launch
                }
                val currentExpected = minOf(
                    unitSize(display),
                    ((countValue ?: count) - offset).coerceAtLeast(0)
                )
                if (accepted.size > currentExpected) {
                    throw ProviderConsistencyException(
                        "Provider returned ${accepted.size} items at offset $offset; " +
                            "the current count permits $currentExpected"
                    )
                }
                validateUniqueKeys(unit, accepted)
                val accessTime = if (unit in protectedUnits) {
                    accessClock++
                    accessClock
                } else {
                    0L
                }
                entries = entries + (unit to Entry(accepted.toList(), accessTime))
                rangeErrors = rangeErrors - unit
                trimCache()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Throwable) {
                if (expectedGeneration == generation && activeLayoutGeneration == layoutGeneration &&
                    !disposed && state.query == query && state.display == display && lazyConfig == config
                ) {
                    rangeErrors = rangeErrors + (unit to exception)
                }
            } finally {
                if (rangeJobs[unit] === currentCoroutineContext()[Job]) {
                    rangeJobs.remove(unit)
                    updateLoading()
                }
            }
        }
        rangeJobs[unit] = job
        updateLoading()
        job.start()
    }

    private fun validateUniqueKeys(unit: Int, items: List<Item>) {
        val seen = mutableSetOf<Any>()
        items.forEach { item ->
            val key = itemKey(item)
            require(seen.add(key)) { "Provider returned duplicate item key $key within a range" }
        }
        entries.filterKeys { it != unit }.values.forEach { entry ->
            entry.items.forEach { item ->
                require(itemKey(item) !in seen) { "Provider returned duplicate item key across cached ranges" }
            }
        }
    }

    private fun touch(unit: Int, entry: Entry<Item>) {
        accessClock++
        entries = entries + (unit to entry.copy(lastAccess = accessClock))
    }

    private fun trimCache() {
        val cache = lazyConfig.cache
        if (cache is TableCache.KeepAll) return
        val capacity = (cache as TableCache.Lru).capacity
        while (entries.size > capacity) {
            val victim = entries.entries
                .filter { it.key !in protectedUnits }
                .minByOrNull { it.value.lastAccess }
                ?.key ?: return
            entries = entries - victim
        }
    }

    private fun convertPages(
        oldEntries: Map<Int, Entry<Item>>,
        count: Int?,
        oldDisplay: TableDisplay.Paged,
        newDisplay: TableDisplay.Paged
    ): Map<Int, Entry<Item>> {
        val total = count ?: return emptyMap()
        val indexed = mutableMapOf<Int, Item>()
        val indexedAccess = mutableMapOf<Int, Long>()
        oldEntries.forEach { (unit, entry) ->
            val offset = unit * oldDisplay.pageSize
            entry.items.forEachIndexed { index, item ->
                if (offset + index < total) {
                    indexed[offset + index] = item
                    indexedAccess[offset + index] = entry.lastAccess
                }
            }
        }
        val converted = mutableMapOf<Int, Entry<Item>>()
        val candidates = indexed.keys.mapTo(mutableSetOf()) { it / newDisplay.pageSize }
        candidates.forEach { page ->
            val offset = page * newDisplay.pageSize
            val size = minOf(newDisplay.pageSize, total - offset)
            if ((offset until offset + size).all { it in indexed }) {
                val pageItems = (offset until offset + size).map { indexed.getValue(it) }
                val mostRecentAccess = (offset until offset + size).maxOfOrNull { indexedAccess.getValue(it) } ?: 0L
                converted[page] = Entry(pageItems, mostRecentAccess)
            }
        }
        return converted
    }

    private fun reconcileCount(newCount: Int, currentRangeJob: Job? = null): Boolean {
        val previousCount = countValue
        countValue = newCount
        if (!stateIsObserved()) return false
        val shrunk = previousCount != null && newCount < previousCount
        if (shrunk) {
            entries = emptyMap()
            rangeErrors = emptyMap()
            layoutGeneration++
            val staleUnits = rangeJobs.filterValues { it !== currentRangeJob }.keys
            staleUnits.forEach { rangeJobs.remove(it)?.cancel() }
        }
        val newPageCount = if (newCount == 0) 0 else (newCount - 1) /
            (state.display as? TableDisplay.Paged)?.pageSize.orOne() + 1
        if (newPageCount > 0 && state.page >= newPageCount) state.setPage(newPageCount - 1)
        else if (newPageCount == 0 && state.page != 0) state.setPage(0)

        val size = unitSize(state.display)
        val finalUnit = if (newCount == 0) -1 else (newCount - 1) / size
        entries = entries.mapNotNull { (unit, entry) ->
            if (unit > finalUnit) null
            else {
                val validSize = minOf(size, newCount - unit * size).coerceAtLeast(0)
                if (validSize == 0) null
                else unit to entry.copy(items = entry.items.take(validSize))
            }
        }.toMap()
        rangeErrors = rangeErrors.filterKeys { it <= finalUnit }
        return shrunk
    }

    private fun Int?.orOne(): Int = this ?: 1

    private fun stateIsObserved(): Boolean =
        observedQuery == state.query && observedDisplay == state.display && observedLazyConfig == lazyConfig

    private fun updateLoading() {
        loadingRanges = rangeJobs.isNotEmpty()
    }

    private fun unitSize(display: TableDisplay): Int = when (display) {
        is TableDisplay.Continuous -> lazyConfig.blockSize
        is TableDisplay.Paged -> display.pageSize
    }

    private fun unitOffset(unit: Int, display: TableDisplay): Int = unit * unitSize(display)

    private fun prefetchCount(display: TableDisplay): Int = when (display) {
        is TableDisplay.Continuous -> lazyConfig.prefetchBlocks
        is TableDisplay.Paged -> lazyConfig.prefetchPages
    }
}

/** A provider's count and range no longer describe the same result snapshot. */
class ProviderConsistencyException(message: String) : IllegalStateException(message)

/**
 * Binds a counted provider to table state. The provider must implement search, filters and sorting
 * globally and keep deterministic ordering. Call [ProviderTableModel.refresh] after mutations.
 */
@Composable
fun <Item> rememberTable(
    provider: TableDataProvider<Item>,
    columns: List<TableColumn<Item, *>>,
    state: TableState = rememberTableState(),
    lazyConfig: TableLazyConfig = TableLazyConfig(),
    itemKey: (Item) -> Any
): ProviderTableModel<Item> {
    val scope = rememberCoroutineScope()
    val latestKey = rememberUpdatedState(itemKey)
    val table = remember(provider, state) {
        ProviderTableModel(provider, columns, state, { latestKey.value(it) }, scope, lazyConfig)
    }
    SideEffect {
        table.updateColumns(columns)
        table.updateLazyConfig(lazyConfig)
        state.reconcileColumns(columns)
    }
    DisposableEffect(table) {
        onDispose { table.dispose() }
    }
    return table
}

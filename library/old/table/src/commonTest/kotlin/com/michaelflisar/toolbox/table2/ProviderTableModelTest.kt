package com.michaelflisar.toolbox.table2

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertFailsWith

class ProviderTableModelTest {
    @Test fun fivePageCacheReusesFilteredPagesAndEvictsLeastRecentlyUsedPage() = runTest {
        val provider = FakeProvider((0..19).map(::Row))
        val state = TableState(TableDisplay.Paged(2, listOf(2)))
        state.search("active query")
        val table = model(provider, state, backgroundScope, TableLazyConfig(cache = TableCache.Lru(5)))
        runCurrent()
        (0..4).forEach { page ->
            table.requestRange(page * 2, page * 2 + 1)
            runCurrent()
        }
        table.requestRange(0, 1)
        runCurrent()
        assertEquals(5, provider.loads.size)
        table.requestRange(10, 11)
        runCurrent()
        assertEquals(10, table.loadedItems.size)
        assertEquals(Row(0), table.itemAt(0))
        assertNull(table.itemAt(2))
        table.requestRange(2, 3)
        runCurrent()
        assertEquals(7, provider.loads.size)
        state.search("new query")
        notifySnapshotChanges()
        runCurrent()
        assertEquals(emptyList(), table.loadedItems)
        assertEquals(2, provider.countQueries.size)
        table.dispose()
    }

    @Test fun lazyConfigChangesTrimCacheAndChangeBlockSizeWithoutRecounting() = runTest {
        val provider = FakeProvider((0..19).map(::Row))
        val state = TableState()
        val table = model(provider, state, backgroundScope,
            TableLazyConfig(cache = TableCache.KeepAll, blockSize = 2, prefetchBlocks = 0))
        runCurrent()
        listOf(0, 2, 4).forEach { table.requestRange(it, it + 1); runCurrent() }
        table.updateLazyConfig(table.lazyConfig.copy(cache = TableCache.Lru(2)))
        notifySnapshotChanges()
        runCurrent()
        assertEquals(4, table.loadedItems.size)
        assertNull(table.itemAt(0))
        table.updateLazyConfig(table.lazyConfig.copy(blockSize = 4))
        assertNull(table.itemAt(4))
        notifySnapshotChanges()
        runCurrent()
        assertEquals(emptyList(), table.loadedItems)
        table.requestRange(4, 5)
        runCurrent()
        assertEquals(4L to 4, provider.loads.last())
        assertEquals(1, provider.countQueries.size)
        table.dispose()
    }

    @Test fun refreshReloadsCurrentSourceWithoutRestoringInitialData() = runTest {
        val provider = FakeProvider((0..2).map(::Row))
        val state = TableState(TableDisplay.Paged(2, listOf(2)))
        val table = model(provider, state, backgroundScope)
        runCurrent()
        table.remove(1)
        runCurrent()
        table.refresh()
        runCurrent()
        table.requestRange(0, 1)
        runCurrent()
        assertEquals(listOf(Row(0), Row(2)), table.loadedItems)
        assertEquals(listOf(Row(0), Row(2)), provider.rows)
        table.replaceAll(listOf(Row(4), Row(5)))
        runCurrent()
        state.select(4)
        val query = state.query
        table.refresh()
        runCurrent()
        table.requestRange(0, 1)
        runCurrent()
        assertEquals(listOf(Row(4), Row(5)), table.loadedItems)
        assertEquals(query, state.query)
        assertEquals(setOf<Any>(4), state.selectedKeys)
        table.clear()
        runCurrent()
        table.refresh()
        runCurrent()
        assertEquals(0, table.totalCount)
        assertEquals(emptyList(), provider.rows)
        table.dispose()
    }

    @Test fun errorScopesExposeCountAndRangeContextAndRetryExistingFailures() = runTest {
        val countFailure = IllegalStateException("Count failed")
        val rangeFailure = IllegalStateException("Range failed")
        val provider = FakeProvider(listOf(Row(0)), countBlock = { throw countFailure })
        val table = model(provider, TableState(), backgroundScope)
        runCurrent()
        val count = TableError(requireNotNull(table.error), TableErrorKind.Count, onRetry = table::retry)
        assertEquals(countFailure, count.error)
        assertEquals(TableErrorKind.Count, count.kind)
        assertNull(count.index)
        provider.countBlock = null
        count.retry()
        runCurrent()
        assertEquals(1, table.totalCount)
        assertEquals(2, provider.countQueries.size)

        provider.loadBlock = { _, _ -> throw rangeFailure }
        table.requestRange(0, 0)
        runCurrent()
        val range = TableError(requireNotNull(table.rangeError(0)), TableErrorKind.Range, 0, table::retry)
        assertEquals(rangeFailure, range.error)
        assertEquals(TableErrorKind.Range, range.kind)
        assertEquals(0, range.index)
        provider.loadBlock = null
        range.retry()
        runCurrent()
        assertEquals(Row(0), table.itemAt(0))
        table.dispose()
    }

    @Test fun dialogDraftDoesNotRecountUntilCommitted() = runTest {
        val provider = FakeProvider(listOf(Row(0)))
        val state = TableState()
        val filter = numberFilter(String::toIntOrNull)
        val table = ProviderTableModel(
            provider, listOf(numberColumn<Row, Int>("id", "ID", { it.id }, filter = filter)),
            state, { it.id }, backgroundScope
        )
        runCurrent()
        val draft = TableFilterDraft(filter, null)
        draft.edit(TableFilterValue.Number("0"), TableFilterPresentation.Dialog) { state.setFilter("id", it) }
        notifySnapshotChanges()
        runCurrent()
        assertEquals(1, provider.countQueries.size)
        draft.commit { state.setFilter("id", it) }
        notifySnapshotChanges()
        runCurrent()
        assertEquals(2, provider.countQueries.size)
        table.dispose()
    }

    @Test fun replacingProviderDataRetainsStateAndOnlyRefreshesAfterSuccess() = runTest {
        val provider = FakeProvider((0..3).map(::Row))
        val state = TableState(TableDisplay.Paged(2, listOf(2)))
        state.setSort("id", TableSortDirection.Descending)
        val table = model(provider, state, backgroundScope)
        runCurrent()
        table.requestRange(0, 1)
        runCurrent()
        state.select(0)
        state.select(3)
        val query = state.query
        provider.failMutation = true
        assertFailsWith<IllegalStateException> { table.replaceAll(listOf(Row(0))) }
        assertEquals(4, table.totalCount)
        assertEquals(setOf<Any>(0, 3), state.selectedKeys)
        assertEquals(1, provider.countQueries.size)
        provider.failMutation = false
        assertFailsWith<IllegalArgumentException> { table.replaceAll(listOf(Row(0), Row(0))) }
        table.replaceAll(listOf(Row(0)))
        runCurrent()
        assertEquals(1, table.totalCount)
        assertEquals(setOf<Any>(0), state.selectedKeys)
        assertEquals(query, state.query)
        table.clear()
        runCurrent()
        assertEquals(0, table.totalCount)
        assertEquals(emptyList(), provider.rows)
        assertEquals(emptySet(), state.selectedKeys)
        assertEquals(query, state.query)
    }

    @Test fun mutationsPersistBeforeRefreshingAndFailuresPreserveCacheAndSelection() = runTest {
        val provider = FakeProvider((0..2).map(::Row))
        val state = TableState(TableDisplay.Paged(2, listOf(2)))
        val table = model(provider, state, backgroundScope)
        runCurrent()
        table.requestRange(0, 1)
        runCurrent()
        state.select(1)
        provider.failMutation = true
        assertFailsWith<IllegalStateException> { table.remove(1) }
        assertEquals(1, table.itemAt(1)?.id)
        assertEquals(setOf<Any>(1), state.selectedKeys)
        assertEquals(1, provider.countQueries.size)
        provider.failMutation = false
        table.remove(1)
        runCurrent()
        assertEquals(listOf(0, 2), provider.rows.map { it.id })
        assertEquals(2, table.totalCount)
        assertEquals(emptySet(), state.selectedKeys)
        table.insert(Row(3))
        runCurrent()
        assertEquals(3, table.totalCount)
        table.update(3, Row(3))
        runCurrent()
        assertEquals(4, provider.countQueries.size)
        assertFailsWith<IllegalArgumentException> { table.update(3, Row(4)) }
        table.dispose()
    }

    private data class Row(val id: Int)

    private fun notifySnapshotChanges() {
        Snapshot.sendApplyNotifications()
    }

    private class FakeProvider(
        var rows: List<Row>,
        var loadBlock: (suspend (offset: Long, limit: Int) -> List<Row>)? = null,
        var countBlock: (suspend (query: TableQuery) -> Long)? = null
    ) : TableDataProvider<Row> {
        val countQueries = mutableListOf<TableQuery>()
        val loads = mutableListOf<Pair<Long, Int>>()
        var failMutation = false

        override suspend fun insert(item: Row) {
            check(!failMutation) { "Mutation failed" }
            require(rows.none { it.id == item.id })
            rows = rows + item
        }
        override suspend fun update(key: Any, item: Row) {
            check(!failMutation) { "Mutation failed" }
            require(key == item.id && rows.any { it.id == key })
            rows = rows.map { if (it.id == key) item else it }
        }
        override suspend fun remove(key: Any) {
            check(!failMutation) { "Mutation failed" }
            rows = rows.filterNot { it.id == key }
        }
        override suspend fun replaceAll(items: List<Row>) {
            check(!failMutation) { "Mutation failed" }
            rows = items.toList()
        }

        override suspend fun count(query: TableQuery): Long {
            countQueries += query
            return countBlock?.invoke(query) ?: rows.size.toLong()
        }

        override suspend fun load(query: TableQuery, offset: Long, limit: Int): List<Row> {
            loads += offset to limit
            return loadBlock?.invoke(offset, limit) ?: rows.drop(offset.toInt()).take(limit)
        }
    }

    private fun columns() = listOf(
        numberColumn<Row, Int>("id", "ID", { it.id })
    )

    private fun model(
        provider: TableDataProvider<Row>,
        state: TableState,
        scope: CoroutineScope,
        lazyConfig: TableLazyConfig = TableLazyConfig()
    ) = ProviderTableModel(provider, columns(), state, { it.id }, scope, lazyConfig)

    @Test
    fun countSupportsEmptyResultsAndRejectsCountsOutsideComposeBounds() = runTest {
        val empty = FakeProvider(emptyList())
        val emptyState = TableState(TableDisplay.Paged(pageSize = 2, pageSizes = listOf(2)))
        val emptyModel = model(empty, emptyState, backgroundScope)
        runCurrent()
        assertEquals(0, emptyModel.totalCount)
        assertEquals(0, emptyModel.pageCount)
        assertNull(emptyModel.itemAt(0))

        val overflow = FakeProvider(emptyList(), countBlock = { Int.MAX_VALUE.toLong() + 1 })
        val overflowModel = model(overflow, TableState(), backgroundScope)
        runCurrent()
        assertEquals(null, overflowModel.totalCount)
        assertIs<IllegalArgumentException>(overflowModel.error)
    }

    @Test
    fun absoluteRangeLoadsDeduplicateRequestsAndExposeItemsByIndex() = runTest {
        val provider = FakeProvider((0..7).map(::Row), loadBlock = { offset, limit ->
            delay(1)
            (offset.toInt() until minOf(offset.toInt() + limit, 8)).map(::Row)
        })
        val state = TableState(TableDisplay.Continuous)
        val table = model(provider, state, backgroundScope,
            TableLazyConfig(blockSize = 4, cache = TableCache.KeepAll, prefetchBlocks = 0))
        runCurrent()

        table.requestRange(5, 6)
        table.requestRange(5, 6)
        runCurrent()
        advanceTimeBy(1)
        runCurrent()

        assertEquals(listOf(4L to 4), provider.loads)
        assertEquals(5, table.itemAt(5)?.id)
        assertEquals(6, table.itemAt(6)?.id)
        assertEquals(listOf(4, 5, 6, 7), table.loadedItems.map { it.id })
    }

    @Test
    fun countAndRangeFailuresRemainSeparateAndRetryTheFailedOperation() = runTest {
        var failCount = true
        var failRange = true
        val provider = FakeProvider(
            rows = (0..3).map(::Row),
            countBlock = {
                if (failCount) error("count failed")
                4L
            },
            loadBlock = { offset, limit ->
                if (failRange) error("range failed")
                (offset.toInt() until offset.toInt() + limit).map(::Row)
            }
        )
        val state = TableState(TableDisplay.Paged(pageSize = 2, pageSizes = listOf(2)))
        val table = model(provider, state, backgroundScope)
        runCurrent()
        assertEquals("count failed", table.error?.message)

        failCount = false
        table.retry()
        runCurrent()
        assertNull(table.error)
        table.requestRange(0, 1)
        runCurrent()
        assertEquals("range failed", table.rangeError(0)?.message)
        assertNull(table.error)
        table.requestRange(0, 1)
        runCurrent()
        assertEquals(1, provider.loads.size)

        failRange = false
        table.retry()
        runCurrent()
        assertNull(table.rangeError(0))
        assertEquals(listOf(0, 1), listOf(table.itemAt(0)?.id, table.itemAt(1)?.id))
    }

    @Test
    fun lruEvictsLeastRecentlyRequestedUnitButProtectsVisibleUnit() = runTest {
        val provider = FakeProvider((0..11).map(::Row))
        val state = TableState(TableDisplay.Continuous)
        val table = model(provider, state, backgroundScope,
            TableLazyConfig(blockSize = 2, cache = TableCache.Lru(2), prefetchBlocks = 0))
        runCurrent()

        table.requestRange(0, 1)
        runCurrent()
        table.requestRange(2, 3)
        runCurrent()
        table.requestRange(0, 1)
        table.requestRange(4, 5)
        runCurrent()

        assertNull(table.itemAt(2))
        assertEquals(0, table.itemAt(0)?.id)
        assertEquals(4, table.itemAt(4)?.id)
    }

    @Test
    fun queryChangesRejectOldCountAndDoNotPruneProviderSelection() = runTest {
        val slowCount = CompletableDeferred<Long>()
        val provider = FakeProvider(
            rows = (0..3).map(::Row),
            countBlock = { query -> if (query.search == "slow") slowCount.await() else 1L }
        )
        val state = TableState()
        state.search("slow")
        state.select(99)
        val table = model(provider, state, backgroundScope)
        runCurrent()

        state.search("fast")
        notifySnapshotChanges()
        assertNull(table.totalCount)
        assertNull(table.itemAt(0))
        assertEquals(emptyList(), table.loadedItems)
        runCurrent()
        slowCount.complete(100)
        runCurrent()

        assertEquals(1, table.totalCount)
        assertEquals(setOf(99), state.selectedKeys)
        assertEquals(1, table.dataVersion)
        assertEquals(listOf("slow", "fast"), provider.countQueries.map { it.search })
    }

    @Test
    fun emptyCountTransitionsInvalidateReadersWhenObservedStateCatchesUp() = runTest {
        val provider = FakeProvider(emptyList())
        val state = TableState(TableDisplay.Paged(pageSize = 2, pageSizes = listOf(2)))
        val table = model(provider, state, backgroundScope)
        val observations = mutableListOf<Int?>()
        backgroundScope.launch {
            snapshotFlow { table.totalCount }.collect { observations += it }
        }
        runCurrent()
        notifySnapshotChanges()
        runCurrent()
        assertEquals(0, observations.last())

        state.search("empty")
        assertNull(table.totalCount)
        notifySnapshotChanges()
        runCurrent()
        notifySnapshotChanges()
        runCurrent()

        assertEquals(0, observations.last())
        assertEquals(2, provider.countQueries.size)
    }

    @Test
    fun refreshRecountsAndPageSizeChangeReusesOnlyCompleteCachedPages() = runTest {
        val rows = (0..6).map(::Row)
        val provider = FakeProvider(rows)
        val state = TableState(
            TableDisplay.Paged(
                pageSize = 2,
                pageSizes = listOf(2, 3)
            )
        )
        state.select(1000)
        val table = model(provider, state, backgroundScope)
        runCurrent()
        (0..3).forEach { page ->
            table.requestRange(page * 2, page * 2 + 1)
            runCurrent()
        }
        assertEquals(4, provider.loads.size)

        state.setDisplay(
            TableDisplay.Paged(
                pageSize = 3,
                pageSizes = listOf(2, 3)
            )
        )
        notifySnapshotChanges()
        assertNull(table.totalCount)
        assertNull(table.itemAt(0))
        assertEquals(emptyList(), table.loadedItems)
        runCurrent()

        assertEquals(1, provider.countQueries.size)
        assertEquals((0..6).toList(), (0..6).map { table.itemAt(it)?.id })
        assertEquals(setOf(1000), state.selectedKeys)

        table.refresh()
        runCurrent()
        assertEquals(2, provider.countQueries.size)
        assertEquals(1, table.dataVersion)
        assertEquals(0, table.loadedItems.size)
    }

    @Test
    fun pageConversionVisitsOnlyCachedPagesEvenForHugeCounts() = runTest {
        val provider = FakeProvider(
            rows = emptyList(),
            loadBlock = { offset, limit -> (offset.toInt() until offset.toInt() + limit).map(::Row) },
            countBlock = { 1_000_000_000L }
        )
        val state = TableState(
            TableDisplay.Paged(
                pageSize = 2,
                pageSizes = listOf(2, 3)
            )
        )
        val table = model(provider, state, backgroundScope)
        runCurrent()

        val highOffset = 900_000_000
        table.requestRange(highOffset, highOffset + 2)
        runCurrent()
        state.setDisplay(
            TableDisplay.Paged(
                pageSize = 3,
                pageSizes = listOf(2, 3)
            )
        )
        notifySnapshotChanges()
        runCurrent()

        assertEquals(
            listOf(900_000_000, 900_000_001, 900_000_002),
            (highOffset..highOffset + 2).map { table.itemAt(it)?.id }
        )
    }

    @Test
    fun prefetchLoadsDoNotCountAsRealCacheAccesses() = runTest {
        val provider = FakeProvider((0..15).map(::Row))
        val state = TableState(TableDisplay.Continuous)
        val table = model(provider, state, backgroundScope,
            TableLazyConfig(blockSize = 2, cache = TableCache.Lru(3), prefetchBlocks = 1))
        runCurrent()

        table.requestRange(0, 1)
        runCurrent()
        table.requestRange(4, 5)
        runCurrent()

        assertEquals(0, table.itemAt(0)?.id)
        assertNull(table.itemAt(2))
        assertEquals(4, table.itemAt(4)?.id)
    }

    @Test
    fun shortResultCountShrinkPurgesPastRowsAndClampsPage() = runTest {
        val provider = FakeProvider((0..5).map(::Row))
        val state = TableState(
            TableDisplay.Paged(pageSize = 2, pageSizes = listOf(2))
        )
        val table = model(provider, state, backgroundScope)
        runCurrent()
        table.requestRange(0, 1)
        table.requestRange(4, 5)
        runCurrent()
        state.setPage(2)

        provider.rows = (0..2).map(::Row)
        table.requestRange(2, 3)
        runCurrent()

        assertEquals(3, table.totalCount)
        assertEquals(1, state.page)
        assertNull(table.itemAt(0))
        assertEquals(2, table.itemAt(2)?.id)
        assertNull(table.itemAt(4))
        assertEquals(listOf(2), table.loadedItems.map { it.id })
    }

    @Test
    fun shortRangeReconcilesOnceThenExposesConsistencyErrorAndRetryCanRecover() = runTest {
        var providerLoadsShouldBeShort = true
        val provider = FakeProvider(
            rows = (0..3).map(::Row),
            loadBlock = { offset, limit ->
                if (providerLoadsShouldBeShort) listOf(Row(offset.toInt()))
                else (offset.toInt() until offset.toInt() + limit).map(::Row)
            }
        )
        val state = TableState(TableDisplay.Paged(pageSize = 2, pageSizes = listOf(2)))
        val table = model(provider, state, backgroundScope)
        runCurrent()

        table.requestRange(0, 1)
        runCurrent()

        assertIs<ProviderConsistencyException>(table.rangeError(0))
        assertEquals(2, provider.countQueries.size)

        providerLoadsShouldBeShort = false
        table.retry()
        runCurrent()
        assertNull(table.rangeError(0))
        assertEquals(listOf(0, 1), listOf(table.itemAt(0)?.id, table.itemAt(1)?.id))
        assertEquals(2, provider.countQueries.size)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun providerNullItemsBecomeRangeErrorsInsteadOfLoadedPlaceholderValues() = runTest {
        val provider = FakeProvider(
            rows = emptyList(),
            loadBlock = { _, _ -> listOf(null) as List<Row> },
            countBlock = { 1L }
        )
        val state = TableState(TableDisplay.Paged(pageSize = 2, pageSizes = listOf(2)))
        val table = model(provider, state, backgroundScope)
        runCurrent()

        table.requestRange(0, 0)
        runCurrent()

        assertNull(table.itemAt(0))
        assertEquals(
            "Provider returned a null item; null is reserved for unloaded placeholders",
            table.rangeError(0)?.message
        )
    }

    @Test
    fun decreasingPageSizeRetainsCompletePagesAcrossGapsAndFinalPage() = runTest {
        val provider = FakeProvider((0..7).map(::Row))
        val state = TableState(TableDisplay.Paged(3, listOf(2, 3)))
        val table = model(provider, state, backgroundScope)
        runCurrent()
        table.requestRange(0, 2)
        table.requestRange(6, 7)
        runCurrent()
        state.setDisplay(TableDisplay.Paged(2, listOf(2, 3)))
        notifySnapshotChanges()
        runCurrent()
        assertEquals(listOf(0, 1, 6, 7), table.loadedItems.map { it.id })
        assertNull(table.itemAt(2))
        assertEquals(1, provider.countQueries.size)
        table.requestRange(2, 3)
        runCurrent()
        assertEquals(2L to 2, provider.loads.last())
    }

    @Test
    fun convertedPagesRespectLruCapacityAndProtectNewCurrentPage() = runTest {
        val provider = FakeProvider((0..5).map(::Row))
        val state = TableState(TableDisplay.Paged(2, listOf(1, 2)))
        val table = model(provider, state, backgroundScope)
        runCurrent()
        listOf(0, 2, 4).forEach { table.requestRange(it, it + 1); runCurrent() }
        table.updateLazyConfig(TableLazyConfig(cache = TableCache.Lru(2)))
        state.setDisplay(TableDisplay.Paged(1, listOf(1, 2)))
        notifySnapshotChanges()
        runCurrent()
        assertEquals(2, table.loadedItems.size)
        assertEquals(0, table.itemAt(0)?.id)
        assertEquals(1, table.loadedItems.count { it.id >= 4 })
        assertEquals(1, provider.countQueries.size)
    }

    @Test
    fun lateOldSizeResponseCannotOverwriteConvertedLayout() = runTest {
        val oldResponse = CompletableDeferred<List<Row>>()
        val provider = FakeProvider(
            (0..5).map(::Row),
            loadBlock = { offset, limit ->
                if (limit == 2) withContext(NonCancellable) { oldResponse.await() }
                else (offset.toInt() until offset.toInt() + limit).map(::Row)
            }
        )
        val state = TableState(TableDisplay.Paged(2, listOf(2, 3)))
        val table = model(provider, state, backgroundScope)
        runCurrent()
        table.requestRange(0, 1)
        runCurrent()
        state.setDisplay(TableDisplay.Paged(3, listOf(2, 3)))
        notifySnapshotChanges()
        runCurrent()
        table.requestRange(0, 2)
        runCurrent()
        oldResponse.complete(listOf(Row(99), Row(98)))
        runCurrent()
        assertEquals(listOf(0, 1, 2), table.loadedItems.map { it.id })
        assertNull(table.error)
        assertNull(table.rangeError(0))
    }
}

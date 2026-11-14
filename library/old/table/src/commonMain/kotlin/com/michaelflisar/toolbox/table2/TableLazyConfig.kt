package com.michaelflisar.toolbox.table2

sealed interface TableCache {
    /** Capacity in display pages when paged, or load blocks when continuous. Visible units are protected. */
    data class Lru(val capacity: Int = 10) : TableCache {
        init { require(capacity > 0) { "Cache capacity must be positive" } }
    }
    data object KeepAll : TableCache
}

/** Provider-only loading settings. Paged requests always use the display's page size. */
data class TableLazyConfig(
    val cache: TableCache = TableCache.Lru(),
    val blockSize: Int = 100,
    val prefetchBlocks: Int = 1,
    val prefetchPages: Int = 0
) {
    init {
        require(blockSize > 0) { "Block size must be positive" }
        require(prefetchBlocks >= 0 && prefetchPages >= 0) { "Prefetch must not be negative" }
    }
}

package com.michaelflisar.toolbox.diff.classes

data class DiffResult(
    val tableResults: List<TableDiffResult> = emptyList()
) {
    val changes: List<Change> by lazy {
        tableResults.flatMap { it.changes }
    }

    fun getChangesForTable(tableName: String): List<Change> {
        return tableResults.firstOrNull { it.tableName == tableName }?.changes.orEmpty()
    }
}
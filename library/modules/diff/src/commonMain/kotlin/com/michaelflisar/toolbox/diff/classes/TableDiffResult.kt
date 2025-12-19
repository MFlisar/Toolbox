package com.michaelflisar.toolbox.diff.classes

data class TableDiffResult(
    val tableName: String,
    val changes: List<Change>
)
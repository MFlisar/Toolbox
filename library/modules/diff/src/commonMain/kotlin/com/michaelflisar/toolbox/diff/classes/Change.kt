package com.michaelflisar.toolbox.diff.classes

sealed class Change {

    abstract val table: String
    abstract val category: ChangeCategory
    abstract val entityId: Long
    abstract val entityLabel: String

    data class Added(
        override val table: String,
        override val category: ChangeCategory,
        override val entityId: Long,
        override val entityLabel: String
    ) : Change()

    data class Removed(
        override val table: String,
        override val category: ChangeCategory,
        override val entityId: Long,
        override val entityLabel: String
    ) : Change()

    data class Updated(
        override val table: String,
        override val category: ChangeCategory,
        override val entityId: Long,
        override val entityLabel: String,
        val fields: List<Field>
    ) : Change() {
        data class Field(
            val field: String,
            val before: String,
            val after: String,
            val extra: Map<String, String> = emptyMap(),
        )
    }
}
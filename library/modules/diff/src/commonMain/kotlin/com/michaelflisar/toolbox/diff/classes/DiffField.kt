package com.michaelflisar.toolbox.diff.classes

import com.michaelflisar.toolbox.diff.Differ.idToLabel
import kotlin.reflect.KProperty1

sealed interface FieldDef<T> {
    fun diff(
        table: String,
        entityId: Long,
        entityLabel: String,
        old: T,
        neu: T
    ): Change.Updated.Field?
}

object DiffField {

    class ScalarProp<T, V>(
        private val prop: KProperty1<T, V>,
        private val category: ChangeCategory,
        private val render: (V) -> String = { it.toString() }
    ) : FieldDef<T> {

        override fun diff(table: String, entityId: Long, entityLabel: String, old: T, neu: T) =
            if (prop.get(old) == prop.get(neu))
                null
            else
                Change.Updated.Field(
                    field = prop.name,
                    before = render(prop.get(old)),
                    after = render(prop.get(neu))
                )

    }

    class IdListProp<T, ID : Number>(
        private val prop: KProperty1<T, List<ID>>,
        private val tableSingleName: String,
    ) : FieldDef<T> {

        override fun diff(
            table: String,
            entityId: Long,
            entityLabel: String,
            old: T,
            neu: T
        ): Change.Updated.Field? {
            val b = prop.get(old).map { it.toLong() }.toSet()
            val a = prop.get(neu).map { it.toLong() }.toSet()
            if (b == a) return null
            return Change.Updated.Field(
                field = prop.name,
                before = "count=${b.size}",
                after = "count=${a.size}",
                extra = mapOf(
                    "added" to fmt(a - b),
                    "removed" to fmt(b - a)
                ).filterValues { it.isNotBlank() })
        }

        private fun fmt(ids: Set<Long>) = ids.joinToString { id ->
            idToLabel(id, tableSingleName)
        }
    }
}
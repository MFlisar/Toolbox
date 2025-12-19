package com.michaelflisar.toolbox.diff

import com.michaelflisar.toolbox.diff.classes.Change
import com.michaelflisar.toolbox.diff.classes.ChangeCategory
import com.michaelflisar.toolbox.diff.classes.FieldDef
import kotlin.reflect.KProperty1

object Differ {

    /*** Automatic label resolution:
     * 1) first non-blank String property
     * 2) fallback: "<tableName> #<id>"
     */
    fun <T> autoLabelOf(
        tableName: String,
        idOf: KProperty1<T, Long>,
        vararg labelProps: KProperty1<T, String?>
    ): (T) -> String = { t ->
        labelProps.firstNotNullOfOrNull {
            it.get(t)?.takeIf { s -> s.isNotBlank() }
        } ?: "$tableName #${idOf.get(t)}"
    }

    fun idToLabel(id: Long, tableSingleName: String) = "$tableSingleName #$id"

    fun <T> diffTable(
        tableName: String,
        oldList: List<T>,
        newList: List<T>,
        idOf: KProperty1<T, Long>,
        labelProps: Array<out KProperty1<T, String?>>,
        fields: List<FieldDef<T>>,
        recordCategory: ChangeCategory = ChangeCategory.RECORD
    ): List<Change> = diffTable(
        tableName = tableName,
        oldList = oldList,
        newList = newList,
        idOf = idOf,
        labelOf = autoLabelOf(tableName, idOf, *labelProps),
        fields = fields,
        recordCategory = recordCategory
    )

    fun <T> diffTable(
        tableName: String,
        oldList: List<T>,
        newList: List<T>,
        idOf: KProperty1<T, Long>,
        labelOf: (T) -> String,
        fields: List<FieldDef<T>>,
        recordCategory: ChangeCategory = ChangeCategory.RECORD
    ): List<Change> {
        val out = mutableListOf<Change>()
        val oldMap = oldList.associateBy { idOf.get(it) }
        val newMap = newList.associateBy {
            idOf.get(
                it
            )
        }
        val allIds = (oldMap.keys + newMap.keys).toSet().sorted()
        for (id in allIds) {
            val o = oldMap[id]
            val n = newMap[id]
            when {
                o == null && n != null -> out += Change.Added(
                    table = tableName,
                    category = recordCategory,
                    entityId = id,
                    entityLabel = labelOf(n)
                )

                o != null && n == null -> out += Change.Removed(
                    table = tableName,
                    category = recordCategory,
                    entityId = id,
                    entityLabel = labelOf(o)
                )

                o != null && n != null -> {
                    val label = labelOf(n)
                    val updates = fields.mapNotNull {
                        it.diff(
                            table = tableName,
                            entityId = id,
                            entityLabel = label,
                            old = o,
                            neu = n
                        )
                    }
                    if (updates.isNotEmpty()) {
                        out += Change.Updated(
                            table = tableName,
                            category = recordCategory,
                            entityId = id,
                            entityLabel = labelOf(o),
                            fields = updates,
                        )
                    }
                }
            }
        }
        return out
    }
}
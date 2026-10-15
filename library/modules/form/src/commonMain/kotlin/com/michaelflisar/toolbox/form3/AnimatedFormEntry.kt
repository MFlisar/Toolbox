package com.michaelflisar.toolbox.form3

import androidx.compose.animation.core.MutableTransitionState

internal data class AnimatedFormEntry(
    val id: Any,
    val item: FormItem,
    val visibility: MutableTransitionState<Boolean>,
)

internal fun reconcileFormEntries(
    previous: List<AnimatedFormEntry>,
    items: List<Pair<Any, FormItem>>,
): List<AnimatedFormEntry> {
    val previousById = previous.associateBy { it.id }
    val entries = items.map { (id, item) ->
        previousById[id]?.copy(item = item)
            ?: AnimatedFormEntry(id, item, MutableTransitionState(false))
    }.toMutableList()
    val currentIds = items.map { it.first }.toSet()

    // Keep exiting items before their next surviving neighbor, even if the list was reordered.
    previous.forEachIndexed { index, entry ->
        if (entry.id !in currentIds) {
            val nextId = previous.drop(index + 1).firstOrNull { it.id in currentIds }?.id
            val insertionIndex = entries.indexOfFirst { it.id == nextId }
            entries.add(if (insertionIndex < 0) entries.size else insertionIndex, entry)
        }
    }
    return entries
}

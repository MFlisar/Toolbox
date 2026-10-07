package com.michaelflisar.toolbox.tasks.ui

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.michaelflisar.toolbox.tasks.ui.state.TaskNodeState
import kotlinx.coroutines.flow.collect

@Composable
internal fun rememberTaskAutoScroll(
    listState: LazyListState,
    execution: TaskNodeState?,
    enabled: Boolean,
): () -> Unit {
    val wasAtBottom = remember(listState, execution) { mutableStateOf(true) }

    LaunchedEffect(listState, execution, enabled) {
        var previousLayout: LazyListLayoutInfo? = null
        var previouslyScrolling = false

        snapshotFlow {
            Triple(listState.layoutInfo, listState.isScrollInProgress, !listState.canScrollForward)
        }.collect { (layout, scrolling, atBottom) ->
            val previous = previousLayout
            val grew = layout.totalItemsCount > (previous?.totalItemsCount ?: 0) ||
                    layout.visibleItemsInfo.any { item ->
                        val old = previous?.visibleItemsInfo?.firstOrNull { it.key == item.key }
                        old != null && item.size > old.size
                    }

            if (layout.totalItemsCount == 0) {
                wasAtBottom.value = true
            } else if (scrolling || previouslyScrolling) {
                wasAtBottom.value = atBottom
            } else if (enabled && wasAtBottom.value && grew) {
                // Use the position before growth, not canScrollForward after the new layout.
                val lastIndex = layout.totalItemsCount - 1
                if (layout.visibleItemsInfo.none { it.index == lastIndex }) {
                    listState.scrollToItem(lastIndex)
                }
                val measured = listState.layoutInfo
                measured.visibleItemsInfo.firstOrNull { it.index == lastIndex }?.let { last ->
                    val distance = last.offset.toLong() + last.size +
                            measured.afterContentPadding - measured.viewportEndOffset
                    if (distance > 0L) {
                        listState.scrollBy(distance.toFloat())
                    }
                }
            }

            previousLayout = layout
            previouslyScrolling = scrolling
        }
    }

    return { wasAtBottom.value = false }
}

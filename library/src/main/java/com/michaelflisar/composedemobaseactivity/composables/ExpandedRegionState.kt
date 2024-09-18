package com.michaelflisar.composedemobaseactivity.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

class ExpandedRegionState(
    private val expandedIds: SnapshotStateList<Int>
) {
    fun isExpanded(id: Int) = expandedIds.contains(id)
    fun toggle(id: Int) = if (isExpanded(id)) expandedIds.remove(id) else expandedIds.add(id)
}

@Composable
fun rememberExpandedRegions(ids: List<Int> = emptyList()) = ExpandedRegionState(
    expandedIds = remember { ids.toMutableStateList() }
)
package com.michaelflisar.toolbox.feature.selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

@Stable
class SelectionDataItems<ID: Comparable<ID>>(
    val totalItemCount: MutableIntState,
    val selectedIds: MutableState<List<ID>>,
    override val isActive: MutableState<Boolean>,
    override var menuProvider: @Composable (data: SelectionDataItems<ID>) -> Unit
) : SelectionData<ID, SelectionDataItems<ID>> {

    override val total: Int
        get() = totalItemCount.intValue

    override val selected: Int
        get() = selectedIds.value.size

    val isSomethingSelected: Boolean
        get() = selected > 0

    override fun isSelected(id: ID): Boolean {
        return selectedIds.value.contains(id)
    }

    override fun select(id: ID) {
        if (!isSelected(id)) {
            selectedIds.value += id
        }
    }

    override fun deselect(id: ID) {
        if (isSelected(id)) {
            selectedIds.value -= id
        }
    }

    override fun toggle(id: ID) {
        if (isSelected(id)) {
            deselect(id)
        } else {
            select(id)
        }
    }

    override fun clearSelection() {
        selectedIds.value = emptyList()
    }
}
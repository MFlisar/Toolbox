package com.michaelflisar.toolbox.app.features.toolbar.selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun <ID> rememberSelectionDataItems(
    totalItemCount: Int,
    selectedIds: List<ID> = emptyList(),
    isActive: Boolean = false,
    menuProvider: @Composable (data: SelectionDataItems<ID>) -> Unit
): SelectionDataItems<ID> {
    val data = SelectionDataItems(
        totalItemCount = rememberSaveable(totalItemCount) { mutableIntStateOf(totalItemCount) },
        selectedIds = rememberSaveable { mutableStateOf(selectedIds) },
        isActive = rememberSaveable { mutableStateOf(isActive) },
        menuProvider = menuProvider
    )
    val selectionToolbarState = LocalSelectionToolbarState.current
    LaunchedEffect(Unit) {
        selectionToolbarState.restoreSelectionMode(data)
    }
    return data
}

@Stable
class SelectionDataItems<ID> internal constructor(
    val totalItemCount: MutableIntState,
    val selectedIds: MutableState<List<ID>>,
    override val isActive: MutableState<Boolean>,
    override var menuProvider: @Composable (data: SelectionDataItems<ID>) -> Unit
) : SelectionData<ID, SelectionDataItems<ID>> {

    override val total: Int
        get() = totalItemCount.value

    override val selected: Int
        get() = selectedIds.value.size

    override fun isSelected(id: ID): Boolean {
        return selectedIds.value.contains(id)
    }

    override fun select(id: ID) {
        if (!isSelected(id)) {
            selectedIds.value = selectedIds.value + id
        }
    }

    override fun deselect(id: ID) {
        if (isSelected(id)) {
            selectedIds.value = selectedIds.value - id
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
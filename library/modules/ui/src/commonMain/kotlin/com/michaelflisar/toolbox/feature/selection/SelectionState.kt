package com.michaelflisar.toolbox.feature.selection

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Stable
class SelectionState<ID>(
    allIdsFlow: StateFlow<Set<ID>>,
    scope: CoroutineScope
) {
    private var allIds: Set<ID> = emptySet()
    private var _selected by mutableStateOf<Set<ID>>(emptySet())
    private var _visible by mutableStateOf<Boolean>(false)

    init {
        allIdsFlow
            .onEach { ids ->
                allIds = ids
                _selected = _selected.intersect(ids)
            }
            .launchIn(scope)
    }

    val selected: Set<ID>
        get() = _selected

    val visible: Boolean
        get() = _visible

    val selectedCount: Int
        get() = _selected.size

    val totalCount: Int
        get() = allIds.size

    val isSomethingSelected: Boolean
        get() = _selected.isNotEmpty()

    val isAllSelected: Boolean
        get() = _selected.size == allIds.size

    fun isSelected(id: ID): Boolean =
        id in _selected

    fun select(id: ID) {
        if (id !in _selected) {
            _selected += id
        }
        show()
    }

    fun deselect(id: ID) {
        if (id in _selected) {
            _selected -= id
        }
        show()
    }

    fun toggle(id: ID) {
        if (id in _selected) deselect(id) else select(id)
    }

    fun select(id: ID, selected: Boolean) {
        if (selected) {
            select(id)
        } else {
            deselect(id)
        }
        show()
    }

    fun clear() {
        if (_selected.isNotEmpty()) {
            _selected = emptySet()
        }
        show()
    }

    fun selectAll() {
        if (_selected.size != allIds.size) {
            _selected = allIds
        }
        show()
    }

    fun set(ids: Collection<ID>) {
        _selected = ids.toSet()
        show()
    }

    fun invert() {
        _selected = allIds - _selected
        show()
    }

    fun show() {
        _visible = true
    }

    fun clearAndClose() {
        clear()
        _visible = false
    }
}
package com.michaelflisar.toolbox.feature.selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

interface SelectionData<ID, Data : SelectionData<ID, Data>> {

    val selected: Int
    val total: Int

    fun isSelected(id: ID): Boolean
    fun select(id: ID)
    fun deselect(id: ID)
    fun toggle(id: ID)
    fun clearSelection()

    fun select(id: ID, selected: Boolean) {
        if (selected) {
            select(id)
        } else {
            deselect(id)
        }
    }

    fun deselect(ids: List<ID>) {
        ids.forEach {
            deselect(it)
        }
    }

    fun select(ids: List<ID>) {
        ids.forEach {
            select(it)
        }
    }

    val isActive: MutableState<Boolean>

    var menuProvider: @Composable (data: Data) -> Unit

    @Suppress("UNCHECKED_CAST")
    @Composable
    fun Menu() {
        menuProvider(this as Data)
    }
}
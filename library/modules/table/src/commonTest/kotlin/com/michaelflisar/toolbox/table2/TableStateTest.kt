package com.michaelflisar.toolbox.table2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TableStateTest {
    private fun populatedState() = TableState(TableDisplay.Paged()).apply {
        search("search")
        setFilter("name", TableFilterValue.Text("filter"))
        setSort("name", TableSortDirection.Descending)
        select(1)
        setPage(2)
    }

    @Test fun resetWithoutFlagsResetsAllInteractionStateButPreservesDisplay() {
        val state = populatedState()
        val display = state.display
        val version = state.positionVersion
        state.reset()
        assertEquals(TableQuery(), state.query)
        assertTrue(state.selectedKeys.isEmpty())
        assertEquals(0, state.page)
        assertEquals(version + 1, state.positionVersion)
        assertEquals(display, state.display)
    }

    @Test fun individualQueryFlagsPreserveOtherAreasAndResetPosition() {
        val resets: List<(TableState) -> Unit> = listOf(
            { it.reset(search = true) },
            { it.reset(filters = true) },
            { it.reset(sorts = true) }
        )
        resets.forEachIndexed { index, reset ->
            val state = populatedState()
            val before = state.query
            val version = state.positionVersion
            reset(state)
            assertEquals(
                before.copy(
                    search = if (index == 0) "" else before.search,
                    filters = if (index == 1) emptyMap() else before.filters,
                    sorts = if (index == 2) emptyList() else before.sorts
                ),
                state.query
            )
            assertEquals(setOf<Any>(1), state.selectedKeys)
            assertEquals(0, state.page)
            assertEquals(version + 1, state.positionVersion)
        }
    }

    @Test fun selectionResetPreservesQueryAndPosition() {
        val state = populatedState()
        val query = state.query
        val version = state.positionVersion
        state.reset(selection = true)
        assertTrue(state.selectedKeys.isEmpty())
        assertEquals(query, state.query)
        assertEquals(2, state.page)
        assertEquals(version, state.positionVersion)
    }

    @Test fun positionResetPreservesQueryAndSelectionAndWorksAtPageZero() {
        val state = populatedState()
        val query = state.query
        val version = state.positionVersion
        state.reset(position = true)
        assertEquals(0, state.page)
        assertEquals(query, state.query)
        assertEquals(setOf<Any>(1), state.selectedKeys)
        assertEquals(version + 1, state.positionVersion)
        state.reset(position = true)
        assertEquals(version + 2, state.positionVersion)
    }

    @Test fun combinedQueryResetChangesPositionOnlyOnce() {
        val state = populatedState()
        val version = state.positionVersion
        state.reset(search = true, filters = true, sorts = true)
        assertEquals(TableQuery(), state.query)
        assertEquals(setOf<Any>(1), state.selectedKeys)
        assertEquals(version + 1, state.positionVersion)
    }

    @Test fun disabledFlagsDoNotResetAnything() {
        val state = populatedState()
        val query = state.query
        val version = state.positionVersion
        state.reset(search = false)
        assertEquals(query, state.query)
        assertEquals(2, state.page)
        assertEquals(setOf<Any>(1), state.selectedKeys)
        assertEquals(version, state.positionVersion)
    }
}

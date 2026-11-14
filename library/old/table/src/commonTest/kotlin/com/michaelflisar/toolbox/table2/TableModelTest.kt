package com.michaelflisar.toolbox.table2

import androidx.compose.ui.unit.dp
import kotlin.test.*
import kotlinx.coroutines.test.runTest

class TableModelTest {
    @Test fun replacementPreservesSettingsAndPrunesSelectionWithoutNewModel() = runTest {
        val state = TableState(TableDisplay.Paged(1, listOf(1)))
        val model = LocalTableModel(items, columns, state) { it.id }
        state.setSort("age", TableSortDirection.Descending)
        state.search("Anna")
        state.select(0)
        state.select(2)
        state.setPage(1)
        val query = state.query
        model.replaceAll(listOf(items[0]))
        assertEquals(query, state.query)
        assertEquals(setOf<Any>(0), state.selectedKeys)
        assertEquals(0, state.page)
        assertEquals(listOf(items[0]), model.items)
        assertFailsWith<IllegalArgumentException> { model.replaceAll(listOf(items[0], items[0])) }
        assertEquals(listOf(items[0]), model.items)
        model.clear()
        assertTrue(model.items.isEmpty())
        assertTrue(state.selectedKeys.isEmpty())
        assertEquals(query, state.query)
        assertEquals(TableDisplay.Paged(1, listOf(1)), state.display)
    }
    private data class Person(val id: Int, val name: String, val age: Int)
    private val name = textColumn<Person>("name", "Name", { it.name }, filter = textFilter())
    private val age = numberColumn<Person, Int>("age", "Age", { it.age }, filter = numberFilter(String::toIntOrNull))
    private val columns = listOf(name, age)
    private val items = listOf(Person(0, "Anna", 30), Person(1, "Ben", 20), Person(2, "Anna", 20))

    @Test fun modelOwnsMutationsAndKeepsDataWhenColumnsChange() = runTest {
        val state = TableState(TableDisplay.Paged(1, listOf(1)))
        val model = LocalTableModel(items, columns, state) { it.id }
        state.select(2)
        state.setPage(2)
        model.remove(2)
        assertEquals(2, model.items.size)
        assertEquals(1, state.page)
        assertTrue(state.selectedKeys.isEmpty())
        model.update(1, Person(1, "Changed", 50))
        assertEquals("Changed", model.sortedItems.single { it.id == 1 }.name)
        model.insert(Person(3, "New", 10))
        assertEquals(3, model.totalCount)
        model.updateColumns(listOf(name))
        assertEquals(3, model.items.size)
        assertFailsWith<IllegalArgumentException> { model.insert(items[0]) }
        assertFailsWith<IllegalArgumentException> { model.update(1, Person(9, "Wrong key", 0)) }
        assertEquals(3, model.totalCount)
    }

    @Test fun filterAndSortBeforePagination() {
        val state = TableState(TableDisplay.Paged(1, listOf(1, 2)))
        val model = LocalTableModel(items, columns, state) { it.id }
        state.setFilter("name", TableFilterValue.Text("Ann"))
        state.setSort("age", TableSortDirection.Ascending)
        assertEquals(listOf(2, 0), model.sortedItems.map { it.id })
        assertEquals(2, model.totalCount)
        state.setPage(1)
        assertEquals(1, model.firstVisibleIndex)
        assertEquals(0, model.itemAt(model.firstVisibleIndex)?.id)
        state.search("Anna")
        assertEquals(0, state.page)
    }

    @Test fun selectionUsesKeysAndSurvivesQueryChanges() {
        val state = TableState()
        val model = LocalTableModel(items, columns, state) { it.id }
        state.select(1)
        state.setSort("age", TableSortDirection.Descending)
        state.search("Anna")
        assertEquals(listOf(items[1]), model.selectedItems)
        state.retainSelection(setOf(0, 2))
        assertTrue(state.selectedKeys.isEmpty())
    }

    @Test fun multiSortKeepsPriorityAndStableTies() {
        val state = TableState()
        state.setSort("name", TableSortDirection.Ascending)
        state.setSort("age", TableSortDirection.Ascending)
        state.setSort("name", TableSortDirection.Descending)
        assertEquals(listOf("name", "age"), state.query.sorts.map { it.columnId })
        assertEquals(listOf(1, 2, 0), applyTableQuery(items, columns, state.query).map { it.id })
        val ties = items + Person(3, "Anna", 20)
        assertEquals(listOf(2, 3, 0, 1), applyTableQuery(ties, columns,
            TableQuery(sorts = listOf(TableSort("name", TableSortDirection.Ascending), TableSort("age", TableSortDirection.Ascending)))
        ).map { it.id })
    }

    @Test fun allTextOperatorsAndCaseSensitivity() {
        val filter = textFilter()
        val cases = mapOf(
            TableFilterOperator.Equal to ("anna" to true),
            TableFilterOperator.NotEqual to ("ben" to true),
            TableFilterOperator.StartsWith to ("an" to true),
            TableFilterOperator.EndsWith to ("na" to true),
            TableFilterOperator.Contains to ("nn" to true),
            TableFilterOperator.NotContains to ("xx" to true)
        )
        cases.forEach { (operator, test) -> assertEquals(test.second, filter.matches("Anna", TableFilterValue.Text(test.first, operator))) }
        assertFalse(filter.matches("Anna", TableFilterValue.Text("anna", TableFilterOperator.Equal, false)))
    }

    @Test fun numericPrecisionAndOperators() {
        val filter = numberFilter(String::toLongOrNull)
        val large = 9_007_199_254_740_993L
        assertTrue(filter.matches(large, TableFilterValue.Number("$large")))
        assertFalse(filter.matches(large, TableFilterValue.Number("${large - 1}")))
        assertTrue(filter.matches(large, TableFilterValue.Number("${large - 1}", TableFilterOperator.Greater)))
        assertTrue(filter.matches(12L, TableFilterValue.Number("2", TableFilterOperator.Contains)))
        assertFalse(filter.accepts(TableFilterValue.Number("invalid")))
    }

    @Test fun choiceBooleanAndIndependentStates() {
        val choice = TableFilter.Choice(listOf("A", "B"), { it }, multiSelect = true)
        assertTrue(choice.matches("B", TableFilterValue.Choices(setOf("A", "B"))))
        assertFalse(choice.matches("B", TableFilterValue.Choices(setOf("A"))))
        assertFalse(choice.accepts(TableFilterValue.Choices(setOf("C"))))
        assertTrue(TableFilter.BooleanFilter().matches(false, TableFilterValue.BooleanValue(false)))
        val left = TableState()
        val right = TableState()
        left.setFilter("name", TableFilterValue.Text("Anna"))
        assertTrue(right.query.filters.isEmpty())
    }

    @Test fun invalidDefinitionsAndRemovedColumns() {
        assertFailsWith<IllegalArgumentException> { ColumnWidth.Weight(0f) }
        assertFailsWith<IllegalArgumentException> { ColumnWidth.Fixed((-1).dp) }
        assertFailsWith<IllegalArgumentException> { LocalTableModel(items, listOf(name, name), TableState()) { it.id } }
        assertFailsWith<IllegalArgumentException> { LocalTableModel(items + items[0], columns, TableState()) { it.id } }
        val state = TableState()
        state.setSort("age", TableSortDirection.Ascending)
        state.setFilter("age", TableFilterValue.Number("1"))
        state.reconcileColumns(listOf(name))
        assertTrue(state.query.sorts.isEmpty())
        assertTrue(state.query.filters.isEmpty())
    }

    @Test fun weightedMinimaRedistributeSpace() {
        assertEquals(listOf(80f, 20f), allocateWidths(listOf(ColumnWidth.Weight(minWidth = 80.dp), ColumnWidth.Weight(minWidth = 0.dp)), 100f, listOf(0f, 0f)))
        assertEquals(listOf(80f, 48f), allocateWidths(listOf(ColumnWidth.Weight(minWidth = 80.dp), ColumnWidth.Weight()), 50f, listOf(0f, 0f)))
        assertEquals(emptyList(), allocateWidths(emptyList(), 10f, emptyList()))
    }
}

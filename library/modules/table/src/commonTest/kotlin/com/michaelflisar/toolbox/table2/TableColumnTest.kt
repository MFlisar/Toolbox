package com.michaelflisar.toolbox.table2

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TableColumnTest {
    private enum class Kind { Zebra, Apple }
    private data class Item(val text: String, val number: Int, val kind: Kind, val active: Boolean)

    @Test fun standardFactoriesProvideNaturalSortingWithoutImplicitFilters() {
        val text = textColumn<Item>("text", "Text", { it.text })
        val number = numberColumn<Item, Int>("number", "Number", { it.number })
        val kind = enumColumn<Item, Kind>("kind", "Kind", { it.kind })
        val active = booleanColumn<Item>("active", "Active", { it.active })
        val first = Item("A", 2, Kind.Zebra, false)
        val second = Item("B", 10, Kind.Apple, true)
        listOf(text, number, kind, active).forEach {
            assertTrue(it.sortable)
            assertNull(it.filter)
            assertEquals(TableColumnDefaults.Width, it.width)
            assertEquals(TableColumnDefaults.Padding, it.padding)
            assertEquals(TableColumnDefaults.CellContentAlignment, it.cellContentAlignment)
            assertEquals(TableColumnDefaults.HeaderContentAlignment, it.headerContentAlignment)
            assertTrue(it.compare(first, second) < 0)
        }
        assertEquals("Zebra", kind.search(first))
        assertEquals("false", active.search(first))
    }

    @Test fun factoriesAllowOverridingAllColumnSettings() {
        val filter = textFilter()
        val column = textColumn<Item>(
            id = "text", title = "Title", value = { it.text },
            width = ColumnWidth.Fixed(80.dp),
            filter = filter, comparator = null, searchText = null,
            padding = PaddingValues(3.dp),
            cellContentAlignment = Alignment.BottomEnd,
            headerContentAlignment = Alignment.TopCenter,
            description = "Description",
            headerContent = { Text("Custom header") },
            cellContent = { Text(it.value) }
        )
        assertFalse(column.sortable)
        assertNull(column.search(Item("A", 0, Kind.Zebra, false)))
        assertEquals(filter, column.filter)
        assertEquals(PaddingValues(3.dp), column.padding)
        assertEquals(Alignment.BottomEnd, column.cellContentAlignment)
        assertEquals(Alignment.TopCenter, column.headerContentAlignment)
        assertEquals("Description", column.description)
        assertEquals(ColumnWidth.Fixed(80.dp), column.width)
    }

    @Test fun customFactorySupportsNullableValuesAndExplicitSearchAndComparison() {
        val column = customColumn<String?, String?>(
            id = "nullable", title = "Nullable", value = { it },
            comparator = nullsFirst(naturalOrder<String>()),
            searchText = { it ?: "empty" },
            cellContent = { Text(it.value ?: "-") }
        )
        assertTrue(column.compare(null, "A") < 0)
        assertEquals("empty", column.search(null))
        val action = customColumn<Item, Int>(
            "action", "Action", { it.number }, cellContent = { Text("${it.value}") }
        )
        assertFalse(action.sortable)
        assertNull(action.search(Item("A", 0, Kind.Zebra, false)))
    }

    @Test fun allFactoriesForwardIndependentContentAlignments() {
        val columns = listOf(
            textColumn<Item>("text", "Text", { it.text },
                cellContentAlignment = Alignment.BottomEnd, headerContentAlignment = Alignment.TopCenter),
            numberColumn<Item, Int>("number", "Number", { it.number },
                cellContentAlignment = Alignment.BottomEnd, headerContentAlignment = Alignment.TopCenter),
            enumColumn<Item, Kind>("kind", "Kind", { it.kind },
                cellContentAlignment = Alignment.BottomEnd, headerContentAlignment = Alignment.TopCenter),
            booleanColumn<Item>("active", "Active", { it.active },
                cellContentAlignment = Alignment.BottomEnd, headerContentAlignment = Alignment.TopCenter),
            customColumn<Item, String>("custom", "Custom", { it.text },
                cellContentAlignment = Alignment.BottomEnd, headerContentAlignment = Alignment.TopCenter,
                cellContent = { Text(it.value) })
        )
        columns.forEach {
            assertEquals(Alignment.BottomEnd, it.cellContentAlignment)
            assertEquals(Alignment.TopCenter, it.headerContentAlignment)
        }
    }
}

package com.michaelflisar.toolbox.table2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TableCellContextTest {
    @Test fun headerRendererContextPreservesColumnAndPresentation() {
        val columns = listOf(numberColumn<Int, Int>("id", "ID", { it }))
        val model = LocalTableModel(listOf(42), columns, TableState()) { it }
        val cell = TableHeaderCell(model, columns[0], 0, TableTexts.German,
            showFilter = false, filterPresentation = TableFilterPresentation.Popup)
        val content = TableHeaderContent(cell)
        assertSame(model, content.table)
        assertSame(model.state, content.state)
        assertSame(columns[0], content.column)
        assertSame(TableTexts.German, content.texts)
        assertEquals(0, content.columnIndex)
        assertEquals(false, content.showFilter)
        assertEquals(TableFilterPresentation.Popup, content.filterPresentation)
    }

    @Test fun typedValueContextPreservesCoordinatesAndObservesSelection() {
        val column = textColumn<Int>("name", "Name", { "Item $it" })
        val model = LocalTableModel(listOf(42), listOf(column), TableState()) { it }
        val cell = TableCell(model, column, 42, 17, 3, TableTexts.English)
        val value = TableValueCell(cell, column.value(cell.item)) {}
        assertSame(model, value.table)
        assertSame(column, value.column)
        assertEquals(42, value.item)
        assertEquals(17, value.rowIndex)
        assertEquals(3, value.columnIndex)
        assertEquals("Item 42", value.value)
        model.state.select(42)
        assertTrue(value.selected)
    }
}

package com.michaelflisar.toolbox.table2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TableFilterDraftTest {
    @Test fun clearIsEnabledOnlyWhenItChangesEditorState() {
        val text = TableFilterDraft(textFilter(), null)
        assertFalse(text.canClear)
        text.change(TableFilterValue.Text(""))
        assertFalse(text.canClear)
        text.change(TableFilterValue.Text("", ignoreCase = false))
        assertTrue(text.canClear)
        text.change(TableFilterValue.Text("value"))
        assertTrue(text.canClear)
        text.change(null)
        assertFalse(text.canClear)

        val number = TableFilterDraft(numberFilter(String::toIntOrNull), TableFilterValue.Number(""))
        assertFalse(number.canClear)
        number.change(TableFilterValue.Number("invalid"))
        assertTrue(number.canClear)
        number.change(TableFilterValue.Number("", TableFilterOperator.Greater))
        assertTrue(number.canClear)

        val choice = TableFilterDraft(
            TableFilter.Choice(listOf("a"), { it }), TableFilterValue.Choices(emptySet())
        )
        assertFalse(choice.canClear)
        choice.change(TableFilterValue.Choices(setOf("a")))
        assertTrue(choice.canClear)
        val boolean = TableFilterDraft(TableFilter.BooleanFilter(), TableFilterValue.BooleanValue(false))
        assertTrue(boolean.canClear)
    }

    @Test fun popupAppliesOnlyValidChangesImmediatelyWhileDialogWaitsForCommit() {
        var active: TableFilterValue? = null
        val draft = TableFilterDraft(numberFilter(String::toIntOrNull), null)
        draft.edit(TableFilterValue.Number("12"), TableFilterPresentation.Popup) { active = it }
        assertEquals(TableFilterValue.Number("12"), active)
        draft.edit(TableFilterValue.Number("invalid"), TableFilterPresentation.Popup) { active = it }
        assertEquals(TableFilterValue.Number("12"), active)
        draft.edit(null, TableFilterPresentation.Popup) { active = it }
        assertNull(active)
        draft.edit(TableFilterValue.Number("42"), TableFilterPresentation.Dialog) { active = it }
        assertNull(active)
        draft.commit { active = it }
        assertEquals(TableFilterValue.Number("42"), active)
    }

    @Test fun editingAndClearingLeaveActiveQueryUnchangedUntilCommit() {
        val state = TableState()
        val original = TableFilterValue.Text("original")
        state.setFilter("name", original)
        val draft = TableFilterDraft(textFilter(), original)
        draft.change(TableFilterValue.Text("edited"))
        assertEquals(original, state.query.filters["name"])
        draft.change(null)
        assertEquals(original, state.query.filters["name"])
        draft.commit { state.setFilter("name", it) }
        assertNull(state.query.filters["name"])
    }

    @Test fun abandoningDraftAndReopeningUsesActiveValue() {
        val original = TableFilterValue.Text("original")
        val abandoned = TableFilterDraft(textFilter(), original)
        abandoned.change(TableFilterValue.Text("not committed"))
        val reopened = TableFilterDraft(textFilter(), original)
        assertEquals(original, reopened.value)
    }

    @Test fun invalidNumericAndCustomDraftsCannotCommit() {
        val numeric = TableFilterDraft(numberFilter(String::toIntOrNull), null)
        numeric.change(TableFilterValue.Number("invalid"))
        assertFalse(numeric.valid)
        assertFailsWith<IllegalStateException> { numeric.commit { error("Must not commit") } }
        numeric.change(TableFilterValue.Number("42"))
        assertTrue(numeric.valid)
        numeric.commit { assertEquals(TableFilterValue.Number("42"), it) }

        val custom = TableFilter.Custom<String>(
            type = "custom", validate = { it["value"] == "valid" },
            predicate = { _, _ -> true }, summary = { "" }, content = {}
        )
        val draft = TableFilterDraft(custom, null)
        draft.change(TableFilterValue.Custom("custom", mapOf("value" to "invalid")))
        assertFalse(draft.valid)
        assertFailsWith<IllegalStateException> { draft.commit { error("Must not commit") } }
    }

    @Test fun emptyTextNumberAndChoicesCommitAsClearedFilters() {
        val drafts = listOf(
            TableFilterDraft(textFilter(), TableFilterValue.Text("")),
            TableFilterDraft(numberFilter(String::toIntOrNull), TableFilterValue.Number("")),
            TableFilterDraft(TableFilter.Choice(listOf("a"), { it }), TableFilterValue.Choices(emptySet()))
        )
        drafts.forEach { it.commit { value -> assertNull(value) } }
    }

    @Test fun choiceAndBooleanDraftsCommitStructuredValues() {
        val choices = TableFilterDraft(
            TableFilter.Choice(listOf("a", "b"), { it }, multiSelect = true), null
        )
        choices.change(TableFilterValue.Choices(setOf("a", "b")))
        choices.commit { assertEquals(TableFilterValue.Choices(setOf("a", "b")), it) }
        val boolean = TableFilterDraft(TableFilter.BooleanFilter(), null)
        boolean.change(TableFilterValue.BooleanValue(false))
        boolean.commit { assertEquals(TableFilterValue.BooleanValue(false), it) }
    }
}

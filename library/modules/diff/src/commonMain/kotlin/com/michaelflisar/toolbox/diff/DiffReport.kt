package com.michaelflisar.toolbox.diff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.ui.components.StyledCard
import com.michaelflisar.composestyled.ui.components.StyledPrimaryScrollableTabRow
import com.michaelflisar.composestyled.ui.components.StyledTab
import com.michaelflisar.composestyled.ui.layout.StyledColumn
import com.michaelflisar.composestyled.ui.layout.StyledRow
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.specials.StyledInformationRowHorizontal
import com.michaelflisar.toolbox.diff.classes.Change
import com.michaelflisar.toolbox.diff.classes.DiffResult
import kotlinx.coroutines.launch

@Composable
fun DiffReport(
    result: DiffResult,
) {
    if (result.changes.isEmpty()) {
        StyledText(
            text = "Keine Änderungen gefunden.",
            style = StyledTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
        return
    }

    val resultsNotEmpty =
        remember(result.tableResults) { result.tableResults.filter { it.changes.isNotEmpty() } }

    val scope = rememberCoroutineScope()
    val tableNames =
        remember(result.tableResults) { result.tableResults.map { it.tableName }.sorted() }
    val tabTitles = listOf("Statistiken") + resultsNotEmpty.map { it.tableName }.sorted()
    val pagerState = rememberPagerState { tabTitles.size }

    Column {
        StyledPrimaryScrollableTabRow(
            edgePadding = 0.dp,
            selectedTabIndex = pagerState.currentPage
        ) {
            tabTitles.forEachIndexed { index, title ->
                StyledTab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { StyledText(title) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) { page ->
            when (page) {
                0 -> {
                    Box(modifier = Modifier.padding(StyledTheme.padding.medium)) {
                        DiffStatistics(result, tableNames)
                    }
                }

                else -> {
                    val table = tableNames[page - 1]
                    val itemsForTable = result.getChangesForTable(table)
                    val changesForTables = itemsForTable.groupBy { it.entityId }.values.toList()
                    LazyColumn(
                        modifier = Modifier.padding(StyledTheme.padding.medium),
                        verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.medium)
                    ) {
                        if (changesForTables.isEmpty()) {
                            item {
                                StyledText(
                                    text = "Keine Änderungen in dieser Tabelle.",
                                    style = StyledTheme.typography.bodyMedium
                                )
                            }
                        }
                        itemsIndexed(changesForTables) { index, changes ->
                            DiffEntry(index, changesForTables.size, changes)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiffStatistics(
    result: DiffResult,
    tableNames: List<String>,
) {
    StyledColumn {
        val labelWidth = 200.dp
        StyledText(
            text = "Änderungen",
            modifier = Modifier.fillMaxWidth(),
            style = StyledTheme.typography.titleMedium
        )
        StyledInformationRowHorizontal(
            label = "Gesamt",
            info = "${result.changes.size}",
            labelWidth = labelWidth
        )
        StyledText(
            text = "Tabellen pro Tabelle",
            modifier = Modifier.fillMaxWidth(),
            style = StyledTheme.typography.titleMedium
        )
        tableNames.forEach { table ->
            StyledInformationRowHorizontal(
                label = "Tabelle $table",
                info = "${result.getChangesForTable(table).size}",
                labelWidth = labelWidth
            )

            val changes = result.getChangesForTable(table)
            val added = changes.filterIsInstance<Change.Added>()
            val removed = changes.filterIsInstance<Change.Removed>()
            val updated = changes.filterIsInstance<Change.Updated>()
            val updatedFields =
                updated.map { it.fields.map { it.field } }.flatten().distinct().sorted()
            val labelStyle = StyledTheme.typography.bodyMedium

            if (added.isNotEmpty()) {
                StyledInformationRowHorizontal(
                    label = "• Hinzugefügt",
                    info = "${added.size}",
                    labelWidth = labelWidth,
                    labelStyle = labelStyle
                )
            }
            if (removed.isNotEmpty()) {
                StyledInformationRowHorizontal(
                    label = "• Entfernt",
                    info = "${removed.size}",
                    labelWidth = labelWidth,
                    labelStyle = labelStyle
                )
            }
            for (field in updatedFields) {
                val count = updated.sumOf { it.fields.count { it.field == field } }
                StyledInformationRowHorizontal(
                    label = "• $field geändert",
                    info = "$count",
                    labelWidth = labelWidth,
                    labelStyle = labelStyle
                )
            }
        }
    }
}

@Composable
private fun DiffEntry(
    index: Int,
    size: Int,
    changes: List<Change>,
) {
    StyledCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        StyledColumn {
            StyledRow {
                StyledText(
                    text = "${index + 1} / $size",
                    style = StyledTheme.typography.titleMedium
                )
                StyledText(
                    text = changes.firstOrNull()?.entityLabel ?: ""
                )
            }

            changes.forEach { change ->
                when (change) {
                    is Change.Added -> StyledText(text = "• Hinzugefügt")
                    is Change.Removed -> StyledText(text = "• Entfernt")
                    is Change.Updated -> change.fields.forEach { field ->
                        StyledText(text = "• ${field.field}: ${field.before} → ${field.after}")
                    }
                }
            }
        }
    }
}



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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components.MyCard
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyFilledTitle
import com.michaelflisar.toolbox.components.MyLabeledInformationHorizontal
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.diff.classes.Change
import com.michaelflisar.toolbox.diff.classes.ChangeCategory
import com.michaelflisar.toolbox.diff.classes.DiffResult
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing
import kotlinx.coroutines.launch

@Composable
fun DiffReport(
    result: DiffResult
) {
    if (result.changes.isEmpty()) {
        Text(
            text = "Keine Änderungen gefunden.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
        return
    }

    val resultsNotEmpty = remember(result.tableResults) { result.tableResults.filter { it.changes.isNotEmpty() } }

    val scope = rememberCoroutineScope()
    val tableNames = remember(result.tableResults) { result.tableResults.map { it.tableName }.sorted() }
    val tabTitles = listOf("Statistiken") + resultsNotEmpty.map { it.tableName }.sorted()
    val pagerState = rememberPagerState { tabTitles.size }

    Column {
        SecondaryScrollableTabRow(
            edgePadding = 0.dp,
            selectedTabIndex = pagerState.currentPage
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
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
                    Box(modifier = Modifier.padding(MaterialTheme.padding.default)) {
                        DiffStatistics(result, tableNames)
                    }
                }

                else -> {
                    val table = tableNames[page - 1]
                    val itemsForTable = result.getChangesForTable(table)
                    val changesForTables = itemsForTable.groupBy { it.entityId }.values.toList()
                    LazyColumn(
                        modifier = Modifier.padding(MaterialTheme.padding.default),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)
                    ) {
                        if (changesForTables.isEmpty()) {
                            item {
                                Text(
                                    text = "Keine Änderungen in dieser Tabelle.",
                                    style = MaterialTheme.typography.bodyMedium
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
    tableNames: List<String>
) {
    MyColumn {
        val labelWidth = 200.dp
        MyFilledTitle(text = "Änderungen", modifier = Modifier.fillMaxWidth())
        MyLabeledInformationHorizontal(
            label = "Gesamt",
            info = "${result.changes.size}",
            labelWidth = labelWidth
        )
        MyFilledTitle(text = "Tabellen pro Tabelle", modifier = Modifier.fillMaxWidth())
        tableNames.forEach { table ->
            MyLabeledInformationHorizontal(
                label = "Tabelle $table",
                info = "${result.getChangesForTable(table).size}",
                labelWidth = labelWidth
            )

            val changes = result.getChangesForTable(table)
            val added = changes.filterIsInstance<Change.Added>()
            val removed = changes.filterIsInstance<Change.Removed>()
            val updated = changes.filterIsInstance<Change.Updated>()
            val updatedFields = updated.map { it.fields.map { it.field } }.flatten().distinct().sorted()
            val labelStyle = MaterialTheme.typography.bodyMedium

            if (added.isNotEmpty()) {
                MyLabeledInformationHorizontal(
                    label = "• Hinzugefügt",
                    info = "${added.size}",
                    labelWidth = labelWidth,
                    labelStyle = labelStyle
                )
            }
            if (removed.isNotEmpty()) {
                MyLabeledInformationHorizontal(
                    label = "• Entfernt",
                    info = "${removed.size}",
                    labelWidth = labelWidth,
                    labelStyle = labelStyle
                )
            }
            for (field in updatedFields) {
                val count = updated.sumOf { it.fields.count { it.field == field } }
                MyLabeledInformationHorizontal(
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
    changes: List<Change>
) {
    MyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        MyColumn {
            MyRow {
                Text(
                    text = "${index + 1} / $size",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = changes.firstOrNull()?.entityLabel ?: ""
                )
            }

            changes.forEach { change ->
                when (change) {
                    is Change.Added -> Text(text = "• Hinzugefügt")
                    is Change.Removed -> Text(text = "• Entfernt")
                    is Change.Updated -> change.fields.forEach { field ->
                        Text(text = "• ${field.field}: ${field.before} → ${field.after}")
                    }
                }
            }
        }
    }
}



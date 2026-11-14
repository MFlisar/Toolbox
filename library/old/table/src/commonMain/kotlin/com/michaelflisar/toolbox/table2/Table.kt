package com.michaelflisar.toolbox.table2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.theme.scrollbar
import com.michaelflisar.toolbox.ui.MyScrollableLazyColumn
import kotlinx.coroutines.flow.distinctUntilChanged

sealed interface TableInteraction<Item> {
    class None<Item> : TableInteraction<Item>
    class RowClick<Item>(val onClick: (Item) -> Unit) : TableInteraction<Item>
    class CellClick<Item>(val onClick: (Item, TableColumn<Item, *>) -> Unit) : TableInteraction<Item>
    class Select<Item> : TableInteraction<Item>
}

/** Controls header-surface clicks independently of controls rendered inside the header. */
sealed interface TableHeaderInteraction<out Item> {
    data object None : TableHeaderInteraction<Nothing>
    data object SortOnClick : TableHeaderInteraction<Nothing>
    data object MenuOnClick : TableHeaderInteraction<Nothing>
    class Click<Item>(val onClick: (TableColumn<Item, *>) -> Unit) : TableHeaderInteraction<Item>
}

data class TableColors(
    val container: Color,
    val content: Color,
    val header: Color,
    val headerContent: Color,
    val selected: Color,
    val selectedContent: Color,
    val divider: Color
)

object TableDefaults {
    @Composable
    fun colors() = TableColors(
        MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.outlineVariant
    )
}

open class TableScope<Item> internal constructor(
    val table: TableModel<Item>,
    val texts: TableTexts
) {
    val state: TableState get() = table.state
}

open class TableHeaderContext<Item> internal constructor(
    val table: TableModel<Item>,
    val column: TableColumn<Item, *>,
    /** Zero-based position in the model's full column list. */
    val columnIndex: Int,
    val texts: TableTexts,
    val showFilter: Boolean = true,
    val filterPresentation: TableFilterPresentation = TableFilterPresentation.Dialog
) {
    val state: TableState get() = table.state
}

/** Column-renderer context. Content renders the built-in header, never the column renderer. */
class TableHeaderContent<Item> internal constructor(base: TableHeaderContext<Item>) :
    TableHeaderContext<Item>(base.table, base.column, base.columnIndex, base.texts, base.showFilter, base.filterPresentation) {
    @Composable
    fun Content() { DefaultHeaderContent() }
}

/** Table-slot context. Content delegates to the column's configured header renderer. */
class TableHeaderCell<Item> internal constructor(
    table: TableModel<Item>, column: TableColumn<Item, *>, columnIndex: Int, texts: TableTexts,
    showFilter: Boolean = true, filterPresentation: TableFilterPresentation = TableFilterPresentation.Dialog
) : TableHeaderContext<Item>(table, column, columnIndex, texts, showFilter, filterPresentation) {
    @Composable
    fun Content() { column.headerContent(TableHeaderContent(this)) }
}

open class TableCellContext<Item> internal constructor(
    val table: TableModel<Item>,
    val column: TableColumn<Item, *>,
    val item: Item,
    /** Absolute index in the filtered/sorted result, independent of the displayed page. */
    val rowIndex: Int,
    val columnIndex: Int,
    val texts: TableTexts
) {
    val state: TableState get() = table.state
    val selected: Boolean get() = table.itemKey(item) in state.selectedKeys
}

/** Table-slot context. Content delegates to the column's configured value renderer. */
class TableCell<Item> internal constructor(
    table: TableModel<Item>, column: TableColumn<Item, *>, item: Item,
    rowIndex: Int, columnIndex: Int, texts: TableTexts
) : TableCellContext<Item>(table, column, item, rowIndex, columnIndex, texts) {
    @Composable
    fun Content() { column.render(this) }
}

/** Column-renderer context. Content renders the factory default without calling the renderer again. */
class TableValueCell<Item, Value> internal constructor(
    base: TableCellContext<Item>,
    val value: Value,
    private val defaultContent: @Composable (Value) -> Unit
) : TableCellContext<Item>(base.table, base.column, base.item, base.rowIndex, base.columnIndex, base.texts) {
    @Composable
    fun Content() { defaultContent(value) }
}

enum class TableErrorKind { Count, Range }

class TableError internal constructor(
    val error: Throwable,
    val kind: TableErrorKind,
    /** Absolute affected row, or null for count failures. */
    val index: Int? = null,
    private val onRetry: () -> Unit
) {
    fun retry() = onRetry()
}

/**
 * A counted, lazy table. Slots render contents only; this composable owns cell placement,
 * column correspondence, padding, dividers and exclusive row/cell/selection interactions.
 * Give the table a bounded height (for example fillMaxSize or weight in a Column).
 * [showHeaderFilters] hides header filter entries without clearing active filters.
 * [errorContent] handles both count failures and individual unloaded row failures.
 */
@Composable
fun <Item> Table(
    table: TableModel<Item>,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: TableColors = TableDefaults.colors(),
    texts: TableTexts = TableTexts.English,
    interaction: TableInteraction<Item> = TableInteraction.None(),
    headerInteraction: TableHeaderInteraction<Item> = TableHeaderInteraction.SortOnClick,
    showColumnHeaders: Boolean = true,
    showHeaderFilters: Boolean = true,
    headerFilterPresentation: TableFilterPresentation = TableFilterPresentation.Dialog,
    lazyListState: LazyListState = rememberLazyListState(),
    header: (@Composable TableScope<Item>.() -> Unit)? = null,
    footer: (@Composable TableScope<Item>.() -> Unit)? = { TableFooter() },
    emptyContent: @Composable TableScope<Item>.() -> Unit = { TableEmptyContent() },
    errorContent: @Composable TableScope<Item>.(TableError) -> Unit = { TableErrorContent(it) },
    headerCell: @Composable (TableHeaderCell<Item>) -> Unit = { it.Content() },
    cell: @Composable (TableCell<Item>) -> Unit = { it.Content() }
) {
    val scope = remember(table, texts) { TableScope(table, texts) }
    val horizontal = rememberScrollState()
    val density = LocalDensity.current
    val columns = table.columns
    val count = table.visibleCount
    val first = table.firstVisibleIndex
    val total = table.totalCount
    val autoItems = if (table is LocalTableModel) table.items else table.loadedItems
    var measured by remember(columns, autoItems, density.density, density.fontScale, table.dataVersion) {
        mutableStateOf(List(columns.size) { 0f })
    }
    LaunchedEffect(table.state.positionVersion) {
        lazyListState.scrollToItem(0)
    }
    LaunchedEffect(table, first, count, total) {
        snapshotFlow {
            val visible = lazyListState.layoutInfo.visibleItemsInfo
            (visible.firstOrNull()?.index ?: 0) to (visible.lastOrNull()?.index ?: 0)
        }.distinctUntilChanged().collect { (start, end) ->
            if (count > 0) table.requestRange(first + start.coerceAtMost(count - 1), first + end.coerceAtMost(count - 1))
        }
    }
    // Auto is opt-in: local lists measure all rows; providers measure cached rows only.
    columns.forEachIndexed { index, column ->
        val width = column.width
        if (width is ColumnWidth.Auto) {
            val headerScope = TableHeaderCell(table, column, index, texts, showHeaderFilters, headerFilterPresentation)
            SubcomposeLayout(Modifier.clearAndSetSemantics {}) { _ ->
                val constraints = Constraints(maxWidth = with(density) { width.maxWidth.roundToPx() })
                var max = subcompose("header") {
                    Box(Modifier.width(IntrinsicSize.Max).padding(column.padding)) { headerCell(headerScope) }
                }.maxOfOrNull { it.measure(constraints).width } ?: 0
                autoItems.forEachIndexed { rowIndex, item ->
                    val size = subcompose(rowIndex) {
                        Box(Modifier.padding(column.padding)) {
                            cell(TableCell(table, column, item, rowIndex, index, texts))
                        }
                    }.maxOfOrNull { it.measure(constraints).width } ?: 0
                    max = maxOf(max, size)
                }
                val dp = with(density) { max.toDp().value }
                if (measured[index] != dp) measured = measured.toMutableList().also { it[index] = dp }
                layout(0, 0) {}
            }
        }
    }
    Column(
        modifier.clip(shape).background(colors.container).border(1.dp, colors.divider, shape)
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.content) {
            header?.invoke(scope)
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
                val scrollbarWidth = MaterialTheme.scrollbar.size
                val available = (maxWidth - scrollbarWidth - 1.dp * (columns.size - 1).coerceAtLeast(0)).value
                val widths = allocateWidths(columns.map { it.width }, available.coerceAtLeast(0f), measured)
                val fullWidth = (widths.sum() + (columns.size - 1).coerceAtLeast(0)).dp
                Column(Modifier.fillMaxSize()) {
                    if (showColumnHeaders) {
                        Row(
                            Modifier.fillMaxWidth().background(colors.header)
                                .horizontalScroll(horizontal).padding(end = scrollbarWidth)
                                .height(IntrinsicSize.Min)
                        ) {
                            columns.forEachIndexed { index, column ->
                                val headerScope = TableHeaderCell(table, column, index, texts, showHeaderFilters, headerFilterPresentation)
                                var showMenu by remember(column.id) { mutableStateOf(false) }
                                val sort = table.state.query.sorts.find { it.columnId == column.id }
                                val clickable = when (headerInteraction) {
                                    TableHeaderInteraction.None -> false
                                    TableHeaderInteraction.SortOnClick -> column.sortable
                                    TableHeaderInteraction.MenuOnClick -> column.sortable || (showHeaderFilters && column.filter != null)
                                    is TableHeaderInteraction.Click -> true
                                }
                                Box(
                                    Modifier.width(widths[index].dp).fillMaxHeight()
                                        .clickable(enabled = clickable) {
                                            when (headerInteraction) {
                                                TableHeaderInteraction.None -> Unit
                                                TableHeaderInteraction.SortOnClick -> table.state.toggleSort(column.id)
                                                TableHeaderInteraction.MenuOnClick -> showMenu = true
                                                is TableHeaderInteraction.Click -> headerInteraction.onClick(column)
                                            }
                                        }
                                        .semantics {
                                            contentDescription = listOf(column.title, column.description,
                                                sort?.let { texts.sortDescription(it.direction, table.state.query.sorts.indexOf(it) + 1) }.orEmpty()
                                            ).filter { it.isNotEmpty() }.joinToString(", ")
                                        }
                                        .padding(column.padding),
                                    contentAlignment = column.headerContentAlignment
                                ) {
                                    CompositionLocalProvider(LocalContentColor provides colors.headerContent) {
                                        headerCell(headerScope)
                                        if (showMenu) headerScope.HeaderMenu { showMenu = false }
                                    }
                                }
                                if (index < columns.lastIndex) VerticalDivider(color = colors.divider)
                            }
                        }
                        HorizontalDivider(color = colors.divider)
                    }
                    when {
                        table.error != null -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            scope.errorContent(TableError(requireNotNull(table.error), TableErrorKind.Count, onRetry = table::retryLoad))
                        }
                        total == null -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        count == 0 -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            scope.emptyContent()
                        }
                        else -> MyScrollableLazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            state = lazyListState,
                            itemSpacing = 0.dp
                        ) {
                            items(
                                count = count,
                                key = { relative ->
                                    val absolute = first + relative
                                    table.itemAt(absolute)?.let { LoadedKey(table.itemKey(it)) }
                                        ?: PlaceholderKey(absolute)
                                }
                            ) { relative ->
                                val absolute = first + relative
                                val item = table.itemAt(absolute)
                                if (item == null) {
                                    val failure = table.rangeError(absolute)
                                    Box(Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(8.dp)) {
                                        if (failure != null) scope.errorContent(TableError(failure, TableErrorKind.Range, absolute, table::retryLoad))
                                        else LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.Center))
                                    }
                                } else {
                                    val selected = table.itemKey(item) in table.state.selectedKeys
                                    val rowModifier = when (interaction) {
                                        is TableInteraction.RowClick -> Modifier.clickable { interaction.onClick(item) }
                                        is TableInteraction.Select -> Modifier.selectable(selected) {
                                            table.state.toggleSelection(table.itemKey(item))
                                        }
                                        else -> Modifier
                                    }
                                    Row(
                                        Modifier.fillMaxWidth()
                                            .background(if (selected) colors.selected else colors.container)
                                            .horizontalScroll(horizontal).width(fullWidth)
                                            .height(IntrinsicSize.Min).then(rowModifier)
                                    ) {
                                        CompositionLocalProvider(LocalContentColor provides if (selected) colors.selectedContent else colors.content) {
                                            columns.forEachIndexed { index, column ->
                                                val cellModifier = if (interaction is TableInteraction.CellClick) {
                                                    Modifier.clickable { interaction.onClick(item, column) }
                                                } else Modifier
                                                Box(
                                                    Modifier.width(widths[index].dp).fillMaxHeight()
                                                        .then(cellModifier).padding(column.padding),
                                                    contentAlignment = column.cellContentAlignment
                                                ) { cell(TableCell(table, column, item, absolute, index, texts)) }
                                                if (index < columns.lastIndex) VerticalDivider(color = colors.divider)
                                            }
                                        }
                                    }
                                }
                                if (relative < count - 1) HorizontalDivider(color = colors.divider)
                            }
                        }
                    }
                }
            }
            if (footer != null) {
                HorizontalDivider(color = colors.divider)
                footer.invoke(scope)
            }
        }
    }
}

private data class LoadedKey(val key: Any)
private data class PlaceholderKey(val index: Int)

package com.michaelflisar.toolbox.table

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.michaelflisar.toolbox.composables.MyInput
import com.michaelflisar.toolbox.ui.MyScrollableLazyColumn

object MyTable {

    data class State(
        val filters: SnapshotStateList<Filter>,
        val sorts: SnapshotStateList<Sort>,
        val selectedRows: SnapshotStateList<Int>
    )

    class Setup<T>(
        val clickType: ClickType<T> = ClickType.None()
    ) {
        sealed class ClickType<T> {
            class None<T> : ClickType<T>()
            class Select<T> : ClickType<T>()
            class RowClick<T>(val onRowClicked: (row: Int, item: T) -> Unit) : ClickType<T>()
            class CellClick<T>(val onCellClicked: (row: Int, column: Int, item: T) -> Unit) :
                ClickType<T>()
        }
    }


    data class Header(
        val label: String,
        val modifier: RowScope.() -> Modifier,
        val cellPadding: Dp = 4.dp,
        val textAlign: TextAlign = TextAlign.Unspecified
    )

    class Row<T>(
        val item: T,
        val cells: List<Cell>
    )

    class Sort(
        val columnIndex: Int,
        val type: Type = Type.Asc
    ) {
        enum class Type {
            Asc, Desc
        }

        fun <T> comparator() = when (type) {
            Type.Asc -> compareBy { it.cells[columnIndex].sort() }
            Type.Desc -> compareByDescending<Row<T>> { it.cells[columnIndex].sort() }
        }
    }

    data class Filter(
        val columnIndex: Int,
        val filter: String
    )

    sealed class Cell {

        abstract fun filter(text: String): Boolean
        abstract fun sort(): Comparable<*>

        @Composable
        abstract fun render(modifier: Modifier)

        class Text(
            val value: String,
            val color: Color = Color.Unspecified,
            val textStyle: TextStyle? = null,
            val fontWeight: FontWeight? = null
        ) : Cell() {

            override fun filter(text: String) =
                text.isEmpty() || value.lowercase().contains(text, true)

            override fun sort() = value

            @Composable
            override fun render(modifier: Modifier) {
                Text(
                    modifier = modifier,
                    text = value,
                    style = textStyle ?: LocalTextStyle.current,
                    fontWeight = fontWeight,
                    color = color
                )
            }
        }

        class Number<T>(
            val value: T?,
            val color: Color = Color.Unspecified,
            val textStyle: TextStyle? = null,
            val fontWeight: FontWeight? = null
        ) : Cell() where T : kotlin.Number, T : Comparable<T> {

            override fun filter(text: String) =
                text.isEmpty() || (value != null && value.toString().contains(text, true))

            override fun sort() = value ?: (0 as T)

            @Composable
            override fun render(modifier: Modifier) {
                Text(
                    modifier = modifier,
                    text = value?.toString() ?: "-",
                    style = textStyle ?: LocalTextStyle.current,
                    fontWeight = fontWeight,
                    color = color
                )
            }
        }

        class Checkmark(
            val checked: Boolean,
            val color: Color? = null
        ) : Cell() {

            override fun filter(text: String) = text.isEmpty() ||
                    (checked && (text.lowercase() == "j" || text.lowercase() == "y") || text.lowercase() == "1") ||
                    (!checked && (text.lowercase() == "n" || text.lowercase() == "0"))

            override fun sort() = if (checked) 1 else 0

            @Composable
            override fun render(modifier: Modifier) {
                if (checked) {
                    Box(
                        modifier = modifier
                    ) {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = color
                                ?: LocalContentColor.current//.copy(alpha = LocalContentAlpha.current)
                        )
                    }
                } else Spacer(modifier)
            }
        }
    }
}

@Composable
fun rememberMyTableState(
    filters: SnapshotStateList<MyTable.Filter> = remember { mutableStateListOf() },
    sorts: SnapshotStateList<MyTable.Sort> = remember { mutableStateListOf() },
    selectedRows: SnapshotStateList<Int> = remember { mutableStateListOf() },
) = MyTable.State(filters, sorts, selectedRows)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> MyTable(
    modifier: Modifier,
    headers: List<MyTable.Header>,
    rows: List<MyTable.Row<T>>,
    keyProvider: (item: T) -> Any,
    filterProvider: (row: MyTable.Row<T>, filters: List<MyTable.Filter>) -> Boolean,
    setup: MyTable.Setup<T>,
    state: MyTable.State,
    onFilterChanged: (filtered: List<MyTable.Row<T>>) -> Unit = {},
    onSortingChanged: (sorts: List<MyTable.Sort>) -> Unit = {}
) {
    val filteredList by remember(rows, state.filters.toList()) {
        derivedStateOf {
            rows.filter { filterProvider(it, state.filters) }
        }
    }

    val sortedList by remember(filteredList.toList(), state.sorts.toList()) {
        derivedStateOf {
            val s = state.sorts.toList()
            if (s.isEmpty()) {
                filteredList
            } else {
                var comparator = s.first().comparator<T>()
                s.drop(1).forEach { sort ->
                    val c = when (sort.type) {
                        MyTable.Sort.Type.Asc -> compareBy { it.cells[sort.columnIndex].sort() }
                        MyTable.Sort.Type.Desc -> compareByDescending<MyTable.Row<T>> { it.cells[sort.columnIndex].sort() }
                    }
                    comparator = comparator.then(c)
                }

                filteredList.sortedWith(comparator)
            }
        }
    }

    LaunchedEffect(filteredList) {
        onFilterChanged(filteredList)
    }
    LaunchedEffect(state.sorts.toList()) {
        onSortingChanged(state.sorts.toList())
    }

    MyScrollableLazyColumn(
        modifier = modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground),
        itemSpacing = 0.dp,
        overlapScrollbar = true
    ) {
        stickyHeader {
            Header(headers, state.filters, state.sorts)
            TableColumnDivider()
        }
        if (sortedList.isEmpty()) {
            item(key = "EMPTY-KEY") {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    text = "Hier herrscht gähnende Leere...",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        sortedList.forEachIndexed { index, item ->
            item(key = keyProvider(item.item)) {
                Row(item, index, headers, state.selectedRows, setup)
            }
            if (index <= sortedList.lastIndex) {
                item(key = "DIVIDER-$index") {
                    TableColumnDivider()
                }
            }
        }
    }
}

@Composable
private fun Header(
    headers: List<MyTable.Header>,
    filters: SnapshotStateList<MyTable.Filter>,
    sorts: SnapshotStateList<MyTable.Sort>
) {
    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(color = Color.DarkGray)
                    .padding(end = 8.dp)
            ) {
                headers.forEachIndexed { index, header ->
                    HeaderCell(index, header, filters, sorts)
                    if (index != headers.size - 1) {
                        TableRowSpacer()
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.HeaderCell(
    index: Int,
    header: MyTable.Header,
    filters: SnapshotStateList<MyTable.Filter>,
    sorts: SnapshotStateList<MyTable.Sort>
) {
    val focusRequester = remember { FocusRequester() }

    val sort = remember(sorts.toList()) {
        derivedStateOf {
            sorts.find { it.columnIndex == index }
        }
    }
    val filter = remember(filters.toList()) {
        derivedStateOf {
            filters.find { it.columnIndex == index }
        }
    }
    val popup = remember { mutableStateOf(false) }

    Row(
        modifier = header.modifier(this).padding(header.cellPadding).height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            HeaderIcon(
                imageVector = Icons.Default.FilterAlt,
                tint = if (filter.value == null) null else MaterialTheme.colorScheme.primary
            ) {
                popup.value = !popup.value
            }
            if (popup.value) {
                Popup(
                    onDismissRequest = {
                        popup.value = false
                    },
                    properties = PopupProperties(focusable = true),
                    offset = IntOffset(0, with(LocalDensity.current) { 16.dp.roundToPx() })
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            //Text("Filter", fontWeight = FontWeight.Bold)
                            MyInput(
                                title = "Filter",
                                modifier = Modifier.focusRequester(focusRequester).width(200.dp),
                                value = filter.value?.filter ?: "",
                                onValueChange = {
                                    if (it.isEmpty()) {
                                        filters.remove(filter.value)
                                    } else {
                                        val f = MyTable.Filter(index, it)
                                        val f2 = filter.value
                                        if (f2 != null) {
                                            filters.remove(f2)
                                        }
                                        filters.add(f)
                                    }
                                }
                            )
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        }
                    }

                }
            }
        }
        Text(
            modifier = Modifier.padding(horizontal = 4.dp).weight(1f),
            text = header.label,
            textAlign = header.textAlign,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        HeaderIcon(
            imageVector = when (sort.value?.type) {
                MyTable.Sort.Type.Asc -> Icons.Default.TrendingUp
                MyTable.Sort.Type.Desc -> Icons.Default.TrendingDown
                null -> Icons.Default.Remove
            },
            tint = when (sort.value?.type) {
                MyTable.Sort.Type.Asc,
                MyTable.Sort.Type.Desc -> MaterialTheme.colorScheme.primary

                null -> null
            }
        ) {
            val s = sort.value
            when (s?.type) {
                MyTable.Sort.Type.Asc -> {
                    sorts.remove(s)
                    sorts.add(MyTable.Sort(index, MyTable.Sort.Type.Desc))
                }

                MyTable.Sort.Type.Desc -> {
                    sorts.remove(s)
                    //sorts.remove(s)
                    //sorts.add(Sort(index, Sort.Type.Desc))
                }

                null -> {
                    sorts.add(MyTable.Sort(index, MyTable.Sort.Type.Asc))
                }
            }
        }
    }
}

@Composable
private fun HeaderIcon(
    imageVector: ImageVector,
    tint: Color? = null,
    onClick: () -> Unit
) {
    Icon(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .padding(2.dp),
        tint = tint ?: LocalContentColor.current,//.copy(alpha = LocalContentAlpha.current),
        imageVector = imageVector,
        contentDescription = null
    )
}

@Composable
private fun <T> Row(
    row: MyTable.Row<T>,
    line: Int,
    headers: List<MyTable.Header>,
    selectedRows: SnapshotStateList<Int>,
    setup: MyTable.Setup<T>
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(
                when (val clickType = setup.clickType) {
                    is MyTable.Setup.ClickType.RowClick -> Modifier.clickable {
                        clickType.onRowClicked(
                            line,
                            row.item
                        )
                    }

                    is MyTable.Setup.ClickType.Select -> Modifier.clickable {
                        if (selectedRows.contains(line)) {
                            selectedRows.remove(line)
                        } else {
                            selectedRows.add(line)
                        }
                    }

                    else -> Modifier
                }
            )
            .then(
                if (selectedRows.contains(line)) {
                    Modifier.background(MaterialTheme.colorScheme.secondary)
                } else Modifier
            )
            .padding(end = 8.dp)
            .padding(end = 16.dp) // für Scrollbar
    ) {
        headers.forEachIndexed { column, header ->
            val modifier = when (val clickType = setup.clickType) {
                is MyTable.Setup.ClickType.CellClick -> Modifier.clickable {
                    clickType.onCellClicked(
                        line,
                        column,
                        row.item
                    )
                }

                is MyTable.Setup.ClickType.None,
                is MyTable.Setup.ClickType.RowClick,
                is MyTable.Setup.ClickType.Select -> Modifier
            }
            row.cells[column].render(
                header.modifier(this).fillMaxHeight().then(modifier).padding(header.cellPadding)
            )
            if (column < headers.lastIndex) {
                TableRowSpacer()
            }
        }
    }
}

@Composable
fun RowScope.TableRowSpacer() {
    VerticalDivider(color = MaterialTheme.colorScheme.onBackground)
}

@Composable
fun RowScope.TableRowSpacerInvisible() {
    Spacer(modifier = Modifier.fillMaxHeight().width(1.dp))
}

@Composable
fun TableColumnDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)
}


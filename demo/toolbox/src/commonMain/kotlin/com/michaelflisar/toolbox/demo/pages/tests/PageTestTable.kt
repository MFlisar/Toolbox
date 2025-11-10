package com.michaelflisar.toolbox.demo.pages.tests

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material.icons.filled.RunningWithErrors
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.composedialogs.core.DispatcherIO
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.table.Table
import com.michaelflisar.toolbox.table.TableDefaults
import com.michaelflisar.toolbox.table.TableHeader
import com.michaelflisar.toolbox.table.TableSearchBar
import com.michaelflisar.toolbox.table.data.ColumnWidth
import com.michaelflisar.toolbox.table.data.TableClickHandler
import com.michaelflisar.toolbox.table.data.rememberTableState
import com.michaelflisar.toolbox.table.definitions.Cell
import com.michaelflisar.toolbox.table.definitions.Column
import com.michaelflisar.toolbox.table.definitions.Filter
import com.michaelflisar.toolbox.table.definitions.Header
import com.michaelflisar.toolbox.table.definitions.rememberTableColumns
import com.michaelflisar.toolbox.table.definitions.rememberTableDefinition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Parcelize
object PageTestTable : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = "Table Test",
        subTitle = null,
        icon = Icons.Default.TableRows.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> = emptyList()

    @Composable
    override fun Screen() {
        Page()
    }

}

@Composable
private fun Page(
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val scope = rememberCoroutineScope()

    var data by remember { mutableStateOf<List<TestData>?>(emptyList()) }
    val loadData = {
        scope.launch(DispatcherIO) {
            // Simulate a delay to mimic data loading
            data = null
            delay(1000)
            data = TestData.createRandom(1000)
        }

    }

    // -----------
    // Setup
    // -----------

    val tableClickHandler = TableClickHandler.RowClick<TestData>(
        onRowClicked = { index, item ->
            appState.showSnackbar("Clicked on item at index $index: ${item.name}")
        }
    )
    val useSlowAutoWidths = false
    val tableDefinition = rememberTableDefinition<TestData>(
        columns = rememberTableColumns(
            listOf<Column<*, TestData>>(
                Column(
                    header = Header.Text("Index", textAlign = TextAlign.Center),
                    width = if (useSlowAutoWidths) ColumnWidth.Auto() else ColumnWidth.Fixed(48.dp),
                    filter = Filter.Number(),
                    cellValue = { data?.indexOf(it)?.plus(1) ?: -1 },
                    createCell = { item, value ->
                        Cell.Number(
                            value = value,
                            verticalCellAlignment = Alignment.CenterVertically,
                            textAlign = TextAlign.Center
                        )
                    }
                ),
                Column(
                    header = Header.Text("ID", textAlign = TextAlign.Center),
                    width = ColumnWidth.Fixed(48.dp),
                    filter = Filter.Number(),
                    cellValue = { it.id },
                    createCell = { item, value ->
                        Cell.Number(
                            value = value,
                            verticalCellAlignment = Alignment.CenterVertically,
                            textAlign = TextAlign.Center
                        )
                    }
                ),
                Column(
                    header = Header.Text("Name"),
                    width = if (useSlowAutoWidths) ColumnWidth.Auto() else ColumnWidth.Weight(
                        1f,
                        minWidth = 192.dp
                    ),
                    filter = Filter.Text(),
                    cellValue = { it.name },
                    createCell = { item, value ->
                        Cell.Text(
                            value = value,
                            verticalCellAlignment = Alignment.CenterVertically
                        )
                    }
                ),
                Column(
                    header = Header.Text("Description"),
                    width = if (useSlowAutoWidths) ColumnWidth.Auto() else ColumnWidth.Weight(
                        1f,
                        minWidth = 192.dp
                    ),
                    filter = Filter.Text(),
                    cellValue = { it.description },
                    createCell = { item, value ->
                        Cell.Text(
                            value = value,
                            verticalCellAlignment = Alignment.CenterVertically
                        )
                    }
                ),
                Column(
                    header = Header.Text("Type"),
                    width = ColumnWidth.Fixed(96.dp),
                    filter = Filter.Enum(TestData.Type.entries),
                    cellValue = { it.type },
                    createCell = { item, value ->
                        Cell.Enum(
                            value = value,
                            verticalCellAlignment = Alignment.CenterVertically
                        )
                    }
                ),
                Column(
                    header = Header.Text("Icon"),
                    width = ColumnWidth.Fixed(48.dp),
                    filter = null,
                    cellValue = { it.icon },
                    createCell = { item, value ->
                        Cell.Custom(
                            value = value,
                            verticalCellAlignment = Alignment.CenterVertically
                        ) { modifier ->
                            Icon(
                                modifier = modifier,
                                imageVector = value,
                                contentDescription = null,
                            )
                        }
                    }
                ),
                Column(
                    header = Header.Icon(
                        icon = { Icon(Icons.Default.Delete, null) },
                    ),
                    width = ColumnWidth.Fixed(48.dp),
                    cellValue = { Icons.Default.Delete },
                    createCell = { item, value ->
                        Cell.Custom(
                            value = value,
                            verticalCellAlignment = Alignment.CenterVertically
                        ) { modifier ->
                            MyIconButton(
                                modifier = modifier,
                                icon = value.toIconComposable(),
                                onClick = { data = data?.minus(item) }
                            )
                        }
                    }
                )
            )
        ),
        keyProvider = { it.id }
    )
    val tableState = data?.let { rememberTableState(it, tableDefinition) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize(),
    ) {
        if (tableState == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text("Loading data...", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (tableState.items.isEmpty()) {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = { loadData() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Load Data")
            }
        } else {
            TableSearchBar(
                state = tableState,
                colors = TableDefaults.searchBarColors(
                    borderColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Table(
                state = tableState,
                clickHandler = tableClickHandler,
                modifier = Modifier.weight(1f),
                showFilterOnHeaderClick = false, // we use a search bar instead
                onFilterChanged = {
                    // --
                },
                onSortingChanged = {
                    // --
                },
                header = {
                    TableHeader(
                        state = tableState,
                        title = { Text("Test Table") },
                        //colors = TableDefaults.headerColors(MaterialTheme.colorScheme.primary)
                    )
                },
                //footer = {
                //    TableFooter(
                //        state = tableState,
                //        colors = TableDefaults.footerColors()
                //    )
                //}
            )

        }
    }

}

class TestData(
    val id: Int,
    val name: String,
    val description: String,
    val type: Type,
    val icon: ImageVector
) {
    companion object {
        fun createRandom(size: Int): List<TestData> {
            return List(size) { index ->
                TestData(
                    id = index,
                    name = "Item $index",
                    description = "This is a description for item $index",
                    type = TestData.Type.entries.toTypedArray().random(),
                    icon = when (index % 10) {
                        0 -> Icons.Default.RunningWithErrors
                        1 -> Icons.Default.RunCircle
                        2 -> Icons.Default.Tab
                        3 -> Icons.Default.TableRows
                        4 -> Icons.Default.Pages
                        5 -> Icons.Default.Stop
                        6 -> Icons.Default.Info
                        7 -> Icons.Default.AccessibilityNew
                        8 -> Icons.Default.Add
                        9 -> Icons.Default.Abc
                        else -> Icons.Default.Error
                    }
                )
            }
        }
    }

    enum class Type {
        Type1, Type2, Type3, Type4, Type5
    }
}
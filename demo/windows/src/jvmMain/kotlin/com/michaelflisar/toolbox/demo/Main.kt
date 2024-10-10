package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialogContent
import com.michaelflisar.toolbox.MaterialColors
import com.michaelflisar.toolbox.Toolbox
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.composables.MyCheckbox
import com.michaelflisar.toolbox.composables.MyDropdown
import com.michaelflisar.toolbox.composables.MyMultiDropdown
import com.michaelflisar.toolbox.composables.MyMultiSegmentedControl
import com.michaelflisar.toolbox.composables.MySegmentedControl
import com.michaelflisar.toolbox.get
import com.michaelflisar.toolbox.table.MyTable
import com.michaelflisar.toolbox.table.rememberMyTableState
import com.michaelflisar.toolbox.windowsapp.DesktopApplication
import com.michaelflisar.toolbox.windowsapp.DesktopDialog
import com.michaelflisar.toolbox.windowsapp.classes.LocalAppState
import com.michaelflisar.toolbox.windowsapp.classes.Status
import com.michaelflisar.toolbox.windowsapp.classes.rememberAppState
import com.michaelflisar.toolbox.windowsapp.initLogging
import com.michaelflisar.toolbox.windowsapp.prefs.DefaultDesktopAppPrefs
import com.michaelflisar.toolbox.windowsapp.ui.DesktopRoot
import com.michaelflisar.toolbox.windowsapp.ui.StatusBar
import com.michaelflisar.toolbox.windowsapp.ui.StatusBarText
import com.michaelflisar.toolbox.windowsapp.ui.dialogs.DesktopInfoDialog
import com.michaelflisar.toolbox.windowsapp.ui.dialogs.DesktopListDialog
import com.michaelflisar.toolbox.windowsapp.ui.dialogs.rememberDesktopInfoDialogData
import com.michaelflisar.toolbox.windowsapp.ui.dialogs.rememberDesktopInfoDialogSimpleData
import com.michaelflisar.toolbox.windowsapp.ui.pane.PaneContainer
import com.michaelflisar.toolbox.windowsapp.ui.pane.rememberPane
import com.michaelflisar.toolbox.windowsapp.ui.tabs.TabContent
import com.michaelflisar.toolbox.windowsapp.ui.tabs.TabItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.pushingpixels.aurora.component.model.Command
import org.pushingpixels.aurora.component.model.CommandGroup
import org.pushingpixels.aurora.component.model.CommandMenuContentModel
import org.pushingpixels.aurora.window.auroraApplication

fun main() {
    auroraApplication {
        val appState = rememberAppState()
        val logSetup = Toolbox.initLogging()
        val showLogs = remember { mutableStateOf(false) }
        val prefs = DefaultDesktopAppPrefs

        DesktopApplication(
            title = "Demo App",
            state = appState,
            menuCommands = { buildMenu(showLogs) },
            prefs = prefs
            //icon = painterResource("logo.png"),
            //alwaysOnTop = alwaysOnTop.value,
            //onClosed = {
            //}
        ) {
            val tabs = listOf(
                TabItem.Group("Region 1"),
                TabItem.Item(1, "UI Examples"),
                TabItem.Item(2, "Dialog"),
                TabItem.Item(3, "Pane"),
                TabItem.Item(4, "SegmentedControl/Dropdowns"),
                TabItem.Item(5, "Table"),
                TabItem.Group("Region 2"),
                TabItem.Item(
                    40,
                    "Tab 2.1",
                    icon = TabItem.Icon(rememberVectorPainter(Icons.Default.Settings))
                )
            )
            val selectedTabId = remember { mutableStateOf(1) }
            DesktopRoot(
                tabItems = tabs,
                selectedTabId = selectedTabId,
                tabWidth = 128.dp,
                tabFooter = {
                    Icon(
                        modifier = Modifier.size(64.dp).padding(8.dp)
                            .align(Alignment.CenterHorizontally),
                        imageVector = Icons.Default.Apps,
                        contentDescription = null
                    )
                },
                statusbar = {
                    StatusBar {
                        StatusBarText("App Version: 1.0")
                    }
                },
                content = { Content(it, selectedTabId) }
            )

            if (showLogs.value) {
                DesktopDialog("Logs", showLogs, padding = PaddingValues()) {
                    LumberjackDialogContent("Logs", logSetup)
                }
                /*LumberjackDialog(
                    visible = showLogs,
                    title = "Logs",
                    setup = logSetup
                )*/
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    selectedTabId: MutableState<Int>
) {
    TabContent(modifier = modifier, selectedTabId = selectedTabId) { tabId ->
        when (tabId) {
            1 -> ContentPage1()
            2 -> ContentPageDialogs()
            3 -> ContentPagePane()
            4 -> ContentPageSegmentsAndDropdowns()
            5 -> ContentPageTable()
            else -> ContentPageX(tabId)
        }
    }
}

@Composable
private fun ContentPage1() {
    val scope = rememberCoroutineScope()
    val appState = LocalAppState.current
    Column(
        modifier = Modifier.padding(ToolboxDefaults.CONTENT_PADDING_SMALL),
        verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
    ) {
        Text(text = "Center")
        Button(
            onClick = {
                L.d { "Test Log" }
            }
        ) {
            Text("Test Log")
        }
        Button(
            onClick = {
                L.e { "Test Error" }
            }
        ) {
            Text("Test Error", color = MaterialColors.Red[200])
        }
        Button(
            enabled = appState.state.value == Status.None,
            onClick = {
                if (appState.state.value == Status.None) {
                    scope.launch(Dispatchers.IO) {
                        L.d { "Work wird gestartet..." }
                        appState.state.value = Status.Running("Doing some work...")
                        delay(5000)
                        appState.state.value = Status.None
                        L.d { "Work erfolgreich erledigt!" }
                    }
                } else {
                    L.e { "Work läuft bereits!!" }
                }
            }
        ) {
            Text("Test Work for 5 seconds...")
        }

        val selectedIndex = remember { mutableStateOf(0) }
        val items = remember { mutableStateOf((1..100).map { "Item $it" }) }
        MyDropdown(
            title = "List",
            items = items.value,
            selected = selectedIndex,
            filter = MyDropdown.Filter("Search") { filter, item ->
                filter.isEmpty() || item.contains(filter, true)
            }
        )

        MyCheckbox(title = "Checkbox1", checked = true, onCheckedChange = {})
        MyCheckbox(title = "Checkbox1 with longer title", checked = true, onCheckedChange = {})

        MyCheckbox(modifier = Modifier.fillMaxWidth(), title = "Checkbox2", checked = true, onCheckedChange = {})
        MyCheckbox(modifier = Modifier.fillMaxWidth(), title = "Checkbox2 with longer title", checked = true, onCheckedChange = {})

        Row {
            Text(modifier = Modifier.weight(1f), text = "Text")
            MyCheckbox(title = "Checkbox3", checked = true, onCheckedChange = {})
        }
        Row {
            Text(modifier = Modifier.weight(1f), text = "Text")
            MyCheckbox(title = "Checkbox3 with longer title", checked = true, onCheckedChange = {})
        }

        LaunchedEffect(Unit) {
            if (items.value.size > 50)
                items.value = items.value.subList(0, 50)
        }
    }
}

@Composable
private fun ContentPagePane() {
    PaneContainer(
        modifier = Modifier.fillMaxSize(),
        left = rememberPane("Left") {
            Text("Left")
        },
        right = rememberPane("Right") {
            Text("Right")
        },
    ) {
        Column(
            modifier = Modifier.padding(ToolboxDefaults.CONTENT_PADDING_SMALL),
            verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
        ) {
            Text("Content")
        }
    }
}

@Composable
private fun ContentPageSegmentsAndDropdowns() {
    Column(
        modifier = Modifier.padding(ToolboxDefaults.CONTENT_PADDING_SMALL),
        verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
    ) {
        val items = listOf("1", "2", "3")
        val selected = remember { mutableStateOf(0) }
        val selectedMulti = remember { mutableStateOf(emptyList<String>()) }

        MySegmentedControl(items = items, selected = selected)
        MyDropdown(title = "Select", items = items, selected = selected)
        MyMultiSegmentedControl(items = items, selected = selectedMulti)
        MyMultiDropdown(title = "Select", items = items, selected = selectedMulti)
    }
}

@Composable
private fun ContentPageDialogs() {
    val showDialog = remember { mutableStateOf(false) }
    val showDialog2 = remember { mutableStateOf(false) }
    val showDialog3 = rememberDesktopInfoDialogData()
    val showDialog4 = rememberDesktopInfoDialogSimpleData()

    Column(
        modifier = Modifier.padding(ToolboxDefaults.CONTENT_PADDING_SMALL),
        verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
    ) {
        Text("Content")
        Button(
            onClick = {
                showDialog.value = true
            }
        ) {
            Text("Show Dialog")
        }
        Button(
            onClick = {
                showDialog2.value = true
            }
        ) {
            Text("Show ListDialog")
        }
        Button(
            onClick = {
                showDialog3.value = DesktopInfoDialog.Data(
                    "Title",
                    "Some info...",
                    icon = { Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error) }
                )
            }
        ) {
            Text("Show InfoDialog")
        }
        Button(
            onClick = {
                showDialog4.value = "Info..."
            }
        ) {
            Text("Show Dialog with Buttons")
        }
    }


    DesktopDialog("Dialog", showDialog) {
        Text("Text")
    }

    DesktopListDialog(
        title = "Liste",
        visible = showDialog2,
        items = listOf("1", "2", "3", "4"),
        onFilter = { item, filter -> item.contains(filter, true) },
        onItemSelected = {
            // ...
        }
    ) {
        Text(it)
    }

    DesktopInfoDialog(
        data = showDialog3
    )

    DesktopInfoDialog(
        title = "Dialog",
        info = showDialog4,
        buttons = DesktopDialog.Buttons.TwoButtons(
            label1 = "Yes",
            label2 = "No",
            on1Clicked = {},
            on2Clicked = {}
        )
    )
}

@Composable
private fun ContentPageTable() {

    val appState = LocalAppState.current
    val scope = rememberCoroutineScope()

    val tableState = rememberMyTableState()
    val filtered = remember { mutableStateOf(-1) }
    val fullTableData = remember {
        listOf(
            TableDataEntry(1, "Michael", "Description of Michael..."),
            TableDataEntry(2, "Christine", "Description of Christine..."),
            TableDataEntry(3, "Benjamin", "Description of Benjamin..."),
            TableDataEntry(4, "Michael", "Description of Michael..."),
            TableDataEntry(5, "Christine", "Description of Christine..."),
            TableDataEntry(6, "Benjamin", "Description of Benjamin..."),
            TableDataEntry(7, "Michael", "Description of Michael..."),
            TableDataEntry(8, "Christine", "Description of Christine..."),
            TableDataEntry(9, "Benjamin", "Description of Benjamin..."),
            TableDataEntry(10, "Michael", "Description of Michael..."),
            TableDataEntry(11, "Christine", "Description of Christine..."),
            TableDataEntry(12, "Benjamin", "Description of Benjamin...")
        )
    }
    val entities = remember { mutableStateOf(fullTableData) }

    // Data...
    val headers = TableDataEntry.HEADERS
    val rows = entities.value.map { it.createRow() }

    // Table
    MyTable(
        modifier = Modifier
            .fillMaxSize()
            .padding(ToolboxDefaults.CONTENT_PADDING_SMALL),
        headers = headers,
        rows = rows,
        state = tableState,
        keyProvider = { it.id },
        filterProvider = { row, filters ->
            var valid = true
            for (filter in filters) {
                valid = row.cells[filter.columnIndex].filter(filter.filter)
                if (!valid) {
                    break
                }
            }
            valid
        },
        setup = MyTable.Setup(
            clickType = MyTable.Setup.ClickType.RowClick(
                onRowClicked = { index, item ->
                    appState.showSnackbar(
                        scope,
                        "Item ${item.id} clicked!",
                        cancelAllPending = true
                    )
                }
            )
        ),
        onFilterChanged = {
            if (entities.value.isNotEmpty()) {
                filtered.value = it.size
            }
        }
    )
}

@Composable
private fun ContentPageX(tabId: Int) {
    Column(
        modifier = Modifier.padding(ToolboxDefaults.CONTENT_PADDING_SMALL)
    ) {
        Text("Tab ID: $tabId")
    }
}

@Composable
private fun buildMenu(
    showLogs: MutableState<Boolean>
): CommandGroup {
    val commandGroup = CommandGroup(
        commands = listOf(
            Command(
                text = "App",
                secondaryContentModel = CommandMenuContentModel(
                    CommandGroup(
                        commands = listOf(
                            Command(
                                text = "New",
                                action = {
                                    L.d { "Menu => New" }
                                }),
                            Command(
                                text = "Open",
                                action = {
                                    L.d { "Menu => Open" }
                                }),
                            Command(
                                text = "Save",
                                action = {
                                    L.d { "Menu => Save" }
                                })
                        )
                    )
                )
            ),
            Command(
                text = "Logs",
                action = {
                    showLogs.value = true
                }
            ),
        )
    )
    return commandGroup
}

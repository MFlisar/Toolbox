package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.michaelflisar.composecolors.material.MaterialColor
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialogContent
import com.michaelflisar.toolbox.Toolbox
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.components.MyChip
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyExpandableOutlinedTitle
import com.michaelflisar.toolbox.components.MyExpandableTitle
import com.michaelflisar.toolbox.components.MyFlowRow
import com.michaelflisar.toolbox.components.MyMultiDropdown
import com.michaelflisar.toolbox.components.MyMultiSegmentedButtonRow
import com.michaelflisar.toolbox.components.MyMultiSegmentedControl
import com.michaelflisar.toolbox.components.MySegmentedButtonRow
import com.michaelflisar.toolbox.components.MySegmentedControl
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.mflisar
import com.michaelflisar.toolbox.table.Setup
import com.michaelflisar.toolbox.table.Table
import com.michaelflisar.toolbox.table.TableTitle
import com.michaelflisar.toolbox.table.definitions.rememberTableDefinition
import com.michaelflisar.toolbox.ui.MyScrollableColumn
import com.michaelflisar.toolbox.windows.DesktopDialog
import com.michaelflisar.toolbox.windows.classes.LocalAppState
import com.michaelflisar.toolbox.windows.hostName
import com.michaelflisar.toolbox.windows.initLumberjack
import com.michaelflisar.toolbox.windows.javaVersion
import com.michaelflisar.toolbox.windows.jewel.JewelContent
import com.michaelflisar.toolbox.windows.jewel.JewelMenuBarItem
import com.michaelflisar.toolbox.windows.jewel.JewelNavigation
import com.michaelflisar.toolbox.windows.jewel.JewelNavigationItem
import com.michaelflisar.toolbox.windows.jewel.JewelNavigationItemSpacer
import com.michaelflisar.toolbox.windows.jewel.JewelStatusBar
import com.michaelflisar.toolbox.windows.jewel.JewelStatusBarItem
import com.michaelflisar.toolbox.windows.jewel.JewelTitleBar
import com.michaelflisar.toolbox.windows.jewel.jewelApplication
import com.michaelflisar.toolbox.windows.prefs.DefaultDesktopAppPrefs
import com.michaelflisar.toolbox.windows.reset
import com.michaelflisar.toolbox.windows.ui.dialogs.DesktopInfoDialog
import com.michaelflisar.toolbox.windows.ui.dialogs.DesktopListDialog
import com.michaelflisar.toolbox.windows.ui.dialogs.rememberDesktopInfoDialogData
import com.michaelflisar.toolbox.windows.ui.dialogs.rememberDesktopInfoDialogSimpleData
import com.michaelflisar.toolbox.windows.ui.pane.DesktopPaneContainer
import com.michaelflisar.toolbox.windows.ui.pane.rememberDesktopPane
import com.michaelflisar.toolbox.windows.userName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

fun main() {

    val fileLoggerSetup = Toolbox.initLumberjack()

    jewelApplication(
        title = "Demo App",
        prefs = DefaultDesktopAppPrefs,
        icon = { painterResource(Res.drawable.mflisar) }
    ) { windowState ->

        val appState = LocalAppState.current
        val scope = rememberCoroutineScope()
        val showLogs = remember { mutableStateOf(false) }

        // Navigation
        val navItems = listOf(
            JewelNavigationItem("UI Examples", Icons.Default.Info) {
                ContentPage1()
            },
            JewelNavigationItem("Dialog", Icons.Default.Window) {
                ContentPageDialogs()
            },
            JewelNavigationItem("Pane", Icons.Default.SwipeLeft) {
                ContentPagePane()
            },
            JewelNavigationItem("SegmentedControl/Dropdowns", Icons.Default.ArrowDropDown) {
                ContentPageSegmentsAndDropdowns()
            },
            JewelNavigationItem("Table", Icons.Default.TableView) {
                ContentPageTable()
            },
            JewelNavigationItemSpacer(),
            //JewelNavigationItemDivider,
            JewelNavigationItem("Settings", Icons.Default.Settings) {
                MyColumn(
                    Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
                ) {
                    Text("Settings Page")
                }
            }
        )

        // MenuBar
        val menuBarItem = listOf(
            JewelMenuBarItem.Group(
                "App",
                items = listOf(
                    JewelMenuBarItem.Item("Reset Window Size", Icons.Default.Clear) {
                        scope.launch(Dispatchers.IO) {
                            windowState.reset(DefaultDesktopAppPrefs, position = false)
                        }
                    }
                )),
            JewelMenuBarItem.Item("Log") {
                showLogs.value = true
            }
        )

        // Statusbar
        val statusBarLeft = listOf(
            JewelStatusBarItem("App Version: 1.0") {
                appState.showSnackbar("App clicked!")
            }
        )
        val statusBarRight = listOf(
            JewelStatusBarItem(Toolbox.javaVersion()),
            JewelStatusBarItem(Toolbox.userName()),
            JewelStatusBarItem(Toolbox.hostName())
        )

        // Content
        JewelContent(
            icon = painterResource(Res.drawable.mflisar),
            menuBarItems = menuBarItem,
            menuBarSetup = JewelTitleBar.Setup(),
            selectedNavigationItem = 0,
            navigationItems = navItems,
            navigationSetup = JewelNavigation.Setup(),
            statusbar = {
                JewelStatusBar(
                    left = statusBarLeft,
                    right = statusBarRight
                )
            }
        )

        // Dialoge
        if (showLogs.value) {
            DesktopDialog("Logs", showLogs, padding = PaddingValues()) {
                LumberjackDialogContent("Logs", fileLoggerSetup)
            }
            /*LumberjackDialog(
                visible = showLogs,
                title = "Logs",
                setup = logSetup
            )*/
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContentPage1() {
    val scope = rememberCoroutineScope()
    val appState = LocalAppState.current
    MyScrollableColumn(
        modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
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
            Text("Test Error", color = MaterialColor.Red200)
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

        MyCheckbox(
            modifier = Modifier.fillMaxWidth(),
            title = "Checkbox2",
            checked = true,
            onCheckedChange = {})
        MyCheckbox(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text("Checkbox2 with longer title")
                Text("Some info", style = MaterialTheme.typography.bodySmall)
            },
            checked = true,
            onCheckedChange = {})

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

        MyExpandableTitle("Expandable") {
            Text("Content...")
        }
        MyExpandableTitle("Expandable2", info = { Text("State") }) {
            Text("Content...")
        }

        MyExpandableOutlinedTitle("Expandable3 - Outlined") {
            Text("Content...")
        }

        MyFlowRow {
            (1..20).forEach {
                MyChip(title = "Chip $it", icon = if (it <= 10) null else {
                    { Icon(Icons.Default.Info, null) }
                })
            }
        }
    }
}

@Composable
private fun ContentPagePane() {
    DesktopPaneContainer(
        modifier = Modifier.fillMaxSize(),
        left = rememberDesktopPane("Left") {
            Text("Left")
        },
        right = rememberDesktopPane("Right") {
            Text("Right")
        },
    ) {
        MyColumn(
            modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
        ) {
            Text("Content")
        }
    }
}

@Composable
private fun ContentPageSegmentsAndDropdowns() {
    MyColumn(
        modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
    ) {
        val items = listOf("1", "2", "3")
        val selected = remember { mutableStateOf(0) }
        val selectedMulti = remember { mutableStateOf(emptyList<String>()) }

        MySegmentedControl(items = items, selected = selected)
        MyDropdown(title = "Select", items = items, selected = selected)
        MySegmentedButtonRow(items = items, selected = selected)

        MyMultiSegmentedControl(items = items, selected = selectedMulti)
        MyMultiDropdown(title = "Select", items = items, selected = selectedMulti)
        MyMultiSegmentedButtonRow(items = items, selected = selectedMulti)


    }
}

@Composable
private fun ContentPageDialogs() {
    val showDialog = remember { mutableStateOf(false) }
    val showDialog2 = remember { mutableStateOf(false) }
    val showDialog3 = rememberDesktopInfoDialogData()
    val showDialog4 = rememberDesktopInfoDialogSimpleData()

    MyColumn(
        modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
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
                    "Important",
                    "Some info...",
                    type = DesktopInfoDialog.Data.Type.Error
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
        items = List(20) { it.toString() },
        onFilter = { item, filter -> item.contains(filter, true) },
        onItemSelected = {
            // ...
        },
        footer = {
            Text(modifier = Modifier.fillMaxWidth(), text = "FOOTER", textAlign = TextAlign.Center)
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

    val tableDefinition = rememberTableDefinition(
        columns = TableDataEntry.columns(),
        keyProvider = { it.id }
    )
    val filtered = remember { mutableStateOf(-1) }
    val fullTableData = remember {
        listOf(
            TableDataEntry(
                1,
                true,
                "Michael",
                TableDataEntry.ColorEnum.Red,
                "Description of Michael..."
            ),
            TableDataEntry(
                2,
                false,
                "Christine",
                TableDataEntry.ColorEnum.Blue,
                "Description of Christine..."
            ),
            TableDataEntry(
                3,
                true,
                "Benjamin",
                TableDataEntry.ColorEnum.Green,
                "Description of Benjamin..."
            ),
            TableDataEntry(
                4,
                false,
                "Michael",
                TableDataEntry.ColorEnum.Red,
                "Description of Michael..."
            ),
            TableDataEntry(
                5,
                true,
                "Christine",
                TableDataEntry.ColorEnum.Blue,
                "Description of Christine..."
            ),
            TableDataEntry(
                6,
                false,
                "Benjamin",
                TableDataEntry.ColorEnum.Green,
                "Description of Benjamin..."
            ),
            TableDataEntry(
                7,
                true,
                "Michael",
                TableDataEntry.ColorEnum.Red,
                "Description of Michael..."
            ),
            TableDataEntry(
                8,
                false,
                "Christine",
                TableDataEntry.ColorEnum.Blue,
                "Description of Christine..."
            ),
            TableDataEntry(
                9,
                true,
                "Benjamin",
                TableDataEntry.ColorEnum.Green,
                "Description of Benjamin..."
            ),
            TableDataEntry(
                10,
                false,
                "Michael",
                TableDataEntry.ColorEnum.Red,
                "Description of Michael..."
            ),
            TableDataEntry(
                11,
                true,
                "Christine",
                TableDataEntry.ColorEnum.Blue,
                "Description of Christine..."
            ),
            TableDataEntry(
                12,
                false,
                "Benjamin",
                TableDataEntry.ColorEnum.Green,
                "Description of Benjamin..."
            )
        )
    }
    val entities = remember { mutableStateOf(fullTableData) }

    // Table
    MyColumn(
        Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
    ) {
        TableTitle(
            definition = tableDefinition,
            items = entities.value,
            modifier = Modifier.fillMaxWidth(),
            filtered = filtered
        )
        Table(
            modifier = Modifier.weight(1f),
            definition = tableDefinition,
            items = entities.value,
            setup = Setup(
                clickType = Setup.ClickType.RowClick(
                    onRowClicked = { index, item ->
                        appState.showSnackbar("Item ${item.id} clicked!")
                    }
                ),
                emptyText = "Table is empty or filter is filtering all rows..."
            ),
            onFilterChanged = {
                if (entities.value.isNotEmpty()) {
                    filtered.value = it.size
                }
            }
        )
    }
}

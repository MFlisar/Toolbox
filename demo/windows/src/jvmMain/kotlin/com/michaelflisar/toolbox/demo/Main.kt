package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.composecolors.material.MaterialColor
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialogContent
import com.michaelflisar.toolbox.Toolbox
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyCheckChip
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.components.MyChip
import com.michaelflisar.toolbox.components.MyChipsMultiRow
import com.michaelflisar.toolbox.components.MyChipsSingleRow
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyExpandableOutlinedTitle
import com.michaelflisar.toolbox.components.MyExpandableTitle
import com.michaelflisar.toolbox.components.MyFilledTonalButton
import com.michaelflisar.toolbox.components.MyFlowRow
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyIconFilledButton
import com.michaelflisar.toolbox.components.MyIconOutlinedButton
import com.michaelflisar.toolbox.components.MyMultiDropdown
import com.michaelflisar.toolbox.components.MyMultiSegmentedButtonRow
import com.michaelflisar.toolbox.components.MyOutlinedButton
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.components.MySegmentedButtonRow
import com.michaelflisar.toolbox.components.MySegmentedControl
import com.michaelflisar.toolbox.components.MyTextButton
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.mflisar
import com.michaelflisar.toolbox.form.FormDialog
import com.michaelflisar.toolbox.form.rememberFormFieldCustom
import com.michaelflisar.toolbox.form.rememberFormFields
import com.michaelflisar.toolbox.isDark
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
import com.michaelflisar.toolbox.windows.jewel.JewelNavigationRegion
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
            JewelNavigationRegion("Region 1"),
            JewelNavigationItem("UI Examples", Icons.Default.Info) {
                ContentPage1()
            },
            JewelNavigationItem("Dialog", Icons.Default.Window) {
                ContentPageDialogs()
            },
            JewelNavigationRegion("Region 2"),
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
                title = "App",
                items = listOf(
                    JewelMenuBarItem.Item("Reset Window Size", Icons.Default.Clear) {
                        scope.launch(Dispatchers.IO) {
                            windowState.reset(DefaultDesktopAppPrefs, position = false)
                        }
                    }
                )
            ),
            JewelMenuBarItem.Item("Log") {
                showLogs.value = true
            }
        )

        // Statusbar
        val statusBarLeft = listOf(
            JewelStatusBarItem.Custom {
                Image(
                    painter = painterResource(Res.drawable.mflisar),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(4.dp)
                )
            },
            JewelStatusBarItem.Text("App Version: 1.0") {
                appState.showSnackbar("App clicked!")
            }
        )
        val statusBarRight = listOf(
            JewelStatusBarItem.Text(Toolbox.javaVersion()),
            JewelStatusBarItem.Text(Toolbox.userName()),
            JewelStatusBarItem.Text(Toolbox.hostName())
        )

        // Content
        JewelContent(
            icon = painterResource(Res.drawable.mflisar),
            menuBarItems = menuBarItem,
            menuBarSetup = JewelTitleBar.Setup(),
            selectedNavigationItem = remember { mutableStateOf(1) },
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

    val selectedIndex = remember { mutableStateOf(0) }
    val items = remember { mutableStateOf((1..100).map { "Item $it" }) }

    MyScrollableColumn(
        modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
    ) {
        // 1) Buttons
        MyTitle("Buttons") {
            MyRow {
                MyButton(
                    text = "Test Log",
                    onClick = {
                        L.d { "Test Log" }
                    }
                )
                MyButton(
                    text = "Test Error",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialColor.Red200,
                        contentColor = MaterialColor.White
                    ),
                    onClick = {
                        L.e { "Test Error" }
                    }
                )
            }
        }

        // 2) Dropdowns
        MyTitle("Dropdowns") {
            MyDropdown(
                title = "List",
                items = items.value,
                selected = selectedIndex,
                filter = MyDropdown.Filter("Search") { filter, item ->
                    filter.isEmpty() || item.contains(filter, true)
                }
            )
        }

        // 3) Checkboxes and layout tests for checkboxes
        MyTitle("Checkboxes") {
            MyColumn {
                MyCheckbox(title = "Checkbox1", checked = true, onCheckedChange = {})
                MyCheckbox(
                    title = "Checkbox1 with longer title",
                    checked = true,
                    onCheckedChange = {})
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
                    onCheckedChange = {}
                )
                Row {
                    Text(modifier = Modifier.weight(1f), text = "Text")
                    MyCheckbox(title = "Checkbox3", checked = true, onCheckedChange = {})
                }
                Row {
                    Text(modifier = Modifier.weight(1f), text = "Text")
                    MyCheckbox(
                        title = "Checkbox3 with longer title",
                        checked = true,
                        onCheckedChange = {})
                }
            }
        }

        // 4) Chips
        MyTitle("Chips") {
            MyColumn {
                MyFlowRow {
                    repeat(20) {
                        MyChip(
                            title = "Chip $it",
                            icon = if (it <= 10) null else {
                                { Icon(Icons.Default.Info, null) }
                            },
                            onClick = if (it <= 15) null else {
                                { /* clicked */ }
                            }
                        )
                    }
                }
                MyRow {
                    val checked = remember { mutableStateOf(0) }
                    repeat(5) { value ->
                        MyCheckChip(
                            title = "Checked Chip ${value + 1}",
                            checked = checked.value == value
                        ) {
                            checked.value = value
                        }
                    }
                }
            }
        }

        // 5) Buttons
        MyTitle("Buttons") {
            MyFlowRow {
                MyButton("Button") {}
                MyOutlinedButton("Outlined Button") {}
                MyTextButton("Text Button") {}
                MyFilledTonalButton("Filled Tonal Button") {}
                MyButton("Button + Icon", icon = Icons.Default.Info) {}
                MyOutlinedButton("Outlined Button + Icon", icon = Icons.Default.Info) {}
                MyTextButton("Text Button + Icon", icon = Icons.Default.Info) {}
                MyFilledTonalButton("Filled Tonal Button", icon = Icons.Default.Info) {}
            }
        }

        // 6) Icon Buttons
        MyTitle("Icon Buttons") {
            MyFlowRow {
                MyIconButton(Icons.Default.Info) {}
                MyIconOutlinedButton(Icons.Default.Info) {}
                MyIconFilledButton(Icons.Default.Info) {}
            }
        }

        // testing list item updates
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

        MySegmentedControl(items = items, selectedIndex = selected)
        MyChipsSingleRow(items = items, selectedIndex = selected)
        MyDropdown(title = "Select", items = items, selected = selected)
        MySegmentedButtonRow(items = items, selected = selected)

        MyChipsMultiRow(items = items, selected = selectedMulti)
        MyMultiDropdown(title = "Select", items = items, selected = selectedMulti)
        MyMultiSegmentedButtonRow(items = items, selected = selectedMulti)

        MyTitle("Chip Row")
        MyRow(itemSpacing = 4.dp) {
            items.forEach {
                MyChip(title = it, modifier = Modifier.widthIn(min = 48.dp), onClick = {})
            }
        }
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

    val showEditDialog = rememberDialogState<Int>(null)
    val tableDefinition = rememberTableDefinition(
        columns = TableDataEntry.columns(),
        keyProvider = { it.id }
    )
    val filtered = remember { mutableStateOf(-1) }
    val fullTableData = remember { TableDataEntry.defaultData().toMutableList() }
    val entities = remember { mutableStateOf(fullTableData) }

    // Table
    MyColumn(
        Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onPrimary
        ) {
            TableTitle(
                definition = tableDefinition,
                items = entities.value,
                modifier = Modifier.fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
                ,
                filtered = filtered
            )
        }
        Table(
            modifier = Modifier.weight(1f),
            definition = tableDefinition,
            items = entities.value,
            setup = Setup(
                clickType = Setup.ClickType.RowClick(
                    onRowClicked = { index, item ->
                        showEditDialog.show(item.id)
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

    // Edit Dialog
    if (showEditDialog.visible) {

        val itemId = showEditDialog.requireData()
        val item = entities.value.find { it.id == itemId }!!
        val itemIndex = entities.value.indexOfFirst { it.id == itemId }

        // Felder
        val fieldID = TableDataEntry.fieldID(item)
        val fieldChecked = TableDataEntry.fieldChecked(item)
        val fieldName = TableDataEntry.fieldName(item)
        val fieldAge = TableDataEntry.fieldAge(item)
        val fieldColor = TableDataEntry.fieldColor(item)
        val fieldDescription = TableDataEntry.fieldDescription(item)

        val selectedColor = remember { derivedStateOf { fieldColor.value.value.let { TableDataEntry.ColorEnum.entries[it] } } }
        val fieldCustom = rememberFormFieldCustom("Custom Field", selectedColor.value) {
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(it.color),
                contentAlignment = Alignment.Center
            ) {
                Text("Color: ${it.name}", color = if (it.color.isDark()) Color.White else Color.Black)
            }
        }
        val fields = rememberFormFields(
            listOf(
                fieldID,
                fieldChecked,
                fieldName,
                fieldAge,
                fieldColor,
                fieldDescription,
                fieldCustom
            )
        )

        FormDialog(
            state = showEditDialog,
            name = "TableDataEntry",
            fields = fields,
            labelWidth = 120.dp,
            onSave = {
                // save
                val newItem = TableDataEntry(
                    item.id,
                    fieldChecked.value.value,
                    fieldName.value.value,
                    fieldAge.value.value,
                    fieldColor.value.value.let { TableDataEntry.ColorEnum.entries[it] },
                    fieldDescription.value.value
                )
                println("Save: $newItem")
                entities.value.set(itemIndex, newItem)
            }, onDelete = {
                // delete
                entities.value.removeAt(itemIndex)
            }
        )
    }
}

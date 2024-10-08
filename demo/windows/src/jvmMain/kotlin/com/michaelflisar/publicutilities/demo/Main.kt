package com.michaelflisar.publicutilities.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialog
import com.michaelflisar.publicutilities.windowsapp.DesktopApp
import com.michaelflisar.publicutilities.windowsapp.DesktopApplication
import com.michaelflisar.publicutilities.windowsapp.DesktopMainScreen
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme
import com.michaelflisar.publicutilities.windowsapp.classes.LocalAppState
import com.michaelflisar.publicutilities.windowsapp.classes.Status
import com.michaelflisar.publicutilities.windowsapp.classes.rememberAppState
import com.michaelflisar.publicutilities.windowsapp.ui.StatusBar
import com.michaelflisar.publicutilities.windowsapp.ui.StatusBarText
import com.michaelflisar.publicutilities.windowsapp.ui.pane.PaneContainer
import com.michaelflisar.publicutilities.windowsapp.ui.pane.rememberPane
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.TabContent
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.TabItem
import com.michaelflisar.publicutilities.windowsapp.ui.todo.MyDropdown
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
        val logSetup = DesktopApp.initLogging()
        val showLogs = remember { mutableStateOf(false) }

        DesktopApplication(
            title = "Demo App",
            state = appState,
            menuCommands = { buildMenu(showLogs) }
            //icon = painterResource("logo.png"),
            //alwaysOnTop = alwaysOnTop.value,
            //onClosed = {
            //}
        ) {
            val tabs = listOf(
                TabItem.Group("Region 1"),
                TabItem.Item(1, "Tab 1.1"),
                TabItem.Item(2, "Tab 1.2"),
                TabItem.Item(3, "Tab 1.3"),
                TabItem.Group("Region 2"),
                TabItem.Item(
                    4,
                    "Tab 2.1",
                    icon = TabItem.Icon(rememberVectorPainter(Icons.Default.Settings))
                )
            )
            val selectedTab = remember { mutableStateOf(tabs[1] as TabItem.Item) }
            DesktopMainScreen(
                tabItems = tabs,
                selectedTab = selectedTab,
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
                content = { Content(it, selectedTab) }
            )

            if (showLogs.value) {
                LumberjackDialog(
                    visible = showLogs,
                    title = "Logs",
                    setup = logSetup
                )
            }
        }
    }
}

@Composable
private fun Content(modifier: Modifier, selectedTab: MutableState<TabItem.Item>) {
    TabContent(modifier = modifier, selectedTab = selectedTab) { tab ->
        when (tab.id) {
            1 -> ContentPage1()
            2 -> ContentPage2()
            else -> ContentPageX(tab.id)
        }
    }
}

@Composable
private fun ContentPage1() {
    val scope = rememberCoroutineScope()
    val appState = LocalAppState.current
    Column(
        modifier = Modifier.padding(AppTheme.CONTENT_PADDING_SMALL)
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
            Text("Test Error")
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

        LaunchedEffect(Unit) {
            if (items.value.size > 50)
                items.value = items.value.subList(0, 50)
        }
    }
}

@Composable
private fun ContentPage2() {
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
            modifier = Modifier.padding(AppTheme.CONTENT_PADDING_SMALL)
        ) {
            Text("Content")
        }
    }
}

@Composable
private fun ContentPageX(tab: Int) {
    Column(
        modifier = Modifier.padding(AppTheme.CONTENT_PADDING_SMALL)
    ) {
        Text("Tab Index: $tab")
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

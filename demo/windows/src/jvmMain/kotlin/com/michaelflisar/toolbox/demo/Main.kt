package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.DefaultThemes
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialogContent
import com.michaelflisar.toolbox.Toolbox
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.demo.pages.ContentPage1
import com.michaelflisar.toolbox.demo.pages.ContentPage2
import com.michaelflisar.toolbox.demo.pages.ContentPageDialogs
import com.michaelflisar.toolbox.demo.pages.ContentPagePane
import com.michaelflisar.toolbox.demo.pages.ContentPageSegmentsAndDropdowns
import com.michaelflisar.toolbox.demo.pages.ContentPageTable
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.mflisar
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
import com.michaelflisar.toolbox.windows.userName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

fun main() {

    val fileLoggerSetup = Toolbox.initLumberjack()

    jewelApplication(
        title = { "Demo App" },
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
            JewelNavigationItem("UI Examples 2", Icons.Default.Info) {
                ContentPage2()
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


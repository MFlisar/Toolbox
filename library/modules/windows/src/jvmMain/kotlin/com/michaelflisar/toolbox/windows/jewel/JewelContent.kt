package com.michaelflisar.toolbox.windows.jewel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.toolbox.windows.classes.LocalAppState
import org.jetbrains.jewel.window.DecoratedWindowScope

@Composable
fun DecoratedWindowScope.JewelContent(
    icon: Painter? = null,
    menuBarItems: List<JewelMenuBarItem> = emptyList(),
    menuBarSetup: JewelTitleBar.Setup = JewelTitleBar.Setup(),
    selectedNavigationItem: Int,
    navigationItems: List<IJewelNavigationItem>,
    navigationSetup: JewelNavigation.Setup = JewelNavigation.Setup(),
    statusbar: @Composable () -> Unit = {}
) {
    val selectedNavigationItem = remember { mutableStateOf(selectedNavigationItem) }
    JewelContent(
        titleBar = {
            JewelTitleBar(
                icon = icon,
                menuBarItems = menuBarItems,
                menuBarSetup = menuBarSetup
            )
        },
        navigation = {
            JewelNavigation(
                items = navigationItems,
                selected = selectedNavigationItem,
                setup = navigationSetup
            )
        },
        statusbar = statusbar
    ) {
        JewelNavigationContent(
            items = navigationItems,
            selected = selectedNavigationItem
        )
    }
}

@Composable
fun DecoratedWindowScope.JewelContent(
    titleBar: @Composable DecoratedWindowScope.() -> Unit,
    navigation: @Composable () -> Unit = {},
    statusbar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val appState = LocalAppState.current
    titleBar()
    Column {
        Scaffold(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            snackbarHost = { SnackbarHost(appState.snackbarHostState) }
        ) {
            Row(modifier = Modifier.padding(it).fillMaxSize()) {
                navigation()
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    content()
                }
            }
        }
        statusbar()
    }

}
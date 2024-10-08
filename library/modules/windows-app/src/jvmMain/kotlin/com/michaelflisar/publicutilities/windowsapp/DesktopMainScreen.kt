package com.michaelflisar.publicutilities.windowsapp

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.publicutilities.windowsapp.classes.LocalAppState
import com.michaelflisar.publicutilities.windowsapp.ui.internal.StatusBar
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.TabItem
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabIconItem
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabItem
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabStyle
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabs
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabsRegion
import com.michaelflisar.publicutilities.windowsapp.ui.todo.MyVerticalDivider

// ---------------------
// Main Layout
// ---------------------

@Composable
@Preview
fun DesktopMainScreen(
    tabItems: List<TabItem>? = null,
    statusbar: @Composable () -> Unit = { StatusBar() },
    tabWidth: Dp = 64.dp,
    tabStyle: VerticalTabStyle = VerticalTabStyle.Highlight(
        side = VerticalTabStyle.Side.Left,
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ),
    selectedTab: MutableState<Int> = remember { mutableStateOf(0) },
    tabFooter: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable (modifier: Modifier) -> Unit,
) {
    val appState = LocalAppState.current
    Column {
        Scaffold(
            modifier = Modifier.weight(1f),
            snackbarHost = { SnackbarHost(appState.snackbarHostState) },
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                if (tabItems != null) {
                    VerticalTabs(
                        verticalTabStyle = tabStyle,
                        modifier = Modifier
                            .width(tabWidth)
                            .background(MaterialTheme.colorScheme.onBackground)
                    ) {
                        for ((index, item) in tabItems.withIndex()) {
                            when (item) {
                                is TabItem.Group -> VerticalTabsRegion(item.label)
                                is TabItem.Item -> {
                                    if (item.icon == null)
                                        VerticalTabItem(item.label, index, selectedTab)
                                    else
                                        VerticalTabIconItem(
                                            item.label,
                                            item.icon.painter,
                                            item.icon.isIcon,
                                            index,
                                            selectedTab
                                        )
                                }
                            }
                        }
                        if (tabFooter != null) {
                            Spacer(modifier = Modifier.weight(1f))
                            tabFooter()
                        }
                    }
                    MyVerticalDivider(color = MaterialTheme.colorScheme.onBackground)
                }
                content(Modifier.weight(1f))
            }
        }
        statusbar()
    }
}


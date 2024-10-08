package com.michaelflisar.publicutilities.windowsapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.michaelflisar.publicutilities.windowsapp.ui.StatusBar
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.TabItem
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabIconItem
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabItem
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabStyle
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabs
import com.michaelflisar.publicutilities.windowsapp.ui.tabs.VerticalTabsRegion
import com.michaelflisar.publicutilities.windowsapp.ui.todo.MyVerticalDivider

@Composable
fun DesktopMainScreen(
    tabItems: List<TabItem>? = null,
    selectedTab: MutableState<TabItem.Item>? = if (tabItems == null) null else remember {
        mutableStateOf(
            tabItems.first { it is TabItem.Item } as TabItem.Item)
    },
    statusbar: @Composable () -> Unit = { StatusBar() },
    tabWidth: Dp = 64.dp,
    tabStyle: VerticalTabStyle = VerticalTabStyle.Highlight(
        side = VerticalTabStyle.Side.Left,
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ),
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
                if (tabItems != null && selectedTab != null) {
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
                                        VerticalTabItem(item, selectedTab)
                                    else
                                        VerticalTabIconItem(
                                            item,
                                            item.icon.painter,
                                            item.icon.isIcon,
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
                content(Modifier.fillMaxHeight().weight(1f))
            }
        }
        statusbar()
    }
}


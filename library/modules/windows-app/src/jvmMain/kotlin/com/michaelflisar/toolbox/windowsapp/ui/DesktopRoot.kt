package com.michaelflisar.toolbox.windowsapp.ui

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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.windowsapp.classes.LocalAppState
import com.michaelflisar.toolbox.windowsapp.ui.tabs.TabItem
import com.michaelflisar.toolbox.windowsapp.ui.tabs.VerticalTabIconItem
import com.michaelflisar.toolbox.windowsapp.ui.tabs.VerticalTabItem
import com.michaelflisar.toolbox.windowsapp.ui.tabs.VerticalTabStyle
import com.michaelflisar.toolbox.windowsapp.ui.tabs.VerticalTabs
import com.michaelflisar.toolbox.windowsapp.ui.tabs.VerticalTabsRegion

@Composable
fun DesktopRoot(
    statusbar: @Composable () -> Unit = { StatusBar() },
    tabWidth: Dp = 64.dp,
    tabStyle: VerticalTabStyle = VerticalTabStyle.Highlight(
        side = VerticalTabStyle.Side.Left,
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ),
    tabFooter: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable (modifier: Modifier) -> Unit
) {
    DesktopRootImpl(
        null,
        null,
        null,
        statusbar,
        tabWidth,
        tabStyle,
        tabFooter,
        content
    )
}

@Composable
fun DesktopRoot(
    tabItems: List<TabItem>,
    selectedTabId: MutableState<Int> = remember {
        mutableStateOf(tabItems.filterIsInstance<TabItem.Item>().firstOrNull()?.id ?: -1)
    },
    statusbar: @Composable () -> Unit = { StatusBar() },
    tabWidth: Dp = 64.dp,
    tabStyle: VerticalTabStyle = VerticalTabStyle.Highlight(
        side = VerticalTabStyle.Side.Left,
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ),
    tabFooter: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable (modifier: Modifier) -> Unit
) {
    DesktopRootImpl(
        tabItems,
        selectedTabId.value,
        selectedTabId.let { { selectedTabId.value = it } },
        statusbar,
        tabWidth,
        tabStyle,
        tabFooter,
        content
    )
}

@Composable
fun DesktopRoot(
    tabItems: List<TabItem>,
    selectedTabId: Int? = tabItems.filterIsInstance<TabItem.Item>().firstOrNull()?.id ?: -1,
    onSelectedTabChanged: ((id: Int) -> Unit),
    statusbar: @Composable () -> Unit = { StatusBar() },
    tabWidth: Dp = 64.dp,
    tabStyle: VerticalTabStyle = VerticalTabStyle.Highlight(
        side = VerticalTabStyle.Side.Left,
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ),
    tabFooter: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable (modifier: Modifier) -> Unit
) {
    DesktopRootImpl(
        tabItems,
        selectedTabId,
        onSelectedTabChanged,
        statusbar,
        tabWidth,
        tabStyle,
        tabFooter,
        content
    )
}

@Composable
private fun DesktopRootImpl(
    tabItems: List<TabItem>?,
    selectedTabId: Int?,
    onSelectedTabChanged: ((id: Int) -> Unit)?,
    statusbar: @Composable () -> Unit,
    tabWidth: Dp,
    tabStyle: VerticalTabStyle,
    tabFooter: @Composable (ColumnScope.() -> Unit)?,
    content: @Composable (modifier: Modifier) -> Unit
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
                if (tabItems != null && selectedTabId != null && onSelectedTabChanged != null) {
                    VerticalTabs(
                        verticalTabStyle = tabStyle,
                        modifier = Modifier
                            .width(tabWidth)
                            .background(MaterialTheme.colorScheme.onBackground)
                    ) {
                        for (item in tabItems) {
                            when (item) {
                                is TabItem.Group -> VerticalTabsRegion(item.label)
                                is TabItem.Item -> {
                                    if (item.icon == null)
                                        VerticalTabItem(item, selectedTabId, onSelectedTabChanged)
                                    else
                                        VerticalTabIconItem(
                                            item,
                                            item.icon.painter,
                                            item.icon.isIcon,
                                            selectedTabId,
                                            onSelectedTabChanged
                                        )
                                }
                            }
                        }
                        if (tabFooter != null) {
                            Spacer(modifier = Modifier.weight(1f))
                            tabFooter()
                        }
                    }
                    VerticalDivider(color = MaterialTheme.colorScheme.onBackground)
                }
                content(Modifier.fillMaxHeight().weight(1f))
            }
        }
        statusbar()
    }
}


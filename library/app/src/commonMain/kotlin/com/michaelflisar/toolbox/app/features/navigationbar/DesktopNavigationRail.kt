package com.michaelflisar.toolbox.app.features.navigationbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer

@Composable
expect fun JewelNavigation(
    modifier: Modifier = Modifier,
    items: List<INavItem>,
    selected: (screen: Screen) -> Boolean,
    expanded: MutableState<Boolean>,
    showExpand: Boolean,
)

@Composable
fun DesktopNavigationRail(
    itemsTop: List<INavItem>,
    itemsBottom: List<INavItem>,
    showForSingleItem: Boolean,
    expandable: Boolean,
    modifier: Modifier = Modifier,
    navigationExpanded: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    if (itemsTop.size + itemsBottom.size <= 1 && !showForSingleItem) {
        return // no need to show the navigation rail if there is only one item
    }
    val navigator = LocalNavigator.currentOrThrow
    val items: List<INavItem> =
        itemsTop + (itemsBottom.takeIf { it.isNotEmpty() }?.let { listOf(NavItemSpacer()) + it }
            ?: emptyList())

    JewelNavigation(
        modifier = modifier,
        items = items,
        selected = { it == navigator.lastItem },
        expanded = navigationExpanded,
        showExpand = expandable
    )
}
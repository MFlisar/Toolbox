package com.michaelflisar.toolbox.app.features.navigationbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.jewel.JewelNavigation
import com.michaelflisar.toolbox.app.jewel.toJewelNavigationItems

@Composable
actual fun JewelNavigation(
    modifier: Modifier,
    items: List<INavItem>,
    selected: (screen: Screen) -> Boolean,
    expanded: MutableState<Boolean>,
    showExpand: Boolean,
) {
    JewelNavigation(
        modifier = modifier,
        items = items.toJewelNavigationItems(),
        selected = selected,
        expanded = expanded,
        setup = JewelNavigation.Setup(
            showExpand = showExpand
        )
    )
}
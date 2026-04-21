package com.michaelflisar.toolbox.app.features.navigationbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.app.features.navigation.INavItem

@Composable
fun MobileNavigationRail(
    items: List<INavItem>,
    modifier: Modifier = Modifier,
    alwaysShowLabel: Boolean = true,
    showForSingleItem: Boolean = false,
) {
    NavigationRail(
        modifier = modifier,
        items = items,
        alwaysShowLabel = alwaysShowLabel,
        showForSingleItem = showForSingleItem
    )
}
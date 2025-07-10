package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen

sealed interface INavItem

class NavItem(
    val title: String,
    val icon: @Composable (() -> Unit)?,
    val screen: Screen
) : INavItem {
    constructor(
        title: String,
        imageVector: ImageVector?,
        screen: Screen
    ) : this(title, icon = imageVector?.let { @Composable { Icon(it, null) } }, screen)
}

class NavItemAction(
    val title: String,
    val icon: @Composable (() -> Unit)?,
    val action: () -> Unit
) : INavItem {
    constructor(
        title: String,
        imageVector: ImageVector?,
        action: () -> Unit
    ) : this(title, icon = imageVector?.let { @Composable { Icon(it, null) } }, action)
}

class NavItemRegion(
    val title: String,
    val icon: @Composable (() -> Unit)? = null
) : INavItem {
    constructor(
        title: String,
        imageVector: ImageVector?
    ) : this(title, icon = imageVector?.let { @Composable { Icon(it, null) } })
}

class NavItemSpacer(val weight: Float = 1f) : INavItem

data object NavItemDivider : INavItem
package com.michaelflisar.toolbox.app.features.navigation

import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.toolbox.IconComposable

sealed interface INavItem

class NavItem(
    val title: String,
    val icon: IconComposable?,
    val screen: Screen,
) : INavItem

class NavItemAction(
    val title: String,
    val icon: IconComposable?,
    val action: () -> Unit,
) : INavItem

class NavItemRegion(
    val title: String,
    val icon: IconComposable? = null,
) : INavItem

class NavItemSpacer(val weight: Float = 1f) : INavItem

data object NavItemDivider : INavItem
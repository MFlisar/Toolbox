package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.feature.menu.MenuState
import com.michaelflisar.toolbox.feature.menu.PopupMenuScope

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

class NavItemPopupMenu(
    val title: String,
    val icon: IconComposable? = null,
    val state: MenuState,
    val content: @Composable PopupMenuScope.() -> Unit
) : INavItem

data object NavItemDivider : INavItem
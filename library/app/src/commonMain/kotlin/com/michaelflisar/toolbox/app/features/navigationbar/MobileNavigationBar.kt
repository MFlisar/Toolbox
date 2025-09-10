package com.michaelflisar.toolbox.app.features.navigationbar

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.navigation.NavItemDivider
import com.michaelflisar.toolbox.app.features.navigation.NavItemRegion
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer
import com.michaelflisar.toolbox.extensions.Render

@Composable
fun MobileNavigationBar(
    items: List<INavItem>,
    showForSingleItem: Boolean = false
) {
    if (items.size <= 1 && !showForSingleItem) {
        return // no need to show the navigation bar if there is only one item
    }

    val navigator = LocalNavigator.currentOrThrow

    // TODO
    val isRail = false

    if (isRail) {
        NavigationRail {
            items.forEachIndexed { index, item ->
                when (item) {
                    is NavItem -> {
                        NavigationRailItem(
                            icon = {
                                item.icon?.Render()
                            },
                            label = { Text(item.title) },
                            selected = navigator.lastItem == item.screen,
                            onClick = {
                                navigator.replaceAll(item.screen)
                            },
                        )
                    }

                    is NavItemAction -> {
                        throw IllegalArgumentException("NavItemAction is not supported in MobileNavigationBar!")
                    }

                    NavItemDivider,
                    is NavItemRegion,
                    is NavItemSpacer -> {
                        // ignored
                    }
                }
            }
        }
    } else {
        NavigationBar {
            items.forEachIndexed { index, item ->
                when (item) {
                    is NavItem -> {
                        NavigationBarItem(
                            icon = {
                                item.icon?.Render()
                            },
                            label = { Text(item.title) },
                            selected = navigator.lastItem == item.screen,
                            onClick = {
                                navigator.replaceAll(item.screen)
                            },
                        )
                    }

                    is NavItemAction -> {
                        throw IllegalArgumentException("NavItemAction is not supported in MobileNavigationBar!")
                    }

                    NavItemDivider,
                    is NavItemRegion,
                    is NavItemSpacer -> {
                        // ignored
                    }
                }

            }
        }
    }
}
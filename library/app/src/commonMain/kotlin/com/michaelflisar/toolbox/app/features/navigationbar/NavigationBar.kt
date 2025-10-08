package com.michaelflisar.toolbox.app.features.navigationbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.navigation.NavItemDivider
import com.michaelflisar.toolbox.app.features.navigation.NavItemPopupMenu
import com.michaelflisar.toolbox.app.features.navigation.NavItemRegion
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer
import com.michaelflisar.toolbox.extensions.Render
import com.michaelflisar.toolbox.feature.menu.PopupMenu

@Composable
fun NavigationBar(
    items: List<INavItem>,
    alwaysShowLabel: Boolean = true,
    showForSingleItem: Boolean = false,
) {
    if (items.size <= 1 && !showForSingleItem) {
        return // no need to show the navigation bar if there is only one item
    }

    val navigator = LocalNavigator.currentOrThrow

    NavigationBar {
        items.forEachIndexed { index, item ->
            when (item) {
                is NavItem -> {
                    NavigationBarItem(
                        icon = {
                            item.icon?.Render(modifier = Modifier.size(24.dp))
                        },
                        label = { Text(item.title) },
                        alwaysShowLabel = alwaysShowLabel,
                        selected = navigator.lastItem == item.screen,
                        onClick = {
                            navigator.replaceAll(item.screen)
                        },
                    )
                }

                is NavItemAction -> {
                    //throw IllegalArgumentException("NavItemAction is not supported in NavigationBar!")
                    NavigationBarItem(
                        icon = {
                            item.icon?.Render(modifier = Modifier.size(24.dp))
                        },
                        label = { Text(item.title) },
                        alwaysShowLabel = alwaysShowLabel,
                        selected = false,
                        onClick = {
                            item.action()
                        },
                    )

                }
                is NavItemPopupMenu -> {
                    NavigationBarItem(
                        icon = {
                            item.icon?.Render(modifier = Modifier.size(24.dp))
                            PopupMenu(
                                state = item.state
                            ) {
                                item.content(this)
                            }
                        },
                        label = { Text(item.title) },
                        alwaysShowLabel = alwaysShowLabel,
                        selected = false,
                        onClick = {
                            item.state.show()
                        },
                    )

                }

                NavItemDivider,
                is NavItemRegion,
                is NavItemSpacer -> {
                    // ignored
                    L.i { "NavItem of type ${item::class.simpleName} is not supported in NavigationBar (index = $index)" }
                }
            }

        }
    }
}
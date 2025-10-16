package com.michaelflisar.toolbox.app.features.navigationbar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
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
import com.michaelflisar.toolbox.extensions.Icon
import com.michaelflisar.toolbox.feature.menu.PopupMenu

@Composable
internal fun NavigationRail(
    items: List<INavItem>,
    modifier: Modifier,
    alwaysShowLabel: Boolean = true,
    showForSingleItem: Boolean = false,
) {
    if (items.size <= 1 && !showForSingleItem) {
        return // no need to show the navigation bar if there is only one item
    }

    val navigator = LocalNavigator.currentOrThrow

    NavigationRail(
        //windowInsets = WindowInsets.systemBarsForVisualComponents.only(
        //    WindowInsetsSides.Vertical + WindowInsetsSides.Start
        //),
        //windowInsets = WindowInsets.systemBars.only(
        //    WindowInsetsSides.Start
        //),
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        items.forEachIndexed { index, item ->
            when (item) {
                is NavItem -> {
                    NavigationRailItem(
                        icon = {
                            item.icon?.let { Icon(it, modifier = Modifier.size(24.dp)) }
                        },
                        label = { Text(item.title) },
                        alwaysShowLabel = alwaysShowLabel,
                        selected = navigator.lastItem == item.screen,
                        onClick = {
                            navigator.replaceAll(item.screen)
                        },
                    )
                }

                is NavItemPopupMenu -> {
                    NavigationRailItem(
                        icon = {
                            item.icon?.let { Icon(it, modifier = Modifier.size(24.dp)) }
                            PopupMenu(item.state) {
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

                is NavItemAction -> {
                    throw IllegalArgumentException("NavItemAction is not supported in NavigationRail!")
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
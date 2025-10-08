package com.michaelflisar.toolbox.app.features.navigationbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.navigation.NavItemDivider
import com.michaelflisar.toolbox.app.features.navigation.NavItemPopupMenu
import com.michaelflisar.toolbox.app.features.navigation.NavItemRegion
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer
import com.michaelflisar.toolbox.app.features.toolbar.onToolbar
import com.michaelflisar.toolbox.extensions.Render
import com.michaelflisar.toolbox.feature.menu.PopupMenu

@Composable
internal fun WebInsideToolbarNavigation(
    items: List<INavItem>,
    showForSingleItem: Boolean = false,
) {
    if (items.size <= 1 && !showForSingleItem) {
        return // no need to show the navigation bar if there is only one item
    }

    val navigator = LocalNavigator.currentOrThrow

    // this is embedded inside a toolbar!
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEachIndexed { index, item ->
            when (item) {
                is NavItem -> {
                    ToolbarButton(
                        text = item.title,
                        icon = item.icon,
                        selected = navigator.lastItem == item.screen,
                        menu = { },
                        onClick = {
                            navigator.replaceAll(item.screen)
                        }
                    )
                }

                is NavItemPopupMenu -> {
                    ToolbarButton(
                        text = item.title,
                        icon = item.icon,
                        selected = false,
                        menu = {
                            PopupMenu(
                                state = item.state
                            ) {
                                item.content(this)
                            }
                        },
                        onClick = {
                            item.state.show()
                        }
                    )
                }

                is NavItemAction -> {
                    throw IllegalArgumentException("NavItemAction is not supported in WebInsideToolbarNavigation!")
                }

                NavItemDivider -> {
                    VerticalDivider()
                }

                is NavItemRegion,
                is NavItemSpacer -> {
                    // ignored
                    L.i { "NavItem of type ${item::class.simpleName} is not supported in NavigationBar (index = $index)" }
                }
            }

        }
    }
}

@Composable
private fun ToolbarButton(
    text: String,
    icon: IconComposable?,
    selected: Boolean,
    menu: @Composable () -> Unit,
    onClick: () -> Unit
) {
    TextButton(
        onClick = { onClick() },
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.textButtonColors(
            contentColor = LocalContentColor.current
        ),
    ) {
        val density = LocalDensity.current
        val width = remember { mutableStateOf(0.dp) }
        Box {
            Column(
                modifier = Modifier.onSizeChanged {
                    width.value = with(density) { it.width.toDp() }
                }
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icon?.Render()
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = text,
                        //fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
                val color by animateColorAsState(if (selected) MaterialTheme.colorScheme.onToolbar else Color.Transparent)
                Spacer(
                    modifier = Modifier.width(width.value).height(1.dp)
                        .background(color)
                )
            }
            menu()
        }
    }
}
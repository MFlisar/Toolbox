package com.michaelflisar.toolbox.app.features.navigationbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.michaelflisar.toolbox.extensions.Render
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.navigation.NavItemDivider
import com.michaelflisar.toolbox.app.features.navigation.NavItemRegion
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer
import com.michaelflisar.toolbox.app.features.toolbar.onToolbar

@Composable
fun WebInsideToolbarNavigation(
    items: @Composable () -> List<INavItem>,
    showForSingleItem: Boolean = false,
) {
    val items = items()
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
                    TextButton(
                        onClick = {
                            navigator.replaceAll(item.screen)
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = LocalContentColor.current
                        ),
                    ) {
                        val selected = navigator.lastItem == item.screen
                        val density = LocalDensity.current
                        val width = remember { mutableStateOf(0.dp) }
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
                                item.icon?.Render()
                                Text(
                                    modifier = Modifier.wrapContentWidth(),
                                    text = item.title,
                                    //fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            val color by animateColorAsState(if (selected) MaterialTheme.colorScheme.onToolbar else Color.Transparent)
                            Spacer(
                                modifier = Modifier.width(width.value).height(1.dp)
                                    .background(color)
                            )
                        }
                    }
                }

                is NavItemAction -> {
                    throw IllegalArgumentException("NavItemAction is not supported in WebInsideToolbarNavigation!")
                }

                NavItemDivider -> {
                    VerticalDivider()
                }

                is NavItemRegion,
                is NavItemSpacer,
                    -> {
                    // ignored
                }
            }

        }
    }
}
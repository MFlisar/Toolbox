package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.features.backhandlerregistry.LocalBackHandlerRegistry
import com.michaelflisar.toolbox.app.features.menu.Menu
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.WebInsideToolbarNavigation
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import com.michaelflisar.toolbox.app.features.toolbar.composables.ToolbarBackButton
import com.michaelflisar.toolbox.app.features.toolbar.composables.ToolbarTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebToolbar(
    navigationItems: List<INavItem>,
    menuItems: List<MenuItem>,
) {
    val navigator = LocalNavigator.currentOrThrow
    val backHandlerRegistry = LocalBackHandlerRegistry.current
    WebToolbar(
        menuItems = menuItems,
        navigationItems = navigationItems,
        showNavigationForSingleItem = false,
        showBackButton = navigator.canPop || backHandlerRegistry.wouldConsumeBackPress(),
        onBack = {
            if (backHandlerRegistry.handleBackPress()) {
                // --
            } else {
                navigator.pop()
            }
        }
    )
}

@ExperimentalMaterial3Api
@Composable
private fun WebToolbar(
    menuItems: List<MenuItem>,
    navigationItems: List<INavItem>,
    showBackButton: Boolean = false,
    onBack: () -> Unit = {},
    showNavigationForSingleItem: Boolean = false,
    modifier: Modifier = Modifier,
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    // stellt sicher dass Prefs.toolbarStyle genutzt wird!
    background: Color = MaterialTheme.colorScheme.toolbar,
    onBackground: Color = MaterialTheme.colorScheme.onToolbar
) {
    val navigator = LocalNavigator.currentOrThrow
    val currentNavScreen = navigator.lastNavItem
    val navData = currentNavScreen.provideData()

    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolbarTitle(navData.name)
                WebInsideToolbarNavigation(navigationItems, showNavigationForSingleItem)
            }
        },
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (showBackButton) {
                ToolbarBackButton(onClick = onBack)
            }
        },
        actions = {
            Menu(menuItems)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = background,
            scrolledContainerColor = background,
            titleContentColor = onBackground,
            navigationIconContentColor = onBackground,
            actionIconContentColor = onBackground,
        )
    )
}
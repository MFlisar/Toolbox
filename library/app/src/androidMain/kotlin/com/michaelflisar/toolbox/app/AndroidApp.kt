package com.michaelflisar.toolbox.app

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigationbar.MobileNavigationBar
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.scaffold.MobileScaffold
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider
import com.michaelflisar.toolbox.app.features.toolbar.MobileToolbar
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

object AndroidApp

object AndroidAppDefaults {

}

/**
 * The main entry point for the desktop application.
 * This function initializes the desktop app with the provided setup.
 *
 * Content should be provided via [AndroidAppContent]
 *
 * @param navigator the voyager navigator instance
 * @param navigationItems A composable function that provides the list of navigation items.
 * @param menuItems A composable function that provides the list of menu items.
 * @param toolbar A composable function that defines the toolbar layout. Defaults to [AndroidAppDefaults.Toolbar].
 */
@Composable
fun ComponentActivity.AndroidApp(
    navigator: Navigator,
    // Content
    content: @Composable () -> Unit
) {
    // 1) init
    FileKit.init(this)

    val appState = rememberAppState()
    AppThemeProvider(this) {
        RootLocalProvider(appState, setRootLocals = true) {
            Root(
                appState = appState,
                setRootLocals = false
            ) {
                content()
            }
        }
    }
}

/**
 * The main content composable for the Android application.
 *
 * Layout: TitleBar + NavigationBar (BottomBar) + Content
 *
 * @param navigationItems A composable function that provides the list of navigation items.
 * @param toolbar A composable function that defines the toolbar layout. [AndroidToolbar] should be used here.
 * @param footer A n always visible footer at the bottom of the screen, below the content - should be used for ads only!
 */
@Composable
fun AndroidAppContent(
    navigationItems: List<INavItem>,
    toolbar: @Composable () -> Unit,
    footer: @Composable (() -> Unit)? = null
) {
    val navigator = LocalNavigator.currentOrThrow
    MobileScaffold(
        topBar = { toolbar() },
        bottomBar = {
            MobileNavigationBar(navigationItems, showForSingleItem = false)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (footer != null) {
                Column {
                    Box(modifier = Modifier.weight(1f, true)) {
                        AppNavigatorFadeTransition(navigator)
                    }
                    footer()
                }
            } else {
                AppNavigatorFadeTransition(navigator)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidToolbar(
    menuItems: List<MenuItem>,
) {
    MobileToolbar(menuItems)
}
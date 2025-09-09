package com.michaelflisar.toolbox.app

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigationbar.MobileNavigationBar
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.scaffold.MobileScaffold
import com.michaelflisar.toolbox.app.features.toolbar.MobileToolbar
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

object AndroidApp

object AndroidAppDefaults {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Toolbar(
        menuItems: @Composable () -> List<MenuItem>,
    ) {
        MobileToolbar(menuItems())
    }

}

/**
 * The main entry point for the desktop application.
 * This function initializes the desktop app with the provided setup.
 *
 * Layout: TitleBar + NavigationBar (BottomBar) + Content
 *
 * @param navigator the voyager navigator instance
 * @param navigationItems A composable function that provides the list of navigation items.
 * @param menuItems A composable function that provides the list of menu items.
 * @param toolbar A composable function that defines the toolbar layout. Defaults to [AndroidAppDefaults.Toolbar].
 */
@Composable
fun ComponentActivity.AndroidApp(
    navigator: Navigator,
    navigationItems: @Composable () -> List<INavItem>,
    menuItems: @Composable () -> List<MenuItem>,
    // customisation
    toolbar: @Composable () -> Unit = { AndroidAppDefaults.Toolbar(menuItems) }
) {
    // 1) init
    FileKit.init(this)

    val appState = rememberAppState()
    RootLocalProvider(appState, setRootLocals = true) {
        Root(
            appState = appState,
            setRootLocals = false,
            activity = this
        ) {
            MobileScaffold(
                topBar = { toolbar() },
                bottomBar = {
                    MobileNavigationBar(navigationItems, showForSingleItem = false)
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    AppNavigatorFadeTransition(navigator)
                }
            }
        }
    }
}
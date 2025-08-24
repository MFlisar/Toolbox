package com.michaelflisar.toolbox.app

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.DebugDrawer
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigationbar.MobileNavigationBar
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootNavigator
import com.michaelflisar.toolbox.app.features.scaffold.MobileScaffold
import com.michaelflisar.toolbox.app.features.toolbar.MobileToolbar
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

object AndroidApp {


}

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
 * @param screen The initial screen to display.
 * @param navigationItems A composable function that provides the list of navigation items.
 * @param menuItems A composable function that provides the list of menu items.
 * @param toolbar A composable function that defines the toolbar layout. Defaults to [AndroidAppDefaults.Toolbar].
 */
fun ComponentActivity.AndroidApp(
    screen: Screen,
    navigationItems: @Composable () -> List<INavItem>,
    menuItems: @Composable () -> List<MenuItem>,
    // customisation
    toolbar: @Composable () -> Unit = { AndroidAppDefaults.Toolbar(menuItems) }
) {
    FileKit.init(this)
    setContent {
        val appState = rememberAppState()
        RootNavigator(appState, screen, setRootLocals = true) { navigator ->
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
}
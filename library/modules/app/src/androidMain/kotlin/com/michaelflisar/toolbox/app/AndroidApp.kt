package com.michaelflisar.toolbox.app

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberAppToolbar
import com.michaelflisar.toolbox.app.features.menu.BaseAppMenu
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigationbar.MobileNavigationBar
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootNavigator
import com.michaelflisar.toolbox.app.features.scaffold.MobileScaffold
import com.michaelflisar.toolbox.app.features.toolbar.MobileToolbar

object AndroidApp {


}

object AndroidAppDefaults {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Toolbar(
        menuItems: @Composable () -> List<MenuItem>
    ) {
        MobileToolbar()
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
    toolbar: @Composable () -> Unit = {
        AndroidAppDefaults.Toolbar(menuItems)
    },
) {
    setContent {

        val appState = rememberAppState(
            toolbar = rememberAppToolbar(
                menuProvider = { BaseAppMenu(menuItems()) }
            )
        )

        Root(
            appState = appState,
        ) {
            RootNavigator(screen) {
                MobileScaffold(
                    topBar = { toolbar() },
                    bottomBar = {
                        MobileNavigationBar(navigationItems, showForSingleItem = false)
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        CurrentScreen()
                    }
                }
            }
        }
    }
}
package com.michaelflisar.toolbox.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavBackHandler
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.scaffold.MobileScaffold
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider
import com.michaelflisar.toolbox.app.features.toolbar.WebToolbar
import kotlinx.browser.document

object WasmApp

object WasmAppDefaults

/**
 * The main entry point for the wasm application.
 * This function initializes the wasm app with the provided setup.
 *
 * Layout: TitleBar + Navigation in title bar + Content
 *
 * @param setup The general app setup.
 * @param wasmSetup The specific desktop app setup.
 * @param navigator the voyager navigator instance
 * @param navigationItems A composable function that provides the list of navigation items.

 */
@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun WasmApp(
    setup: AppSetup,
    wasmSetup: WasmAppSetup,
    navigator: Navigator,
    content: @Composable () -> Unit
) {
    // 1) init wasm app
    CommonApp.init(setup)

    // 2) remove loading element
    document.getElementById(wasmSetup.divLoadingElementId)?.remove()

    val appState = rememberAppState()
    AppThemeProvider {
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
 * The main content composable for the wasm application.
 *
 * Layout: TitleBar + Navigation in title bar + Content
 *
 * @param menuItems A composable function that provides the list of menu items.
 * @param toolbar A composable function that defines the toolbar layout. [WasmToolbar] should be used here.
 */
@Composable
fun WasmAppContent(
    toolbar: @Composable () -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow
    MobileScaffold(
        topBar = { toolbar() },
        bottomBar = {
            // Nav Items are shown in the toolbar on web!
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavigatorFadeTransition(navigator)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasmToolbar(
    navigationItems: @Composable () -> List<INavItem>,
    menuItems: @Composable () -> List<MenuItem>,
) {
    val navigator = LocalNavigator.currentOrThrow
    val canNavBackHandlerHandleBackPress = NavBackHandler.canHandleBackPress()
    WebToolbar(
        menuItems = menuItems(),
        navigationItems = navigationItems,
        showNavigationForSingleItem = false,
        showBackButton = navigator.canPop,
        onBack = {
            if (canNavBackHandlerHandleBackPress) {
                // --
            } else {
                navigator.pop()
            }
        }
    )
}
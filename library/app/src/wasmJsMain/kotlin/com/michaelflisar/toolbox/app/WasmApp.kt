package com.michaelflisar.toolbox.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.CanvasBasedWindow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavBackHandler
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootNavigator
import com.michaelflisar.toolbox.app.features.scaffold.MobileScaffold
import com.michaelflisar.toolbox.app.features.toolbar.WebToolbar
import kotlinx.browser.document

object WasmApp {

}

object WasmAppDefaults {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Toolbar(
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
}

@OptIn(ExperimentalComposeUiApi::class)
fun WasmApp(
    setup: AppSetup,
    wasmSetup: WasmAppSetup,
    screen: Screen,
    navigationItems: @Composable () -> List<INavItem>,
    menuItems: @Composable () -> List<MenuItem>,
    // customisation
    toolbar: @Composable () -> Unit = {
        WasmAppDefaults.Toolbar(navigationItems, menuItems)
    },
) {
    // 1) init wasm app
    CommonApp.init(setup)

    // 2) remove loading element
    document.getElementById(wasmSetup.divLoadingElementId)?.remove()

    // 3) app
    CanvasBasedWindow(wasmSetup.title, canvasElementId = wasmSetup.canvasElementId) {
        val appState = rememberAppState()
        RootNavigator(appState, screen, setRootLocals = true) { navigator ->
            Root(
                appState = appState,
                setRootLocals = false
            ) {
                MobileScaffold(
                    topBar = { toolbar() },
                    bottomBar = {
                        // Nav Items are shown in the toolbar on web!
                    }
                ) { paddingValues ->
                    L.d { "WasmApp - 5" }
                    Box(modifier = Modifier.padding(paddingValues)) {
                        AppNavigatorFadeTransition(navigator)
                    }
                }
            }
        }
    }
}
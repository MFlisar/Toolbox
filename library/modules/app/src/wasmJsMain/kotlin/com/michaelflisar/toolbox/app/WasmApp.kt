package com.michaelflisar.toolbox.app

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.CanvasBasedWindow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
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
import kotlinx.browser.document

object WasmApp {

}

object WasmAppDefaults {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Toolbar(
        menuItems: @Composable () -> List<MenuItem>
    ) {
        MobileToolbar()
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
        WasmAppDefaults.Toolbar(menuItems)
    },
) {
    // 1) init wasm app
    CommonApp.init(setup)

    // 2) remove loading element
    document.getElementById(wasmSetup.divLoadingElementId)?.remove()

    // 3) app
    CanvasBasedWindow(wasmSetup.title, canvasElementId = wasmSetup.canvasElementId) {

        // 2) app
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
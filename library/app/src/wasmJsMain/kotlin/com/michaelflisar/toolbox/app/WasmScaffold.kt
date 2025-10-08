package com.michaelflisar.toolbox.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.dialogs.ErrorDialogProvider
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavBackHandler
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider
import com.michaelflisar.toolbox.app.features.toolbar.WebToolbar
import kotlinx.browser.document

@Composable
fun WasmApplication(
    screen: Screen,
    wasmSetup: WasmAppSetup,
    // Content
    content: @Composable (navigator: Navigator) -> Unit,
) {
    AppNavigator(
        screen = screen
    ) { navigator ->

        // 2) remove loading element
        document.getElementById(wasmSetup.divLoadingElementId)?.remove()

        val appState = rememberAppState()
        AppThemeProvider {
            RootLocalProvider(appState, setRootLocals = true) {
                Root(
                    appState = appState,
                    setRootLocals = false
                ) {
                    content(navigator)
                }
            }
        }
    }
}

@Composable
fun WasmScaffold(
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable () -> Unit,
) {
    ErrorDialogProvider {
        val appState = LocalAppState.current
        val containerColor = MaterialTheme.colorScheme.background
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = topBar,
            bottomBar = {},
            snackbarHost = { SnackbarHost(appState.snackbarHostState) },
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            containerColor = containerColor,
            contentColor = contentColor,
            contentWindowInsets = contentWindowInsets
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                content()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasmToolbar(
    navigationItems: List<INavItem>,
    menuItems: List<MenuItem>,
) {
    val navigator = LocalNavigator.currentOrThrow
    val canNavBackHandlerHandleBackPress = NavBackHandler.canHandleBackPress()
    WebToolbar(
        menuItems = menuItems,
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
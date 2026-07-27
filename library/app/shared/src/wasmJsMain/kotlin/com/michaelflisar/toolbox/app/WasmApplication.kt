package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.toolbox.MyTheme
import com.michaelflisar.toolbox.app.classes.WasmAppSetup
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.backhandler.WasmBackHandlerUtil
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider
import kotlinx.browser.document

@Composable
fun WasmApplication(
    screen: Screen,
    wasmSetup: WasmAppSetup,
    theme: MyTheme = MyTheme.default(),
    // Content
    content: @Composable (navigator: Navigator) -> Unit,
) {
    ProvideAppLocals(Unit) {
        AppNavigator(
            screen = screen
        ) { navigator ->

            // 2) remove loading element
            document.getElementById(wasmSetup.divLoadingElementId)?.remove()

            val appState = rememberAppState()
            AppThemeProvider(theme) {
                RootLocalProvider(appState, setRootLocals = true) {
                    Root(
                        appState = appState,
                        setRootLocals = false
                    ) {
                        WasmBackHandlerUtil.ProvideMouseBackHandler()
                        content(navigator)
                    }
                }
            }
        }
    }
}



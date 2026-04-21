package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.ApplicationScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.toolbox.MyTheme
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberJewelAppState
import com.michaelflisar.toolbox.app.features.backhandler.JvmBackHandlerUtil
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider
import com.michaelflisar.toolbox.app.jewel.JewelApp
import com.michaelflisar.toolbox.app.jewel.JewelExitHandler
import com.michaelflisar.toolbox.app.jewel.JewelRoot
import org.jetbrains.jewel.window.DecoratedWindowScope

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplicationScope.DesktopApplication(
    // Navigator
    screen: Screen,
    theme: MyTheme = MyTheme.windowsDefault(),
    // JVM specific
    onClosed: (suspend () -> Unit)? = null,
    onPreviewKeyEvent: (NativeKeyEvent) -> Boolean = { false },
    onKeyEvent: (NativeKeyEvent) -> Boolean = { false },
    appIsClosing: MutableState<Boolean> = remember { mutableStateOf(false) },
    // Content
    content: @Composable DecoratedWindowScope.(navigator: Navigator) -> Unit,
) {
    ProvideAppLocals(Unit) {

        val desktopSetup = DesktopAppSetup.get()

        // 1) app states
        val jewelAppState = rememberJewelAppState(desktopSetup.prefs)

        // 2) app
        JewelApp {

            JewelRoot(
                jewelAppState = jewelAppState,
                appIsClosing = appIsClosing,
                onClosed = onClosed,
                onPreviewKeyEvent = onPreviewKeyEvent,
                onKeyEvent = onKeyEvent,
            ) {
                if (desktopSetup.ensureIsFullyOnScreen) {
                    val window = this.window
                    val density = LocalDensity.current
                    LaunchedEffect(density, window) {
                        jewelAppState.ensureIsFullyOnScreen(density, window)
                    }
                }
                AppNavigator(
                    screen = screen
                ) { navigator ->
                    val appState = rememberAppState()
                    AppThemeProvider(theme) {
                        RootLocalProvider(appState, setRootLocals = true) {
                            JvmBackHandlerUtil.ProvideMouseBackHandler()
                            content(navigator)
                        }
                    }
                }
            }

            // Close Action
            JewelExitHandler(appIsClosing)
        }
    }
}
package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.ApplicationScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.toolbox.MyTheme
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.DesktopExitHandler
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberDesktopAppState
import com.michaelflisar.toolbox.app.features.backhandler.JvmBackHandlerUtil
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider
import com.michaelflisar.toolbox.app.features.theme.ThemeSetup
import com.michaelflisar.toolbox.app.internal.JRBThemeSetup
import com.michaelflisar.toolbox.app.jewel.JewelApp
import com.michaelflisar.toolbox.app.jewel.JewelRoot
import org.jetbrains.jewel.window.defaultTitleBarStyle

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplicationScope.DesktopApplication(
    // Navigator
    screen: Screen,
    theme: MyTheme = MyTheme.windowsDefault(),
    // JVM specific
    onClosed: (suspend () -> Unit)? = null,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    appIsClosing: MutableState<Boolean> = remember { mutableStateOf(false) },
    // Content
    content: @Composable (navigator: Navigator) -> Unit,
) {
    // Jvm - JRE vs JBR
    JvmImpl.init(
        instance = object : IJvmImpl {
            override val defaultStatusBarForegroundColor: Color
                @Composable get() = org.jetbrains.jewel.foundation.theme.JewelTheme.defaultTitleBarStyle.colors.content

            override val defaultStatusBarBackgroundColor: Color
                @Composable get() = org.jetbrains.jewel.foundation.theme.JewelTheme.defaultTitleBarStyle.colors.background
        }
    )

    // Custom JBR Theme
    ThemeSetup.set(JRBThemeSetup)

    ProvideAppLocals(Unit) {

        val desktopSetup = DesktopAppSetup.get()

        // 1) app states
        val jewelAppState = rememberDesktopAppState(desktopSetup.prefs)

        // 2) app
        JewelApp {

            JewelRoot(
                desktopAppState = jewelAppState,
                appIsClosing = appIsClosing,
                onClosed = onClosed,
                onPreviewKeyEvent = onPreviewKeyEvent,
                onKeyEvent = onKeyEvent,
            ) {
                if (desktopSetup.ensureIsFullyOnScreen) {
                    val window = LocalComposeWindow.current
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
            DesktopExitHandler(appIsClosing)
        }
    }
}
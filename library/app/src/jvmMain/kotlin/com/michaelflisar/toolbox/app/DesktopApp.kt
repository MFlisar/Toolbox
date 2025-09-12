package com.michaelflisar.toolbox.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.DesktopAppDefaults.getDefaultStatusBarItemsLeft
import com.michaelflisar.toolbox.app.DesktopAppDefaults.getDefaultStatusBarItemsRight
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberJewelAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.rememberComposeTheme
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.scaffold.DesktopScaffold
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider
import com.michaelflisar.toolbox.app.jewel.JewelApp
import com.michaelflisar.toolbox.app.jewel.JewelExitHandler
import com.michaelflisar.toolbox.app.jewel.JewelNavigation
import com.michaelflisar.toolbox.app.jewel.JewelNavigationContainer
import com.michaelflisar.toolbox.app.jewel.JewelRoot
import com.michaelflisar.toolbox.app.jewel.JewelStatusBar
import com.michaelflisar.toolbox.app.jewel.JewelStatusBarItem
import com.michaelflisar.toolbox.app.jewel.JewelTitleBar
import com.michaelflisar.toolbox.app.jewel.JewelTitleBarSetup
import com.michaelflisar.toolbox.app.jewel.JewelTitleMenu
import com.michaelflisar.toolbox.app.jewel.toJewelNavigationItems
import com.michaelflisar.toolbox.utils.JvmUtil
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.jewel.window.DecoratedWindowScope

object DesktopApp {

    private var _setup: DesktopAppSetup? = null

    val setup: DesktopAppSetup
        get() = _setup ?: throw IllegalStateException("DesktopApp not initialized yet!")

    fun init(
        setup: AppSetup,
        desktopSetup: DesktopAppSetup,
    ) {
        if (_setup != null) {
            // already initialized
            return
        }
        CommonApp.init(setup)
        FileKit.init(appId = setup.packageName)
        this._setup = desktopSetup
    }
}

object DesktopAppDefaults {

    fun getDefaultStatusBarItemsLeft(
        showAppIconLeft: Boolean = true,
        showAppVersionLeft: Boolean = true,
        onAppIconClick: (() -> Unit)? = null,
        onAppVersionClick: (() -> Unit)? = null,
    ): List<JewelStatusBarItem> {
        val setup = CommonApp.setup
        return listOfNotNull(
            JewelStatusBarItem.Custom {
                Image(
                    painter = setup.icon(),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(4.dp)
                        .then(
                            if (onAppIconClick != null) {
                                Modifier.clickable { onAppIconClick() }
                            } else Modifier
                        )
                )
            }.takeIf { showAppIconLeft },
            JewelStatusBarItem.Text(
                "App Version: ${setup.versionName}",
                onClick = onAppVersionClick
            ).takeIf { showAppVersionLeft }
        )
    }

    fun getDefaultStatusBarItemsRight(
        showJavaVersionRight: Boolean = true,
        showUserNameRight: Boolean = true,
        showHostNameRight: Boolean = true,
        onJavaVersionClick: (() -> Unit)? = null,
        onUserNameClick: (() -> Unit)? = null,
        onHostNameClick: (() -> Unit)? = null,
    ): List<JewelStatusBarItem> {
        return listOfNotNull(
            JewelStatusBarItem.Text(JvmUtil.javaVersion(), onClick = onJavaVersionClick)
                .takeIf { showJavaVersionRight },
            JewelStatusBarItem.Text(JvmUtil.userName(), onClick = onUserNameClick)
                .takeIf { showUserNameRight },
            JewelStatusBarItem.Text(JvmUtil.hostName(), onClick = onHostNameClick)
                .takeIf { showHostNameRight }
        )
    }
}

/**
 * The main entry point for the desktop application.
 * This function initializes the desktop app with the provided setup.
 *
 * Content should be provided via [DesktopAppContent]
 *
 * @param setup The general app setup.
 * @param desktopSetup The specific desktop app setup.
 * @param navigator the voyager navigator instance
 */
@Composable
fun ApplicationScope.DesktopApp(
    setup: AppSetup,
    desktopSetup: DesktopAppSetup,
    navigator: Navigator,
    // JVM specific
    onClosed: (suspend () -> Unit)? = null,
    onPreviewKeyEvent: (NativeKeyEvent) -> Boolean = { false },
    onKeyEvent: (NativeKeyEvent) -> Boolean = { false },
    appIsClosing: MutableState<Boolean> = remember { mutableStateOf(false) },
    // Content
    content: @Composable DecoratedWindowScope.() -> Unit
) {
    // -----------------
    // 1) init desktop app
    // -----------------

    DesktopApp.init(
        setup = setup,
        desktopSetup = desktopSetup
    )

    // -----------------
    // 2) application
    // -----------------

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
            val appState = rememberAppState()
            AppThemeProvider {
                RootLocalProvider(appState, setRootLocals = true) {
                    content()
                }
            }
        }
        // Close Action
        JewelExitHandler(appIsClosing)
    }
}

/**
 * The default content for a desktop application.
 * It includes a title bar, navigation, and status bar.
 *
 *
 * @param navigationItems A composable function that provides the list of navigation items.
 * @param titlebar A composable function that defines the title bar. [DesktopTitleBar] should be used here.
 * @param statusbar A composable function that defines the status bar. [DesktopStatusBar] should be used here.
 * @param navigationExpanded A mutable state that controls whether the navigation is expanded or collapsed.
 */
@Composable
fun DecoratedWindowScope.DesktopAppContent(
    navigationItems: List<INavItem>,
    titlebar: @Composable DecoratedWindowScope.() -> Unit,
    statusbar: @Composable () -> Unit,
    navigationExpanded: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    val appState = LocalAppState.current
    Column {
        titlebar()
        Root(
            appState = appState,
            setRootLocals = false
        ) {
            val navigator = LocalNavigator.currentOrThrow
            DesktopScaffold(
                statusbar = statusbar,
            ) { paddingValues ->
                val jewelItems = navigationItems.toJewelNavigationItems()
                JewelNavigationContainer(
                    modifier = Modifier.padding(paddingValues),
                    navigation = {
                        JewelNavigation(
                            items = jewelItems,
                            selected = { it == navigator.lastItem },
                            expanded = navigationExpanded
                        )
                    },
                    content = {
                        AppNavigatorFadeTransition(navigator)
                    }
                )
            }
        }
    }
}

@Composable
fun DecoratedWindowScope.DesktopTitleBar(
    menuItems: List<MenuItem>,
    setup: JewelTitleBarSetup = JewelTitleBarSetup(
        showAlwaysOnTop = true,
        showThemeSelector = true
    )
) {
    val navigator = LocalNavigator.currentOrThrow
    val navScreen = navigator.lastNavItem
    val additionalMenu = navScreen.provideMenu()
    JewelTitleBar(
        setup = setup,
        menubar = { JewelTitleMenu(items = menuItems + additionalMenu) }
    )
}

@Composable
fun DesktopStatusBar(
    showAppIconLeft: Boolean = true,
    showAppVersionLeft: Boolean = true,
    showJavaVersionRight: Boolean = true,
    showUserNameRight: Boolean = true,
    showHostNameRight: Boolean = true,
    onAppIconClick: (() -> Unit)? = null,
    onAppVersionClick: (() -> Unit)? = null,
    onJavaVersionClick: (() -> Unit)? = null,
    onUserNameClick: (() -> Unit)? = null,
    onHostNameClick: (() -> Unit)? = null,
    foreground: Color = Color.Unspecified,
    background: Color = Color.Unspecified
) {
    val statusBarLeft = getDefaultStatusBarItemsLeft(
        showAppIconLeft,
        showAppVersionLeft,
        onAppIconClick,
        onAppVersionClick
    )
    val statusBarRight = getDefaultStatusBarItemsRight(
        showJavaVersionRight,
        showUserNameRight,
        showHostNameRight,
        onJavaVersionClick,
        onUserNameClick,
        onHostNameClick
    )
    if (statusBarLeft.isEmpty() && statusBarRight.isEmpty()) {
        return // nothing to show
    }
    JewelStatusBar(
        left = statusBarLeft,
        right = statusBarRight,
        foreground = foreground,
        background = background
    )
}
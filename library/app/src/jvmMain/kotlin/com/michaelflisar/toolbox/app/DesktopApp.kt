package com.michaelflisar.toolbox.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.Toolbox
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberJewelAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.DebugDrawer
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import com.michaelflisar.toolbox.app.features.preferences.DesktopPrefs
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootNavigator
import com.michaelflisar.toolbox.app.features.scaffold.DesktopScaffold
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.jewel.window.DecoratedWindowScope

object DesktopApp {

    lateinit var setup: DesktopAppSetup
        private set

    fun init(
        setup: AppSetup,
        desktopSetup: DesktopAppSetup,
    ) {
        CommonApp.init(setup)
        FileKit.init(appId = setup.packageName)
        this.setup = desktopSetup
    }
}

object DesktopAppDefaults {

    @Composable
    fun TitleBar(
        scope: DecoratedWindowScope,
        prefs: DesktopPrefs,
        menuItems: List<MenuItem>,
        setup: JewelTitleBarSetup = JewelTitleBarSetup(
            showAlwaysOnTop = true,
            showThemeSelector = true
        ),
    ) {
        with(scope) {

            val navigator = LocalNavigator.currentOrThrow
            val navScreen = navigator.lastNavItem
            val menuItems = menuItems
            val additionalMenu = navScreen.provideMenu()

            JewelTitleBar(
                prefs = prefs,
                setup = setup,
                menubar = { JewelTitleMenu(items = menuItems + additionalMenu) }
            )
        }
    }

    @Composable
    fun StatusBar(
        showAppIconLeft: Boolean = true,
        showAppVersionLeft: Boolean = true,
        showJavaVersionRight: Boolean = true,
        showUserNameRight: Boolean = true,
        showHostNameRight: Boolean = true,
    ) {
        val setup = CommonApp.setup
        val appState = LocalAppState.current
        val statusBarLeft = listOfNotNull(
            JewelStatusBarItem.Custom {
                Image(
                    painter = setup.icon(),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(4.dp)
                )
            }.takeIf { showAppIconLeft },
            JewelStatusBarItem.Text("App Version: ${setup.versionName}") {
                appState.showSnackbar("App clicked!")
            }.takeIf { showAppVersionLeft }
        )
        val statusBarRight = listOfNotNull(
            JewelStatusBarItem.Text(JvmUtil.javaVersion()).takeIf { showJavaVersionRight },
            JewelStatusBarItem.Text(JvmUtil.userName()).takeIf { showUserNameRight },
            JewelStatusBarItem.Text(JvmUtil.hostName()).takeIf { showHostNameRight }
        )
        if (statusBarLeft.isEmpty() && statusBarRight.isEmpty()) {
            return // nothing to show
        }
        JewelStatusBar(
            left = statusBarLeft,
            right = statusBarRight,
        )
    }

}

/**
 * The main entry point for the desktop application.
 * This function initializes the desktop app with the provided setup.
 *
 * Layout: TitleBar + Statusbar + JewelNavigation (Rail) on the left side + Content
 *
 * @param setup The general app setup.
 * @param desktopSetup The specific desktop app setup.
 * @param screen The initial screen to display.
 * @param navigationItems A composable function that provides the list of navigation items.
 * @param menuItems A composable function that provides the list of menu items.
 * @param titlebar A composable function for the title bar, defaulting to [DesktopAppDefaults.TitleBar].
 * @param statusbar A composable function for the status bar, defaulting to [DesktopAppDefaults.StatusBar].
 */
fun DesktopApp(
    setup: AppSetup,
    desktopSetup: DesktopAppSetup,
    screen: Screen,
    navigationItems: @Composable () -> List<INavItem>,
    menuItems: @Composable () -> List<MenuItem>,
    // customisation
    titlebar: @Composable DecoratedWindowScope.() -> Unit = { DesktopAppDefaults.TitleBar(this,desktopSetup.prefs,  menuItems()) },
    statusbar: @Composable () -> Unit = { DesktopAppDefaults.StatusBar() }
) {
    // 1) init desktop app
    DesktopApp.init(
        setup = setup,
        desktopSetup = desktopSetup
    )

    // 2) application
    application {

        // 1) app states
        val jewelAppState = rememberJewelAppState(desktopSetup.prefs)

        // 2) app
        JewelApp {
            val appIsClosing = remember { mutableStateOf(false) }
            JewelRoot(
                jewelAppState = jewelAppState,
                appIsClosing = appIsClosing
            ) {
                val window = this.window
                val density = LocalDensity.current
                LaunchedEffect(this.window) {
                    jewelAppState.ensureIsFullyOnScreen(density, window)
                }

                val appState = rememberAppState()
                RootNavigator(appState, screen, setRootLocals = true) { navigator ->
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
                                val jewelItems = navigationItems().toJewelNavigationItems()
                                JewelNavigationContainer(
                                    modifier = Modifier.padding(paddingValues),
                                    navigation = {
                                        JewelNavigation(
                                            items = jewelItems,
                                            selected = { it == navigator.lastItem }
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
            }
            // Close Action
            JewelExitHandler(appIsClosing)
        }
    }
}
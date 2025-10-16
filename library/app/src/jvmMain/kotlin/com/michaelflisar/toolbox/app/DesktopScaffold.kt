package com.michaelflisar.toolbox.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.ApplicationScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.DesktopAppDefaults.getDefaultStatusBarItemsLeft
import com.michaelflisar.toolbox.app.DesktopAppDefaults.getDefaultStatusBarItemsRight
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberJewelAppState
import com.michaelflisar.toolbox.app.features.dialogs.ErrorDialogProvider
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer
import com.michaelflisar.toolbox.app.features.navigationbar.NavigationBar
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.scaffold.NavigationStyle
import com.michaelflisar.toolbox.app.features.scaffold.rememberNavigationStyleAuto
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider
import com.michaelflisar.toolbox.app.features.toolbar.DesktopToolbar
import com.michaelflisar.toolbox.app.features.toolbar.DesktopToolbarProvider
import com.michaelflisar.toolbox.app.features.toolbar.LocalDesktopToolbarProvider
import com.michaelflisar.toolbox.app.jewel.JewelApp
import com.michaelflisar.toolbox.app.jewel.JewelExitHandler
import com.michaelflisar.toolbox.app.jewel.JewelNavigation
import com.michaelflisar.toolbox.app.jewel.JewelRoot
import com.michaelflisar.toolbox.app.jewel.JewelStatusBar
import com.michaelflisar.toolbox.app.jewel.JewelTitleBar
import com.michaelflisar.toolbox.app.jewel.JewelTitleMenu
import com.michaelflisar.toolbox.app.jewel.toJewelNavigationItems
import org.jetbrains.jewel.window.DecoratedWindowScope

@Composable
fun ApplicationScope.DesktopApplication(
    // Navigator
    screen: Screen,
    // JVM specific
    onClosed: (suspend () -> Unit)? = null,
    onPreviewKeyEvent: (NativeKeyEvent) -> Boolean = { false },
    onKeyEvent: (NativeKeyEvent) -> Boolean = { false },
    appIsClosing: MutableState<Boolean> = remember { mutableStateOf(false) },
    // Content
    content: @Composable DecoratedWindowScope.(navigator: Navigator) -> Unit,
) {
    AppNavigator(
        screen = screen
    ) { navigator ->

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
                val appState = rememberAppState()
                AppThemeProvider {
                    RootLocalProvider(appState, setRootLocals = true) {
                        content(navigator)
                    }
                }
            }

            // Close Action
            JewelExitHandler(appIsClosing)
        }
    }
}


@Composable
fun DecoratedWindowScope.DesktopScaffold(
    titleBar: @Composable DecoratedWindowScope.() -> Unit = {},
    statusBar: @Composable () -> Unit = {},
    navigationStyle: State<NavigationStyle> = rememberNavigationStyleAuto(),
    navigation: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    contentToolbar: DesktopToolbarProvider = { screen -> DesktopToolbar(screen) },
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDesktopToolbarProvider provides contentToolbar
    ) {
        ErrorDialogProvider {
            val appState = LocalAppState.current
            Column {
                titleBar()
                Root(
                    appState = appState,
                    setRootLocals = false
                ) {
                    val appState = LocalAppState.current
                    Column(modifier = Modifier) {
                        Scaffold(
                            modifier = Modifier.weight(1f),
                            topBar = {},
                            bottomBar = {
                                if (navigationStyle.value == NavigationStyle.Bottom) {
                                    navigation()
                                }
                            },
                            snackbarHost = { SnackbarHost(appState.snackbarHostState) },
                            floatingActionButton = floatingActionButton,
                            floatingActionButtonPosition = floatingActionButtonPosition,
                            containerColor = containerColor,
                            contentColor = contentColor,
                            contentWindowInsets = contentWindowInsets
                        ) { paddingValues ->
                            Row(
                                modifier = Modifier.fillMaxSize().padding(paddingValues)
                            ) {
                                if (navigationStyle.value == NavigationStyle.Left)
                                    navigation()
                                content()
                            }
                        }
                        statusBar()
                    }
                }
            }
        }
    }
}

class DesktopTitleBarSetup(
    val showAlwaysOnTop: Boolean = true,
    val showThemeSelector: Boolean = true,
)

class DesktopTitleAction(
    val title: String,
    val imageVector: ImageVector,
    val onClick: () -> Unit,
)

@Composable
fun DecoratedWindowScope.DesktopTitleBar(
    setup: DesktopTitleBarSetup = DesktopTitleBarSetup(
        showAlwaysOnTop = true,
        showThemeSelector = true
    ),
    iconItems: List<DesktopTitleAction> = emptyList(),
    menu: @Composable () -> Unit = { },
) {
    JewelTitleBar(
        setup = setup,
        iconItems = iconItems,
        menubar = {
            menu()
        }
    )
}

@Composable
fun DesktopTitleMenu(
    items: List<MenuItem>,
) {
    JewelTitleMenu(items = items)
}

sealed class DesktopStatusBarItem {

    class Text(
        val text: String,
        val color: Color = Color.Unspecified,
        val onClick: (() -> Unit)? = null,
    ) : DesktopStatusBarItem()

    class Custom(
        val content: @Composable () -> Unit,
    ) : DesktopStatusBarItem()
}

@Composable
fun DesktopStatusBar(
    showAppVersionLeft: Boolean = true,
    showJavaVersionRight: Boolean = true,
    showUserNameRight: Boolean = true,
    showHostNameRight: Boolean = true,
    onAppVersionClick: (() -> Unit)? = null,
    onJavaVersionClick: (() -> Unit)? = null,
    onUserNameClick: (() -> Unit)? = null,
    onHostNameClick: (() -> Unit)? = null,
    foreground: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    content: @Composable (() -> Unit)? = null
) {
    val statusBarLeft = getDefaultStatusBarItemsLeft(
        showAppVersionLeft,
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
        background = background,
        content = content
    )
}

@Composable
fun DesktopStatusBarCustom(
    left: List<DesktopStatusBarItem> = emptyList(),
    right: List<DesktopStatusBarItem> = emptyList(),
    foreground: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    content: @Composable (() -> Unit)? = null,
) {
    JewelStatusBar(
        left = left,
        right = right,
        foreground = foreground,
        background = background,
        content = content
    )
}

@Composable
fun DesktopNavigation(
    navigationStyle: State<NavigationStyle>,
    items:  @Composable (style: NavigationStyle) -> List<INavItem>,
    additionalItems: @Composable (style: NavigationStyle) -> List<INavItem>,
    modifier: Modifier = Modifier,
    showForSingleItem: Boolean = false,
    // Rail
    isRailExpandable: Boolean = true,
    isRailExpanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    showAdditionalItemsAtBottomIfRail: Boolean = true,
    // Bottom
    alwaysShowBottomLabel: Boolean = true,
) {
    when (navigationStyle.value) {
        NavigationStyle.Left -> {
            DesktopNavigationRail(
                modifier = modifier,
                itemsTop = items(NavigationStyle.Left) + if (showAdditionalItemsAtBottomIfRail) emptyList() else additionalItems(
                    NavigationStyle.Left
                ),
                itemsBottom = if (showAdditionalItemsAtBottomIfRail) additionalItems(NavigationStyle.Left) else emptyList(),
                //alwaysShowLabel = alwaysShowLabel,
                showForSingleItem = showForSingleItem,
                expandable = isRailExpandable,
                navigationExpanded = isRailExpanded
            )
        }

        NavigationStyle.Bottom -> {
            DesktopNavigationBottom(
                modifier = modifier,
                items = items(NavigationStyle.Bottom) + additionalItems(NavigationStyle.Bottom),
                alwaysShowLabel = alwaysShowBottomLabel,
                showForSingleItem = showForSingleItem
            )
        }
    }
}

@Composable
fun DesktopNavigationRail(
    itemsTop: List<INavItem>,
    itemsBottom: List<INavItem>,
    showForSingleItem: Boolean,
    expandable: Boolean,
    modifier: Modifier = Modifier,
    navigationExpanded: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    if (itemsTop.size + itemsBottom.size <= 1 && !showForSingleItem) {
        return // no need to show the navigation rail if there is only one item
    }
    val navigator = LocalNavigator.currentOrThrow
    val items: List<INavItem> =
        itemsTop + (itemsBottom.takeIf { it.isNotEmpty() }?.let { listOf(NavItemSpacer()) + it }
            ?: emptyList())
    JewelNavigation(
        modifier = modifier,
        items = items.toJewelNavigationItems(),
        selected = { it == navigator.lastItem },
        expanded = navigationExpanded,
        setup = JewelNavigation.Setup(
            showExpand = expandable
        )
    )
}

@Composable
fun DesktopNavigationBottom(
    items: List<INavItem>,
    modifier: Modifier = Modifier,
    alwaysShowLabel: Boolean = true,
    showForSingleItem: Boolean = false,
) {
    NavigationBar(items, modifier, alwaysShowLabel, showForSingleItem)
}
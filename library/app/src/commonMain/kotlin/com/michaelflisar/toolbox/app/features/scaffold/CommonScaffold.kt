package com.michaelflisar.toolbox.app.features.scaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.toolbox.app.features.actions.IBaseAction
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorTransitionPlatformStyle
import com.michaelflisar.toolbox.app.features.navigation.NavItemPopupMenu
import com.michaelflisar.toolbox.app.features.navigation.NavItemRegion
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import com.michaelflisar.toolbox.app.features.navigation.screen.INavScreen
import com.michaelflisar.toolbox.app.features.navigationbar.DesktopNavigationBottom
import com.michaelflisar.toolbox.app.features.navigationbar.DesktopNavigationRail
import com.michaelflisar.toolbox.app.features.navigationbar.MobileNavigationBar
import com.michaelflisar.toolbox.app.features.navigationbar.MobileNavigationRail
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionSetup
import com.michaelflisar.toolbox.app.features.toolbar.WebToolbar
import com.michaelflisar.toolbox.app.features.toolbar.composables.LocalToolbarMainMenuItems
import com.michaelflisar.toolbox.app.features.toolbar.composables.SharedToolbarContainer
import com.michaelflisar.toolbox.app.features.toolbar.composables.ToolbarMainMenuItems
import com.michaelflisar.toolbox.app.pages.PageSettings
import com.michaelflisar.toolbox.app.platform.PlatformStylePreference
import com.michaelflisar.toolbox.app.platform.ResolvedPlatformStyle
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_more
import com.michaelflisar.toolbox.core.resources.menu_settings
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.menu.MenuSeparator
import com.michaelflisar.toolbox.feature.menu.PopupMenu
import com.michaelflisar.toolbox.feature.menu.rememberMenuState
import com.michaelflisar.toolbox.features.proversion.ProState
import org.jetbrains.compose.resources.stringResource

val LocalPlatformStyle =
    compositionLocalOf<ResolvedPlatformStyle> { throw RuntimeException("PlatformStyle not initialised!") }
private val LocalNavigationData =
    compositionLocalOf<NavigationData> { throw RuntimeException("NavigationData not initialised!") }
private val LocalPlatformSpecifics =
    compositionLocalOf<CommonScaffold.PlatformSpecifics> { throw RuntimeException("PlatformSpecifics not initialised!") }

object CommonScaffold {

    class PlatformSpecifics internal constructor(
        val mobile: Mobile,
        val desktop: Desktop,
        val web: Web,
    ) {
        class Mobile internal constructor(
            // no platform specific settings for now
        )

        class Desktop internal constructor(
            val isRailExpandable: Boolean,
            val isRailExpanded: MutableState<Boolean>,
            val showAdditionalItemsAtBottomIfRail: Boolean,
        )

        class Web internal constructor(
            val disableBottomAndSideBar: Boolean,
        )
    }

    enum class SideParPosition {
        BelowTopAppBar,
        FullHeight
    }

    /**
     * wir wollen keine Labels im root level hier
     */
    @Composable
    fun getWebMenuItems(
        pageSettings: PageSettings,
        customActions: List<MenuItem> = emptyList(),
    ): List<MenuItem> {

        val proVersionManager = ProVersionManager.setup
        val proVersion by proVersionManager.proState.collectAsState()

        // 1) Pro Version + Separator
        val itemsProVersion = if (proVersion == ProState.Yes) {
            null
        } else {
            (proVersionManager as? ProVersionSetup.Supported)?.action?.invoke()?.toMenuItem()
        }

        // 2) custom actions
        val itemsCustomAction = customActions

        return listOfNotNull(
            itemsProVersion,
            pageSettings.toMenuItem(openInDialog = false, expandableHeaderOnMobileInDialog = true),
            MenuItem.Group(
                icon = Icons.Default.MoreVert.toIconComposable(),
                items = itemsCustomAction
            ).takeIf { it.items.isNotEmpty() }
        )
    }

    @Composable
    fun getMobileMenuItems(
        pageSettings: PageSettings,
        customActions: List<MenuItem> = emptyList(),
    ): ToolbarMainMenuItems {

        val proVersionManager = ProVersionManager.setup
        val proVersion by proVersionManager.proState.collectAsState()

        val itemProVersion = if (proVersion == ProState.Yes) {
            null
        } else {
            (proVersionManager as? ProVersionSetup.Supported)?.action?.invoke()?.toMenuItem()
        }

        val itemSetting =
            pageSettings.toMenuItem(openInDialog = true, expandableHeaderOnMobileInDialog = true)

        return remember(itemProVersion) {
            ToolbarMainMenuItems(
                itemProVersion,
                itemSetting,
                customActions
            )
        }
    }
}

data class NavigationData(
    val pageSettings: PageSettings,
    val mainPages: List<INavScreen>,
    val additionalActionItems: List<IBaseAction> = emptyList(),
    val showLabels: (style: NavigationStyle) -> Boolean = { _ -> true },
    val showForSingleItem: (style: NavigationStyle) -> Boolean = { _ -> false },
)

@Composable
fun rememberPlatformScaffoldSpecificsMobile(
    // no platform specific settings for now
): CommonScaffold.PlatformSpecifics.Mobile {
    return remember { CommonScaffold.PlatformSpecifics.Mobile() }
}

@Composable
fun rememberPlatformScaffoldSpecificsDesktop(
    isRailExpandable: Boolean = true,
    isRailExpanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    showAdditionalItemsAtBottomIfRail: Boolean = true,
): CommonScaffold.PlatformSpecifics.Desktop {
    return remember {
        CommonScaffold.PlatformSpecifics.Desktop(
            isRailExpandable = isRailExpandable,
            isRailExpanded = isRailExpanded,
            showAdditionalItemsAtBottomIfRail = showAdditionalItemsAtBottomIfRail
        )
    }
}

@Composable
fun rememberPlatformScaffoldSpecificsWeb(
    disableBottomAndSideBar: Boolean = true,
): CommonScaffold.PlatformSpecifics.Web {
    return remember {
        CommonScaffold.PlatformSpecifics.Web(
            disableBottomAndSideBar = disableBottomAndSideBar
        )
    }
}

@Composable
fun rememberPlatformScaffoldSpecifics(
    mobile: CommonScaffold.PlatformSpecifics.Mobile = rememberPlatformScaffoldSpecificsMobile(),
    desktop: CommonScaffold.PlatformSpecifics.Desktop = rememberPlatformScaffoldSpecificsDesktop(),
    web: CommonScaffold.PlatformSpecifics.Web = rememberPlatformScaffoldSpecificsWeb(),
): CommonScaffold.PlatformSpecifics {
    return remember {
        CommonScaffold.PlatformSpecifics(
            mobile = mobile,
            desktop = desktop,
            web = web
        )
    }
}

@Composable
fun CommonScaffold(
    navigator: Navigator,
    navigationData: NavigationData,
    modifier: Modifier = Modifier,
    stylePreference: PlatformStylePreference = PlatformStylePreference.Auto,
    navigationStyle: State<NavigationStyle> = rememberNavigationStyleAuto(),
    // platform specifics
    platformSpecifics: CommonScaffold.PlatformSpecifics = rememberPlatformScaffoldSpecifics(),
    // scaffold UI parts
    topBar: @Composable (screen: INavScreen) -> Unit = { screen ->
        CommonTopBar(screen)
    },
    bottomBar: @Composable (navigationStyle: NavigationStyle, screen: INavScreen) -> Unit = { navigationStyle, screen ->
        if (navigationStyle == NavigationStyle.Bottom) {
            CommonBottomBar()
        }
    },
    sideBar: @Composable (navigationStyle: NavigationStyle, screen: INavScreen) -> Unit = { navigationStyle, screen ->
        if (navigationStyle == NavigationStyle.Left) {
            CommonSideBar()
        }
    },
    floatingActionButton: @Composable () -> Unit = {},
    // settings
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    // content
    content: @Composable () -> Unit = {
        AppNavigatorTransitionPlatformStyle(navigator)
    },
) {
    val screen = navigator.lastNavItem
    val appState = LocalAppState.current
    val style = stylePreference.resolve()
    val sidebarPosition = when (style) {
        ResolvedPlatformStyle.Desktop -> CommonScaffold.SideParPosition.FullHeight
        ResolvedPlatformStyle.Mobile,
        ResolvedPlatformStyle.Web,
            -> CommonScaffold.SideParPosition.BelowTopAppBar
    }

    CompositionLocalProvider(
        LocalPlatformStyle provides style,
        LocalNavigationData provides navigationData,
        LocalPlatformSpecifics provides platformSpecifics,
    ) {
        val mainMenuItems = when (style) {
            ResolvedPlatformStyle.Mobile -> CommonScaffold.getMobileMenuItems(
                navigationData.pageSettings,
                navigationData.additionalActionItems.map { it.toMenuItem() }
            )

            ResolvedPlatformStyle.Desktop,
            ResolvedPlatformStyle.Web,
                -> ToolbarMainMenuItems()
        }
        CompositionLocalProvider(
            LocalToolbarMainMenuItems provides mainMenuItems
        ) {
            Scaffold(
                modifier = modifier,
                topBar = {
                    if (sidebarPosition == CommonScaffold.SideParPosition.BelowTopAppBar)
                        topBar(screen)
                },
                bottomBar = { bottomBar(navigationStyle.value, screen) },
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
                    sideBar(navigationStyle.value, screen)
                    Column {
                        if (sidebarPosition == CommonScaffold.SideParPosition.FullHeight)
                            topBar(screen)
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun CommonTopBar(
    screen: INavScreen,
    content: @Composable (screen: INavScreen) -> Unit = { it.Toolbar() },
) {
    val style = LocalPlatformStyle.current
    val navigationData = LocalNavigationData.current
    val platformSpecifics = LocalPlatformSpecifics.current
    SharedToolbarContainer {
        when (style) {
            ResolvedPlatformStyle.Web -> {
                // TODO: eine custom toolbar geht in Web nicht...
                WebToolbar(
                    navigationItems = if (platformSpecifics.web.disableBottomAndSideBar) navigationData.mainPages.map { it.toNavItem() } else emptyList(),
                    menuItems = CommonScaffold.getWebMenuItems(
                        pageSettings = navigationData.pageSettings
                    )
                )
            }

            ResolvedPlatformStyle.Mobile,
            ResolvedPlatformStyle.Desktop,
                -> {
                content(screen)
            }
        }
    }
}

@Composable
fun CommonBottomBar(
    modifier: Modifier = Modifier,
) {
    val style = LocalPlatformStyle.current
    val navigationData = LocalNavigationData.current
    val platformSpecifics = LocalPlatformSpecifics.current
    when (style) {
        ResolvedPlatformStyle.Web -> {
            if (platformSpecifics.web.disableBottomAndSideBar) {
                // keine Bottom Bar, alles in WebToolbar
            } else {
                MobileNavigationBar(
                    items = navigationData.mainPages.map { it.toNavItem() },
                    modifier = modifier,
                    alwaysShowLabel = navigationData.showLabels(NavigationStyle.Bottom),
                    showForSingleItem = navigationData.showForSingleItem(NavigationStyle.Bottom)
                )
            }
        }

        ResolvedPlatformStyle.Mobile -> {
            MobileNavigationBar(
                items = navigationData.mainPages.map { it.toNavItem() },
                modifier = modifier,
                alwaysShowLabel = navigationData.showLabels(NavigationStyle.Bottom),
                showForSingleItem = navigationData.showForSingleItem(NavigationStyle.Bottom)
            )
        }

        ResolvedPlatformStyle.Desktop -> {
            val menu = rememberMenuState()
            DesktopNavigationBottom(
                modifier = modifier,
                items = navigationData.mainPages.map { it.toNavItem() } + listOf(
                    NavItemPopupMenu(
                        title = stringResource(Res.string.menu_more),
                        icon = Icons.Default.MoreVert.toIconComposable(),
                        state = menu
                    ) {
                        PopupMenu(state = menu) {
                            navigationData.additionalActionItems.forEach {
                                it.PopupMenuItem(this)
                            }
                            if (navigationData.additionalActionItems.isNotEmpty()) {
                                MenuSeparator(text = stringResource(Res.string.menu_settings))
                            }
                            navigationData.pageSettings.PopupMenuItem(this)
                        }
                    }
                ),
                alwaysShowLabel = navigationData.showLabels(NavigationStyle.Bottom),
                showForSingleItem = navigationData.showForSingleItem(NavigationStyle.Bottom)
            )
        }
    }
}

@Composable
fun CommonSideBar(
    modifier: Modifier = Modifier,
) {
    val style = LocalPlatformStyle.current
    val navigationData = LocalNavigationData.current
    val platformSpecifics = LocalPlatformSpecifics.current
    when (style) {
        ResolvedPlatformStyle.Web -> {
            if (platformSpecifics.web.disableBottomAndSideBar) {
                // keine Side Bar, alles in WebToolbar
            } else {
                MobileNavigationRail(
                    items = navigationData.mainPages.map { it.toNavItem() },
                    modifier = modifier,
                    alwaysShowLabel = navigationData.showLabels(NavigationStyle.Left),
                    showForSingleItem = navigationData.showForSingleItem(NavigationStyle.Left)
                )
            }
        }

        ResolvedPlatformStyle.Mobile -> {
            MobileNavigationRail(
                items = navigationData.mainPages.map { it.toNavItem() },
                modifier = modifier,
                alwaysShowLabel = navigationData.showLabels(NavigationStyle.Left),
                showForSingleItem = navigationData.showForSingleItem(NavigationStyle.Left)
            )
        }

        ResolvedPlatformStyle.Desktop -> {
            DesktopNavigationRail(
                modifier = modifier,
                itemsTop = navigationData.mainPages.map { it.toNavItem() } +
                        if (platformSpecifics.desktop.showAdditionalItemsAtBottomIfRail)
                            emptyList()
                        else {
                            listOf(navigationData.pageSettings.toNavItem()) +
                                    listOf(NavItemRegion(stringResource(Res.string.menu_settings))) +
                                    navigationData.additionalActionItems.map { it.toNavItem() }
                        },
                itemsBottom = if (platformSpecifics.desktop.showAdditionalItemsAtBottomIfRail) {
                    navigationData.additionalActionItems.map { it.toNavItem() } +
                            listOf(NavItemRegion(stringResource(Res.string.menu_settings))) +
                            listOf(navigationData.pageSettings.toNavItem())
                } else
                    emptyList(),
                showForSingleItem = navigationData.showForSingleItem(NavigationStyle.Left),
                expandable = platformSpecifics.desktop.isRailExpandable,
                navigationExpanded = platformSpecifics.desktop.isRailExpanded
            )
        }
    }
}
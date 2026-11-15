package com.michaelflisar.toolbox.app

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.savedstate.serialization.SavedStateConfiguration
import com.michaelflisar.composechangelog.rememberChangelogState
import com.michaelflisar.composechangelog.ui.ChangelogDialog
import com.michaelflisar.composepreferences.styled.layout.PreferenceScreen
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveInfo
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveScaffold
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveScaffoldState
import com.michaelflisar.composestyled.classes.StyledNavigationMode
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.platform.PlatformDefaults
import com.michaelflisar.composestyled.platform.SystemBarStyleEffect
import com.michaelflisar.composestyled.platform.WindowMode
import com.michaelflisar.composestyled.platform.currentPlatform
import com.michaelflisar.composestyled.platform.rememberWindowEnvironment
import com.michaelflisar.composestyled.platform.systemBarIconStyleFor
import com.michaelflisar.composestyled.ui.components.StyledBottomNavigationBar
import com.michaelflisar.composestyled.ui.components.StyledExpandableTopBar
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.components.StyledIconButton
import com.michaelflisar.composestyled.ui.components.StyledNavigationItem
import com.michaelflisar.composestyled.ui.components.StyledSideNavigation
import com.michaelflisar.composestyled.ui.components.StyledSnackbarDuration
import com.michaelflisar.composestyled.ui.components.StyledSnackbarHost
import com.michaelflisar.composestyled.ui.components.StyledSnackbarHostState
import com.michaelflisar.composestyled.ui.components.StyledSnackbarResult
import com.michaelflisar.composestyled.ui.components.StyledTopBar
import com.michaelflisar.composestyled.ui.icons.StyledIcons
import com.michaelflisar.composestyled.ui.layout.StyledColumn
import com.michaelflisar.composestyled.ui.layout.StyledDialog
import com.michaelflisar.composestyled.ui.layout.StyledDialog.ExpandToFullScreen
import com.michaelflisar.composestyled.ui.layout.StyledScaffoldDefaults
import com.michaelflisar.composestyled.ui.layout.StyledSheet
import com.michaelflisar.composestyled.ui.layout.StyledSheet.Value
import com.michaelflisar.composestyled.ui.layout.rememberStyledSheetState
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.specials.StyledTranslatableContent
import com.michaelflisar.composestyled.ui.state.rememberDialogState
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateWithLifecycleNotNull
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.NavScreenDefaults
import com.michaelflisar.toolbox.app.features.navigation.PlatformNavigationBackHandler
import com.michaelflisar.toolbox.app.features.navigation.RegisterNavigationBackHandler
import com.michaelflisar.toolbox.app.features.navigation.ToolbarConfig
import com.michaelflisar.toolbox.app.features.navigation.display.MainNavDisplay
import com.michaelflisar.toolbox.app.features.navigation.manager.LocalMainBackStackManager
import com.michaelflisar.toolbox.app.features.navigation.manager.NavigationBackPriority
import com.michaelflisar.toolbox.app.features.navigation.manager.rememberMainBackStackManager
import com.michaelflisar.toolbox.app.features.debug.DebugOverlay
import com.michaelflisar.toolbox.app.features.preferences.rememberThemeConfig
import com.michaelflisar.toolbox.app.features.theme.themeProvider
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.toast.ToastDuration
import com.michaelflisar.toolbox.app.features.toast.showToast
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.button_close
import com.michaelflisar.toolbox.core.resources.menu_settings
import com.michaelflisar.toolbox.core.resources.settings_changelog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

val LocalAppActions =
    compositionLocalOf<AppActions> {
        error("AppActions is not available outside MainAppScaffold")
    }

/** Standard slot contents that callers can reuse inside their own scaffold-slot wrappers. */
object MainAppScaffoldDefaults {

    @Composable
    fun TopAppBar(
        toolbarConfig: ToolbarConfig?,
        topAppBarColor: @Composable (() -> Color)? = null,
        topAppBarContentColor: @Composable (() -> Color)? = null,
    ) {
        if (toolbarConfig == null) return

        val colors = resolveTopAppBarColors(topAppBarColor, topAppBarContentColor)
        val backStackManager = LocalMainBackStackManager.current
        val navigationIcon: @Composable () -> Unit = {
            MainAppNavigationIcon(
                canNavigateBack = backStackManager.canGoBack,
                onBack = backStackManager::onBack,
            )
        }
        val titleContent: @Composable () -> Unit = {
            MainAppTitleContent(
                title = toolbarConfig.title,
                subtitle = toolbarConfig.subtitle,
            )
        }
        val actionsContent: @Composable RowScope.() -> Unit = {
            MainAppActionsContent(actions = toolbarConfig.actions)
        }

        if (toolbarConfig.scrollBehavior != null) {
            StyledExpandableTopBar(
                behavior = toolbarConfig.scrollBehavior,
                navigationIcon = navigationIcon,
                title = titleContent,
                actions = actionsContent,
                containerColor = colors.container,
                contentColor = colors.content,
            )
        } else {
            StyledTopBar(
                navigationIcon = navigationIcon,
                title = titleContent,
                actions = actionsContent,
                containerColor = colors.container,
                contentColor = colors.content,
            )
        }
    }

    @Composable
    fun BottomNavigation(
        items: List<StyledNavigationItem<NavScreen>>,
        selectedKey: NavScreen,
        onItemClick: (NavScreen) -> Unit,
        containerColor: Color = StyledTheme.colorScheme.surface,
        contentColor: Color = StyledTheme.contentColorFor(containerColor),
        indicatorTint: Color = StyledTheme.colorScheme.primary,
    ) {
        StyledBottomNavigationBar(
            modifier = Modifier.fillMaxWidth(),
            items = items,
            selectedKey = selectedKey,
            containerColor = containerColor,
            contentColor = contentColor,
            indicatorTint = indicatorTint,
            onItemClick = { selected -> onItemClick(selected.key) },
        )
    }

    @Composable
    fun SideNavigation(
        items: List<StyledNavigationItem<NavScreen>>,
        selectedKey: NavScreen,
        mode: StyledNavigationMode.Side,
        expanded: MutableState<Boolean>,
        onItemClick: (NavScreen) -> Unit,
        title: @Composable () -> Unit = { StyledText(AppSetup.get().appData.name) },
        containerColor: Color = StyledTheme.colorScheme.surface,
        contentColor: Color = StyledTheme.contentColorFor(containerColor),
        indicatorTint: Color = StyledTheme.colorScheme.primary,
    ) {
        StyledSideNavigation(
            items = items,
            selectedKey = selectedKey,
            onItemClick = { selected -> onItemClick(selected.key) },
            mode = mode.mode,
            expanded = expanded,
            containerColor = containerColor,
            contentColor = contentColor,
            indicatorTint = indicatorTint,
            title = title,
        )
    }

    @Composable
    fun Content(
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
        banner: @Composable () -> Unit = {},
        transitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
            NavScreenDefaults.crossfadeTransitionSpec(),
        popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
            NavScreenDefaults.crossfadeTransitionSpec(),
        predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform =
            NavScreenDefaults.crossfadePredictivePopTransitionSpec(),
    ) {
        val backStackManager = LocalMainBackStackManager.current
        StyledColumn(modifier = Modifier.padding(paddingValues)) {
            MainNavDisplay(
                manager = backStackManager,
                modifier = Modifier.weight(1f),
                transitionSpec = transitionSpec,
                popTransitionSpec = popTransitionSpec,
                predictivePopTransitionSpec = predictivePopTransitionSpec,
                content = { screen ->
                    screen.Content(
                        paddingValues = PaddingValues(),
                        adaptiveInfo = adaptiveInfo,
                    )
                },
            )
            banner()
        }
    }
}

private data class MainAppTopAppBarColors(
    val container: Color,
    val content: Color,
)

@Composable
private fun resolveTopAppBarColors(
    topAppBarColor: @Composable (() -> Color)?,
    topAppBarContentColor: @Composable (() -> Color)?,
): MainAppTopAppBarColors {
    val toolbarStyle = if (AppSetup.get().themeSupport.supportToolbarStyles) {
        AppSetup.get().prefs.toolbarStyle.collectAsStateNotNull().value
    } else {
        null
    }
    val requestedTopBarColor = topAppBarColor?.invoke()
    val requestedTopBarContentColor = topAppBarContentColor?.invoke()
    val containerColor = requestedTopBarColor ?: when (toolbarStyle) {
        ToolbarStyle.Primary -> StyledTheme.colorScheme.primary
        ToolbarStyle.Background -> StyledTheme.colorScheme.background
        ToolbarStyle.Black -> Color.Black
        null -> StyledTheme.colorScheme.surface
    }
    val contentColor = requestedTopBarContentColor ?: when {
        requestedTopBarColor != null -> StyledTheme.contentColorFor(containerColor)
        toolbarStyle == ToolbarStyle.Primary -> StyledTheme.colorScheme.onPrimary
        toolbarStyle == ToolbarStyle.Background -> StyledTheme.colorScheme.onBackground
        toolbarStyle == ToolbarStyle.Black -> Color.White
        else -> StyledTheme.contentColorFor(containerColor)
    }
    return MainAppTopAppBarColors(containerColor, contentColor)
}

@Immutable
data class AppActions(
    val scope: CoroutineScope,
    val showChangelog: () -> Unit,
    val showProVersionDialog: () -> Unit,
    val showSettingsDialog: () -> Unit,
    internal val snackbarHostState: StyledSnackbarHostState,
) {
    private val pending: ArrayList<Job> = ArrayList()

    fun showSnackbar(
        info: String,
        actionLabel: String? = null,
        cancelAllPending: Boolean = true,
        duration: StyledSnackbarDuration = StyledSnackbarDuration.Short,
        scope: CoroutineScope = this.scope,
        onResult: (StyledSnackbarResult) -> Unit = {},
    ) {
        if (cancelAllPending) {
            pending.forEach { it.cancel() }
            pending.clear()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
        pending += scope.launch(Dispatchers.Main) {
            val result = snackbarHostState.showSnackbar(info, actionLabel, duration = duration)
            onResult(result)
        }
    }

    fun showToast(
        info: String,
        duration: ToastDuration = ToastDuration.Short,
        scope: CoroutineScope = this.scope,
    ) {
        if (showToast != null) {
            scope.launch(Dispatchers.Main) {
                showToast?.invoke(info, duration)
            }
            return
        }
        showSnackbar(
            info = info,
            duration = when (duration) {
                ToastDuration.Short -> StyledSnackbarDuration.Short
                ToastDuration.Long -> StyledSnackbarDuration.Long
            },
            scope = scope
        )
    }
}

/**
 * Displays one independently saved Nav3 stack per top-level navigation item. The caller supplies
 * the serializer configuration for the app's open set of [NavScreen] key types. Color overrides
 * are composable lambdas so they are evaluated inside [StyledTheme]. Forward history is enabled
 * by default and can be disabled for the whole app. Inactive top-level stacks are preserved unless
 * [resetInactiveNavigationStacksOnSelection] is enabled.
 * The [topAppBar], [bottomNav], [sideNav], and [content] slots provide the scaffold UI explicitly.
 * Use [MainAppScaffoldDefaults] to reuse the standard bar, navigation, and Nav3 content. A custom
 * [content] slot receives the scaffold padding and adaptive layout information. Set
 * [showTopAppBarWithoutToolbar] to display the top-app-bar slot even when the current screen has no
 * toolbar configuration or selection content.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainAppScaffold(
    navigationItems: List<StyledNavigationItem<NavScreen>>,
    backStackConfiguration: SavedStateConfiguration,
    startDestination: NavScreen? = null,
    toolbar: @Composable () -> Unit = {},
    topAppBarColor: @Composable (() -> Color)? = null,
    topAppBarContentColor: @Composable (() -> Color)? = null,
    scaffoldColor: @Composable (() -> Color)? = null,
    bottomNavColor: @Composable (() -> Color)? = null,
    settings: @Composable ((paddingValues: PaddingValues) -> Unit)? = null,
    forwardHistoryEnabled: Boolean = true,
    resetInactiveNavigationStacksOnSelection: Boolean = false,
    showTopAppBarWithoutToolbar: Boolean = false,
    topAppBar: @Composable (ToolbarConfig?) -> Unit,
    bottomNav: @Composable (
        items: List<StyledNavigationItem<NavScreen>>,
        selectedKey: NavScreen,
        onItemClick: (NavScreen) -> Unit,
    ) -> Unit,
    sideNav: @Composable (
        items: List<StyledNavigationItem<NavScreen>>,
        selectedKey: NavScreen,
        mode: StyledNavigationMode.Side,
        expanded: MutableState<Boolean>,
        onItemClick: (NavScreen) -> Unit,
    ) -> Unit,
    content: @Composable (
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    ) -> Unit,
) {
    val hostPlatform = currentPlatform()
    val provider = themeProvider()
    val themeConfig = rememberThemeConfig(provider)

    val scope = rememberCoroutineScope()

    StyledTheme(
        platform = hostPlatform,
        provider = provider,
        config = themeConfig,
        behavior = StyledTheme.Behavior(
            showScrollbars = PlatformDefaults.showScrollbars(hostPlatform),
        ),
        customization = {
            // Apply component overrides and additional styled components here.
        }
    ) {
        val mainBackStackManager = rememberMainBackStackManager(
            navigationKeys = navigationItems.map { it.key },
            backStackConfiguration = backStackConfiguration,
            startDestination = startDestination,
            forwardHistoryEnabled = forwardHistoryEnabled,
            resetInactiveNavigationStacksOnSelection = resetInactiveNavigationStacksOnSelection,
        )
        val activeBackStack = mainBackStackManager.activeBackStack

        CompositionLocalProvider(
            LocalMainBackStackManager provides mainBackStackManager,
        ) {
            val activeScreen = activeBackStack.lastOrNull() as? NavScreen
                ?: error("The active Nav3 back stack must end with a NavScreen key.")
            val toolbarConfig = key(activeScreen) { activeScreen.Toolbar() }
            val selectionContent = key(activeScreen) { activeScreen.SelectionContent() }
            val showToolbar =
                showTopAppBarWithoutToolbar || toolbarConfig != null || selectionContent != null
            val showNavigation = navigationItems.isNotEmpty()
            val topBarColor = resolveTopAppBarColors(
                topAppBarColor,
                topAppBarContentColor,
            ).container
            val actualScaffoldColor = scaffoldColor?.invoke() ?: StyledTheme.colorScheme.surface
            val actualBottomNavColor = bottomNavColor?.invoke() ?: StyledTheme.colorScheme.surface

            val windowEnvironment = rememberWindowEnvironment()
            val scaffoldState = remember { StyledAdaptiveScaffoldState() }
            val adaptiveNavigationMode = scaffoldState.navigationMode

            if (windowEnvironment.mode == WindowMode.Normal && adaptiveNavigationMode != null) {
                SystemBarStyleEffect(
                    statusBar = systemBarIconStyleFor(
                        color = if (showToolbar) topBarColor else actualScaffoldColor,
                        background = actualScaffoldColor,
                    ),
                    navigationBar = if (showNavigation && adaptiveNavigationMode.isBottom) {
                        systemBarIconStyleFor(actualBottomNavColor, actualScaffoldColor)
                    } else {
                        systemBarIconStyleFor(actualScaffoldColor, actualScaffoldColor)
                    },
                )
            }

            val sideNavigationExpanded = rememberSaveable { mutableStateOf(true) }
            val snackbarHostState = remember { StyledSnackbarHostState() }

            PlatformNavigationBackHandler(
                navigationState = mainBackStackManager.navigationState,
                replaceNavigationState = mainBackStackManager.replaceNavigationState,
                onMouseForward = mainBackStackManager::onMouseForward,
                onBrowserHistoryStateChanged = mainBackStackManager::onBrowserHistoryStateChanged,
            )

            val scrollBehavior = toolbarConfig?.scrollBehavior

            val lastShownVersion = AppSetup.get().prefs.lastAppVersion.asMutableStateNotNull()
            val changelogState = AppSetup.get().changelogSetup?.let {
                rememberChangelogState(
                    setup = it,
                    lastShownVersion = lastShownVersion.value.toInt(),
                    saveLastShownVersion = { lastShownVersion.value = it.toLong() },
                )
            }

            val dialogSettings = rememberDialogState()
            val dialogProVersion = rememberDialogState()
            val appActions = remember {
                AppActions(
                    scope = scope,
                    showChangelog = { changelogState?.show() },
                    showProVersionDialog = { dialogProVersion.show() },
                    showSettingsDialog = { dialogSettings.show() },
                    snackbarHostState = snackbarHostState
                )
            }

            CompositionLocalProvider(
                LocalAppActions provides appActions,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    StyledColumn {
                        toolbar()
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            StyledAdaptiveScaffold(
                                platform = hostPlatform,
                                state = scaffoldState,
                                sideNavigationExpanded = sideNavigationExpanded,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .then(
                                        if (scrollBehavior != null) {
                                            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                                        } else {
                                            Modifier
                                        },
                                    ),
                                topBar = {
                                    if (showToolbar) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .zIndex(1f)
                                        ) {
                                            topAppBar(toolbarConfig)
                                            selectionContent?.invoke()
                                        }
                                    }
                                },
                                navigation = { navigationMode ->
                                    if (showNavigation) {
                                        val selectedKey =
                                            navigationItems[mainBackStackManager.selectedNavigationIndex].key
                                        val onItemClick: (NavScreen) -> Unit = { selected ->
                                            mainBackStackManager.selectNavigationItem(selected)
                                        }
                                        when (navigationMode) {
                                            StyledNavigationMode.Bottom -> {
                                                bottomNav(
                                                    navigationItems,
                                                    selectedKey,
                                                    onItemClick,
                                                )
                                            }

                                            is StyledNavigationMode.Side -> {
                                                sideNav(
                                                    navigationItems,
                                                    selectedKey,
                                                    navigationMode,
                                                    sideNavigationExpanded,
                                                    onItemClick,
                                                )
                                            }
                                        }
                                    }
                                },
                                snackbarHost = {
                                    StyledSnackbarHost(hostState = snackbarHostState)
                                },
                                contentWindowInsets = StyledScaffoldDefaults.contentWindowInsets,
                                containerColor = actualScaffoldColor,
                                content = { paddingValues, adaptiveInfo ->
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        content(paddingValues, adaptiveInfo)
                                    }

                                    // 2) Settings Dialog
                                    if (settings != null) {
                                        val style = StyledDialog.Style.Fullscreen(
                                            animation = StyledDialog.FullscreenAnimation.Slide(),
                                            expandableHeader = StyledDialog.Style.Fullscreen.ExpandableHeader()
                                        )
                                        //val style2 = StyledDialog.Style.BottomSheet(
                                        //    options = StyledDialog.SheetOptions(
                                        //        expandToFullScreen = ExpandToFullScreen.Enabled(
                                        //            allowExit = false
                                        //        ),
                                        //        peekHeightFraction = .8f
                                        //    )
                                        //)
                                        StyledDialog(
                                            state = dialogSettings,
                                            style = style,
                                            title = {
                                                StyledText(stringResource(Res.string.menu_settings))
                                            },
                                            buttons = StyledDialog.Buttons(
                                                negative = StyledDialog.Button(
                                                    label = stringResource(Res.string.button_close),
                                                    icon = StyledIcons.Close
                                                )
                                            ),
                                        ) { paddingValues ->
                                            settings(paddingValues)
                                        }
                                    }

                                    // 3) Changelog Dialog
                                    if (changelogState != null) {
                                        ChangelogDialog(
                                            state = changelogState,
                                            setup = AppSetup.get().changelogSetup!!,
                                            style = StyledDialog.Style.Dialog(),
                                            title = {
                                                StyledText(stringResource(Res.string.settings_changelog))
                                            },
                                            buttons = StyledDialog.Buttons(
                                                negative = StyledDialog.Button(
                                                    label = stringResource(Res.string.button_close),
                                                    icon = StyledIcons.Close
                                                )
                                            ),
                                        )
                                    }

                                    // 4) Pro Version Dialog
                                    ProVersionManager.Paywall(dialogProVersion)
                                }
                            )
                        }
                    }
                    val showDebugDrawer by AppSetup.get().debug.prefs.showDebugDrawer.collectAsStateWithLifecycleNotNull()
                    LaunchedEffect(showDebugDrawer) {
                        if (!showDebugDrawer) {
                            mainBackStackManager.debugDrawerExpanded.value = false
                        }
                    }
                    if (showDebugDrawer) {
                        val expanded = mainBackStackManager.debugDrawerExpanded
                        val sheetState = rememberStyledSheetState(
                            initialValue = if (expanded.value) Value.Expanded else Value.Collapsed,
                        )
                        LaunchedEffect(expanded.value) {
                            sheetState.set(
                                if (expanded.value) Value.Expanded else Value.Collapsed,
                            )
                        }
                        LaunchedEffect(sheetState, expanded) {
                            snapshotFlow { sheetState.currentValue }.collect {
                                expanded.value = it == Value.Expanded
                            }
                        }
                        RegisterNavigationBackHandler(
                            enabled = expanded.value,
                            canNavigateBack = expanded.value,
                            priority = NavigationBackPriority.Overlay,
                            owner = null,
                            onBack = {
                                expanded.value = false
                                true
                            },
                        )
                        StyledSheet(
                            state = sheetState,
                            edge = StyledSheet.Edge.End
                        ) {
                            // setup
                            val filter = MainAppSettings.filter
                            val preferenceState = MainAppSettings.preferenceState
                            val preferenceStyle = MainAppSettings.preferenceStyle

                            // preferences
                            PreferenceScreen(
                                modifier = Modifier.weight(1f),
                                state = preferenceState,
                                filter = filter,
                                style = preferenceStyle
                            ) {
                                AppSetup.get().debug.sideSheet(expanded)
                            }
                        }
                    }
                    BackHandler(
                        enabled = mainBackStackManager.hasEnabledOverlayBackHandler,
                        onBack = mainBackStackManager::onBack,
                    )
                    DebugOverlay()
                }
            }
        }
    }
}

@Stable
class MainAppSheetState internal constructor(
    private val scope: CoroutineScope,
    private val savedVisibility: MutableState<Boolean>,
    private val savedProgress: MutableState<Float>,
) {
    private var animationJob: Job? = null
    private var animationGeneration = 0
    private var animationTarget: Float? = null

    val isVisible: Boolean
        get() = savedVisibility.value

    val progress: Float
        get() = savedProgress.value

    val isOpen: Boolean
        get() = isVisible && progress >= 1f

    val isOpening: Boolean
        get() = animationJob?.isActive == true && animationTarget == 1f

    fun open() {
        if (!isVisible) {
            cancelAnimation()
            savedVisibility.value = true
            savedProgress.value = 0f
        }
        if (!isOpen && !isOpening) {
            animateTo(target = 1f, hideWhenClosed = false)
        }
    }

    fun close(hideWhenClosed: Boolean = false) {
        if (isVisible && (progress > 0f || isOpening)) {
            animateTo(target = 0f, hideWhenClosed = hideWhenClosed)
        } else if (isVisible) {
            if (hideWhenClosed) {
                savedVisibility.value = false
            }
        }
    }

    fun toggle(hideWhenClosed: Boolean = false) {
        if (isOpen || isOpening) close(hideWhenClosed) else open()
    }

    internal fun cancelAnimation() {
        animationJob?.cancel()
        animationJob = null
        animationGeneration++
        animationTarget = null
    }

    internal fun setProgress(value: Float) {
        savedProgress.value = value.coerceIn(0f, 1f)
    }

    internal fun animateTo(target: Float, hideWhenClosed: Boolean) {
        cancelAnimation()
        val generation = animationGeneration
        animationTarget = target
        animationJob = scope.launch {
            try {
                animate(
                    initialValue = progress,
                    targetValue = target,
                    animationSpec = tween(durationMillis = 250),
                ) { value, _ ->
                    setProgress(value)
                }
                if (hideWhenClosed) {
                    savedVisibility.value = false
                }
            } finally {
                if (animationGeneration == generation) {
                    animationTarget = null
                    animationJob = null
                }
            }
        }
    }
}

@Composable
fun rememberMainAppSheetState(): MainAppSheetState {
    val scope = rememberCoroutineScope()
    val savedVisibility = rememberSaveable { mutableStateOf(false) }
    val savedProgress = rememberSaveable { mutableStateOf(0f) }
    return remember(scope, savedVisibility, savedProgress) {
        MainAppSheetState(scope, savedVisibility, savedProgress)
    }
}

/**
 * Adds a draggable, expandable app sheet and floating header toolbar to [MainAppScaffold].
 * The header and sheet content receive the same [MainAppSheetState] for observing progress and
 * opening or closing the sheet.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainAppScaffoldWithAppSheet(
    navigationItems: List<StyledNavigationItem<NavScreen>>,
    backStackConfiguration: SavedStateConfiguration,
    sheetState: MainAppSheetState = rememberMainAppSheetState(),
    startDestination: NavScreen? = null,
    toolbar: @Composable () -> Unit = {},
    topAppBarColor: @Composable (() -> Color)? = null,
    topAppBarContentColor: @Composable (() -> Color)? = null,
    scaffoldColor: @Composable (() -> Color)? = null,
    bottomNavColor: @Composable (() -> Color)? = null,
    settings: @Composable ((paddingValues: PaddingValues) -> Unit)? = null,
    forwardHistoryEnabled: Boolean = true,
    resetInactiveNavigationStacksOnSelection: Boolean = false,
    showTopAppBarWithoutToolbar: Boolean = false,
    topAppBar: @Composable (ToolbarConfig?) -> Unit,
    bottomNav: @Composable (
        items: List<StyledNavigationItem<NavScreen>>,
        selectedKey: NavScreen,
        onItemClick: (NavScreen) -> Unit,
    ) -> Unit,
    sideNav: @Composable (
        items: List<StyledNavigationItem<NavScreen>>,
        selectedKey: NavScreen,
        mode: StyledNavigationMode.Side,
        expanded: MutableState<Boolean>,
        onItemClick: (NavScreen) -> Unit,
    ) -> Unit,
    sheetToolbar: @Composable (MainAppSheetState) -> Unit,
    sheetContent: @Composable (MainAppSheetState) -> Unit,
    content: @Composable (
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    ) -> Unit,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val velocityThreshold = with(density) { 400.dp.toPx() }
    val headerInsetPx = with(density) { 16.dp.toPx() }
    val statusBarTopPx = WindowInsets.statusBars.getTop(density).toFloat()
    val bottomSystemInsetPx = WindowInsets.safeDrawing.getBottom(density)
    var scaffoldSize by remember { mutableStateOf(IntSize.Zero) }
    var scaffoldTopPx by remember { mutableStateOf(0f) }
    var bottomNavigationHeightPx by remember { mutableStateOf(0) }
    var headerHeightPx by remember(density) {
        mutableStateOf(with(density) { 56.dp.roundToPx() })
    }
    val contentStartPxState = remember { mutableStateOf(0f) }
    val contentEndPxState = remember { mutableStateOf(0f) }
    val bottomNavigationInsetPx =
        maxOf(bottomNavigationHeightPx, bottomSystemInsetPx).toFloat()
    val headerStartTopPx =
        scaffoldSize.height - bottomNavigationInsetPx - headerHeightPx
    val headerTravelDistancePx =
        (headerStartTopPx - headerInsetPx - (statusBarTopPx - scaffoldTopPx))
            .coerceAtLeast(0f)
    val headerTopPx =
        headerStartTopPx - headerInsetPx - sheetState.progress * headerTravelDistancePx
    val sheetTopPx =
        headerTopPx + headerHeightPx + headerInsetPx * (1f - sheetState.progress)
    val sheetHeightPx =
        (scaffoldSize.height - sheetTopPx).coerceIn(0f, scaffoldSize.height.toFloat())
    val sheetExpanded = sheetState.isVisible && sheetState.progress >= 1f

    val currentSheetVisible = rememberUpdatedState(sheetState.isVisible)
    val currentSheetProgress = rememberUpdatedState(sheetState.progress)
    val currentHeaderTopPx = rememberUpdatedState(headerTopPx)
    val currentHeaderHeightPx = rememberUpdatedState(headerHeightPx.toFloat())
    val currentHeaderTravelDistancePx = rememberUpdatedState(headerTravelDistancePx)
    val currentOnDragStopped = rememberUpdatedState<(Float) -> Unit>(
        newValue = { velocity ->
            val target = when {
                velocity < -velocityThreshold -> 1f
                velocity > velocityThreshold -> 0f
                sheetState.progress >= 0.5f -> 1f
                else -> 0f
            }
            sheetState.animateTo(target = target, hideWhenClosed = false)
        },
    )

    MainAppScaffold(
        navigationItems = navigationItems,
        backStackConfiguration = backStackConfiguration,
        startDestination = startDestination,
        toolbar = toolbar,
        topAppBarColor = topAppBarColor,
        topAppBarContentColor = topAppBarContentColor,
        scaffoldColor = scaffoldColor,
        bottomNavColor = bottomNavColor,
        settings = settings,
        forwardHistoryEnabled = forwardHistoryEnabled,
        resetInactiveNavigationStacksOnSelection = resetInactiveNavigationStacksOnSelection,
        showTopAppBarWithoutToolbar = showTopAppBarWithoutToolbar,
        topAppBar = { toolbarConfig ->
            StyledTranslatableContent(
                side = StyledTranslatableContent.Side.Top,
                translation = 1f - sheetState.progress,
            ) {
                topAppBar(toolbarConfig)
            }
        },
        bottomNav = { items, selectedKey, onItemClick ->
            StyledTranslatableContent(
                side = StyledTranslatableContent.Side.Bottom,
                translation = 1f - sheetState.progress,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { bottomNavigationHeightPx = it.height },
                ) {
                    bottomNav(items, selectedKey, onItemClick)
                }
            }
        },
        sideNav = { items, selectedKey, mode, expanded, onItemClick ->
            StyledTranslatableContent(
                side = if (layoutDirection == LayoutDirection.Ltr) {
                    StyledTranslatableContent.Side.Left
                } else {
                    StyledTranslatableContent.Side.Right
                },
                translation = 1f - sheetState.progress,
            ) {
                sideNav(items, selectedKey, mode, expanded, onItemClick)
            }
        },
        content = { paddingValues, adaptiveInfo ->
            LaunchedEffect(adaptiveInfo.value.navigationMode) {
                if (adaptiveInfo.value.navigationMode != StyledNavigationMode.Bottom) {
                    bottomNavigationHeightPx = 0
                }
            }
            val startPaddingPx = with(density) {
                paddingValues.calculateStartPadding(layoutDirection).toPx()
            }
            val endPaddingPx = with(density) {
                paddingValues.calculateEndPadding(layoutDirection).toPx()
            }
            val contentLeftPx =
                if (layoutDirection == LayoutDirection.Ltr) startPaddingPx else endPaddingPx
            val contentEndPaddingPx =
                if (layoutDirection == LayoutDirection.Ltr) endPaddingPx else startPaddingPx
            val contentRightPx = scaffoldSize.width - contentEndPaddingPx
            val contentWidthPx = (contentRightPx - contentLeftPx).coerceAtLeast(0f)
            SideEffect {
                contentStartPxState.value = contentLeftPx
                contentEndPxState.value = contentRightPx
            }

            RegisterNavigationBackHandler(
                enabled = sheetExpanded,
                canNavigateBack = sheetExpanded,
                priority = NavigationBackPriority.Overlay,
                owner = null,
                onBack = {
                    sheetState.animateTo(target = 0f, hideWhenClosed = false)
                    true
                },
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            if (!currentSheetVisible.value) {
                                return@awaitEachGesture
                            }
                            val headerTop = currentHeaderTopPx.value
                            val headerBottom = headerTop + currentHeaderHeightPx.value
                            if (
                                down.position.x !in
                                contentStartPxState.value..contentEndPxState.value ||
                                down.position.y !in headerTop..headerBottom
                            ) {
                                return@awaitEachGesture
                            }

                            val travelDistance = currentHeaderTravelDistancePx.value
                            if (travelDistance <= 0f) {
                                return@awaitEachGesture
                            }
                            val velocityTracker = VelocityTracker()
                            velocityTracker.addPosition(down.uptimeMillis, down.position)
                            var progress = currentSheetProgress.value
                            var dragStarted = false
                            val postSlopChange = awaitVerticalTouchSlopOrCancellation(
                                pointerId = down.id,
                            ) { change, overSlop ->
                                change.consume()
                                velocityTracker.addPosition(change.uptimeMillis, change.position)
                                sheetState.cancelAnimation()
                                dragStarted = true
                                progress = (progress - overSlop / travelDistance).coerceIn(0f, 1f)
                                sheetState.setProgress(progress)
                            }
                            if (postSlopChange != null && dragStarted) {
                                verticalDrag(down.id) { change ->
                                    velocityTracker.addPosition(
                                        change.uptimeMillis,
                                        change.position
                                    )
                                    val delta = change.positionChange().y
                                    if (delta != 0f) {
                                        change.consume()
                                        progress = (progress - delta / travelDistance)
                                            .coerceIn(0f, 1f)
                                        sheetState.setProgress(progress)
                                    }
                                }
                                currentOnDragStopped.value(velocityTracker.calculateVelocity().y)
                            }
                        }
                    }
                    .onGloballyPositioned { coordinates ->
                        scaffoldSize = coordinates.size
                        scaffoldTopPx = coordinates.positionInRoot().y
                    },
            ) {
                content(paddingValues, adaptiveInfo)
                if (sheetState.isVisible) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(contentLeftPx.roundToInt(), sheetTopPx.roundToInt())
                            }
                            .requiredWidth(with(density) { contentWidthPx.toDp() })
                            .height(with(density) { sheetHeightPx.toDp() })
                            .zIndex(1f),
                    ) {
                        sheetContent(sheetState)
                    }
                }
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(contentLeftPx.roundToInt(), headerTopPx.roundToInt())
                        }
                        .requiredWidth(with(density) { contentWidthPx.toDp() })
                        .onSizeChanged { headerHeightPx = it.height }
                        .zIndex(2f),
                ) {
                    AnimatedVisibility(
                        visible = sheetState.isVisible,
                        enter = fadeIn(animationSpec = tween(durationMillis = 200)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 200)),
                    ) {
                        sheetToolbar(sheetState)
                    }
                }
            }
        },
    )
}

// --------------------------
// private composables
// --------------------------

@Composable
private fun MainAppNavigationIcon(
    canNavigateBack: Boolean,
    onBack: () -> Unit,
) {
    AnimatedVisibility(
        visible = canNavigateBack,
        enter = slideInHorizontally { -it } +
                expandHorizontally(expandFrom = Alignment.Start),
        exit = shrinkHorizontally(shrinkTowards = Alignment.Start) +
                slideOutHorizontally { -it },
    ) {
        StyledIconButton(onClick = onBack) {
            StyledIcon(
                icon = Icons.AutoMirrored.Filled.ArrowBack.toIconComposable(),
                contentDescription = "Back",
            )
        }
    }
}

@Composable
private fun MainAppTitleContent(
    title: String,
    subtitle: String?,
) {
    Column {
        Crossfade(
            targetState = title,
            label = "toolbarTitle",
        ) { currentTitle ->
            StyledText(currentTitle)
        }
        val lastSubtitle = remember {
            mutableStateOf(subtitle)
        }
        LaunchedEffect(subtitle) {
            subtitle?.let {
                lastSubtitle.value = it
            }
        }
        AnimatedVisibility(
            visible = subtitle != null,
            enter = slideInVertically { it } +
                    expandVertically(expandFrom = Alignment.Top),
            exit = slideOutVertically { it } +
                    shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Crossfade(
                targetState = subtitle ?: lastSubtitle.value.orEmpty(),
                label = "toolbarSubtitle",
            ) { currentSubtitle ->
                StyledText(
                    currentSubtitle,
                    style = StyledTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun RowScope.MainAppActionsContent(
    actions: @Composable RowScope.() -> Unit,
) {
    val rowScope = this
    Crossfade(
        targetState = actions,
        label = "toolbarActions",
    ) { currentActions ->
        currentActions(rowScope)
    }
}

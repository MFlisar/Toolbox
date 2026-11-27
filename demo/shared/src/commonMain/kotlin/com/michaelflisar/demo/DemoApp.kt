package com.michaelflisar.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.components.StyledIconPlainButton
import com.michaelflisar.composestyled.ui.components.StyledNavigationItem
import com.michaelflisar.composestyled.ui.components.StyledSurface
import com.michaelflisar.composestyled.ui.icons.StyledIcons
import com.michaelflisar.composestyled.ui.primitives.StyledExpandIcon
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.demo.pages.Demo1
import com.michaelflisar.demo.pages.Demo2
import com.michaelflisar.demo.pages.Demo3
import com.michaelflisar.demo.pages.Demo4
import com.michaelflisar.demo.pages.DemoDesktopMenuBar
import com.michaelflisar.demo.pages.NavigationSavedStateConfiguration
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.MainAppScaffold
import com.michaelflisar.toolbox.app.MainAppScaffoldDefaults
import com.michaelflisar.toolbox.app.MainAppScaffoldNavigationOptions
import com.michaelflisar.toolbox.app.MainAppScaffoldWithAppSheet
import com.michaelflisar.toolbox.app.MainAppSettings
import com.michaelflisar.toolbox.app.MainAppTheme
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.rememberMainAppSheetState

internal val LocalOpenDemoSheet = compositionLocalOf<(() -> Unit)?> {
    null
}

object Setup {

    val MainNavigationItems: List<StyledNavigationItem<NavScreen>> =
        listOf(
            StyledNavigationItem(
                key = Demo1,
                label = "Page 1",
                icon = StyledIcons.Home
            ),
            StyledNavigationItem(
                key = Demo2,
                label = "Page 2",
                icon = Icons.Default.VerticalAlignTop.toIconComposable()
            ),
            StyledNavigationItem(
                key = Demo3,
                label = "Demos",
                icon = Icons.Default.Bolt.toIconComposable()
            ),
            StyledNavigationItem(
                key = Demo4,
                label = "Master Detail",
                icon = Icons.Default.ViewColumn.toIconComposable()
            )
        )
}

@Composable
fun DemoApp(
    desktopMenuBar: Boolean = false,
    forwardHistoryEnabled: Boolean = false,
    banner: @Composable () -> Unit = {},
) {
    Shared.Init()
    MainAppTheme {
        //DemoAppSimple(desktopMenuBar, forwardHistoryEnabled, banner)
        DemoAppAdvanced(desktopMenuBar, forwardHistoryEnabled, banner)
    }
}

@Composable
private fun DemoAppSimple(
    desktopMenuBar: Boolean = false,
    forwardHistoryEnabled: Boolean = false,
    banner: @Composable () -> Unit = {},
) {
    val navigationItems = Setup.MainNavigationItems

    val sideNavColor = StyledTheme.colorScheme.primarySurface
    val bottomNavColor = StyledTheme.colorScheme.primarySurface

    MainAppScaffold(
        navigationItems = navigationItems,
        backStackConfiguration = NavigationSavedStateConfiguration,
        navigationOptions = MainAppScaffoldNavigationOptions(
            resetInactiveNavigationStacksOnSelection = true,
        ),
        forwardHistoryEnabled = forwardHistoryEnabled,
        toolbar = { if (desktopMenuBar) DemoDesktopMenuBar() },
        settings = { paddingValues -> MainAppSettings(paddingValues) },
        bottomNavColor = bottomNavColor,
        topAppBar = { toolbarConfig ->
            MainAppScaffoldDefaults.TopAppBar(toolbarConfig = toolbarConfig)
        },
        bottomNav = { items, selectedKey, onItemClick ->

            val containerColor = bottomNavColor
            val contentColor: Color = StyledTheme.contentColorFor(containerColor)
            val indicatorTint: Color = StyledTheme.colorScheme.primary

            MainAppScaffoldDefaults.BottomNavigation(
                items = items,
                selectedKey = selectedKey,
                onItemClick = onItemClick,
                containerColor = containerColor,
                contentColor = contentColor,
                indicatorTint = indicatorTint
            )
        },
        sideNav = { items, selectedKey, mode, expanded, onItemClick ->

            val containerColor = sideNavColor
            val contentColor: Color = StyledTheme.contentColorFor(containerColor)
            val indicatorTint: Color = StyledTheme.colorScheme.primary

            MainAppScaffoldDefaults.SideNavigation(
                items = items,
                selectedKey = selectedKey,
                mode = mode,
                expanded = expanded,
                onItemClick = onItemClick,
                title = { StyledText(AppSetup.get().appData.name) },
                containerColor = containerColor,
                contentColor = contentColor,
                indicatorTint = indicatorTint
            )
        },
        content = { paddingValues, adaptiveInfo ->
            MainAppScaffoldDefaults.Content(
                paddingValues = paddingValues,
                adaptiveInfo = adaptiveInfo,
                banner = banner,
            )
        },
    )
}

@Composable
private fun DemoAppAdvanced(
    desktopMenuBar: Boolean = false,
    forwardHistoryEnabled: Boolean = false,
    banner: @Composable () -> Unit = {},
) {
    val navigationItems = Setup.MainNavigationItems
    val sideNavigationTitle: @Composable () -> Unit = {
        StyledText(AppSetup.get().appData.name)
    }

    val sideNavColor = StyledTheme.colorScheme.primarySurface
    val bottomNavColor = StyledTheme.colorScheme.primarySurface

    val sheetState = rememberMainAppSheetState()

    CompositionLocalProvider(LocalOpenDemoSheet provides sheetState::open) {
        MainAppScaffoldWithAppSheet(
            navigationItems = navigationItems,
            backStackConfiguration = NavigationSavedStateConfiguration,
            sheetState = sheetState,
            navigationOptions = MainAppScaffoldNavigationOptions(
                resetInactiveNavigationStacksOnSelection = true,
            ),
            forwardHistoryEnabled = forwardHistoryEnabled,
            toolbar = { if (desktopMenuBar) DemoDesktopMenuBar() },
            settings = { paddingValues ->
                MainAppSettings(paddingValues)
            },
            bottomNavColor = bottomNavColor,
            showTopAppBarWithoutToolbar = true,
            topAppBar = { toolbarConfig ->
                MainAppScaffoldDefaults.TopAppBar(toolbarConfig)
            },
            bottomNav = { items, selectedKey, onItemClick ->
                val containerColor = bottomNavColor
                val contentColor: Color = StyledTheme.contentColorFor(containerColor)
                val indicatorTint: Color = StyledTheme.colorScheme.primary

                MainAppScaffoldDefaults.BottomNavigation(
                    items = items,
                    selectedKey = selectedKey,
                    onItemClick = onItemClick,
                    containerColor = containerColor,
                    contentColor = contentColor,
                    indicatorTint = indicatorTint
                )
            },
            sideNav = { items, selectedKey, mode, expanded, onItemClick ->
                val containerColor = sideNavColor
                val contentColor: Color = StyledTheme.contentColorFor(containerColor)
                val indicatorTint: Color = StyledTheme.colorScheme.primary

                MainAppScaffoldDefaults.SideNavigation(
                    items = items,
                    selectedKey = selectedKey,
                    mode = mode,
                    expanded = expanded,
                    title = sideNavigationTitle,
                    onItemClick = onItemClick,
                    containerColor = containerColor,
                    contentColor = contentColor,
                    indicatorTint = indicatorTint
                )
            },
            sheetToolbar = { state ->
                DemoAppFloatingBar(
                    modifier = Modifier.fillMaxWidth(),
                    progress = state.progress,
                    toggle = state::toggle,
                    onClose = { state.close(hideWhenClosed = true) }
                )
            },
            sheetContent = { state ->
                DemoAppBottomSheet(
                    modifier = Modifier.fillMaxSize(),
                    onClose = state::close,
                )
            },
            content = { paddingValues, adaptiveInfo ->
                MainAppScaffoldDefaults.Content(
                    paddingValues = paddingValues,
                    adaptiveInfo = adaptiveInfo,
                    banner = banner,
                )
            },
        )
    }
}

@Composable
private fun DemoAppFloatingBar(
    modifier: Modifier,
    progress: Float,
    toggle: () -> Unit,
    onClose: () -> Unit,
) {
    val padding = 16.dp * (1f - progress)
    val containerColor = lerp(
        StyledTheme.colorScheme.primary,
        StyledTheme.colorScheme.background,
        progress,
    )
    val contentColor = StyledTheme.contentColorFor(containerColor)

    StyledSurface(
        modifier = modifier
            .padding(horizontal = padding)
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(padding),
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = 8.dp * (1f - progress),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StyledIconPlainButton(
                onClick = { toggle() },
            ) {
                StyledExpandIcon(
                    expansionProgress = 1f - progress
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                StyledText(if (progress >= 0.5f) "Drag to close" else "Drag to expand")
            }
            StyledIconPlainButton(
                onClick = onClose,
            ) {
                StyledIcon(
                    icon = StyledIcons.Close,
                    contentDescription = "Close"
                )
            }
        }
    }
}

@Composable
private fun DemoAppBottomSheet(
    modifier: Modifier,
    onClose: () -> Unit,
) {
    StyledSurface(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        color = StyledTheme.colorScheme.primarySurface,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            StyledButton(onClick = onClose) {
                StyledText("Close")
            }
        }
    }
}

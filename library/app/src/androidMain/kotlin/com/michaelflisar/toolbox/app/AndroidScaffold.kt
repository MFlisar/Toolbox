package com.michaelflisar.toolbox.app

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
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ComponentActivity
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.toolbox.MyTheme
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.dialogs.ErrorDialogProvider
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigationbar.NavigationBar
import com.michaelflisar.toolbox.app.features.navigationbar.NavigationRail
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.scaffold.NavigationStyle
import com.michaelflisar.toolbox.app.features.scaffold.rememberNavigationStyleAuto
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider
import com.michaelflisar.toolbox.app.features.toolbar.LocalToolbarMainMenuItems
import com.michaelflisar.toolbox.app.features.toolbar.MainMenuItems

@Composable
fun ComponentActivity.AndroidApplication(
    // Navigator
    screen: Screen,
    theme: MyTheme = MyTheme.default(),
    // Content
    content: @Composable (navigator: Navigator) -> Unit,
) {
    ProvideAppLocals{
        AppNavigator(
            screen = screen
        ) { navigator ->
            val appState = rememberAppState()
            AppThemeProvider(theme, this) {
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
}

@Composable
fun AndroidScaffold(
    mainMenuItems: MainMenuItems = MainMenuItems(),
    toolbar: @Composable () -> Unit = {},
    navigationStyle: State<NavigationStyle> = rememberNavigationStyleAuto(),
    navigation: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalToolbarMainMenuItems provides mainMenuItems
    ) {
        ErrorDialogProvider {
            val appState = LocalAppState.current
            val containerColor = MaterialTheme.colorScheme.background
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = toolbar,
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (navigationStyle.value == NavigationStyle.Left)
                        navigation()
                    content()
                }
            }
        }
    }
}

@Composable
fun AndroidNavigation(
    navigationStyle: State<NavigationStyle>,
    items: List<INavItem>,
    modifier: Modifier = Modifier,
    alwaysShowLabel: Boolean = true,
    showForSingleItem: Boolean = false,
) {
    when (navigationStyle.value) {
        NavigationStyle.Left -> {
            AndroidNavigationRail(
                items = items,
                modifier = modifier,
                alwaysShowLabel = alwaysShowLabel,
                showForSingleItem = showForSingleItem
            )
        }

        NavigationStyle.Bottom -> {
            AndroidNavigationBar(
                items = items,
                modifier = modifier,
                alwaysShowLabel = alwaysShowLabel,
                showForSingleItem = showForSingleItem
            )
        }
    }
}

@Composable
fun AndroidNavigationRail(
    items: List<INavItem>,
    modifier: Modifier = Modifier,
    alwaysShowLabel: Boolean = true,
    showForSingleItem: Boolean = false,
) {
    NavigationRail(
        modifier = modifier,
        items = items,
        alwaysShowLabel = alwaysShowLabel,
        showForSingleItem = showForSingleItem
    )
}

@Composable
fun AndroidNavigationBar(
    items: List<INavItem>,
    modifier: Modifier = Modifier,
    alwaysShowLabel: Boolean = true,
    showForSingleItem: Boolean = false,
) {
    NavigationBar(
        modifier = modifier,
        items = items,
        alwaysShowLabel = alwaysShowLabel,
        showForSingleItem = showForSingleItem
    )
}
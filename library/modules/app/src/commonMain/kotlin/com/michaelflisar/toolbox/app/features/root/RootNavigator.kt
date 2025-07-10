package com.michaelflisar.toolbox.app.features.root

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorContent
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState

@Composable
fun RootNavigator(
    screen: Screen,
    onBackPressed: (currentScreen: Screen) -> Boolean = { true },
    content: NavigatorContent = { CurrentScreen() },
) {
    val appState = LocalAppState.current
    val drawerState = LocalDebugDrawerState.current
    Navigator(
        screen = screen,
        onBackPressed = { screen ->
            if (RootBackHandler.navigatorShouldHandlerBackPress(appState, drawerState)) {
                onBackPressed(screen)
            } else {
                false
            }
        },
        content = content
    )
}


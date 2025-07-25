package com.michaelflisar.toolbox.app.features.root

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.NavigatorContent
import com.michaelflisar.toolbox.app.features.appstate.AppState
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator

@Composable
fun RootNavigator(
    appState: AppState,
    screen: Screen,
    setRootLocals: Boolean,
    //onBackPressed: (currentScreen: Screen) -> Boolean = { true },
    content: NavigatorContent,
) {
    RootLocalProvider(appState, setRootLocals) {
        AppNavigator(
            screen = screen,
            onBackPressed = { screen ->
                // does not work reliable (windows + esc key e.g.) => I use the BackHandler instead
                false
            },
            content = content
        )
    }
}


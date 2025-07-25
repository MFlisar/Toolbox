package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorContent
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.navigator.OnBackPressed
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.SlideTransition

@Composable
fun AppNavigator(
    screen: Screen,
    disposeBehavior: NavigatorDisposeBehavior = NavigatorDisposeBehavior(disposeSteps = false),
    onBackPressed: OnBackPressed = { true },
    content: NavigatorContent = { CurrentScreen() },
) {
    Navigator(
        screen = screen,
        disposeBehavior = disposeBehavior,
        onBackPressed = onBackPressed
    ) { navigator ->
        content(navigator)
    }
}

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun AppNavigatorSlideTransition(
    navigator: Navigator,
) {
    SlideTransition(
        navigator = navigator,
        disposeScreenAfterTransitionEnd = true
    )
}

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun AppNavigatorFadeTransition(
    navigator: Navigator,
) {
    FadeTransition(
        navigator = navigator,
        disposeScreenAfterTransitionEnd = true
    )
}
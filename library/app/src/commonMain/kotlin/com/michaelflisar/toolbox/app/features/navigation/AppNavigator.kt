package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorContent
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.ScreenTransition
import cafe.adriel.voyager.transitions.SlideTransition
import com.michaelflisar.toolbox.app.features.backhandlerregistry.NavigatorBackHandler
import com.michaelflisar.toolbox.app.features.toolbar.selection.ResetSelectionToolbarOnScreenChange

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavigator(
    screen: Screen,
    disposeBehavior: NavigatorDisposeBehavior = NavigatorDisposeBehavior(
        disposeSteps = false
    ),
    content: NavigatorContent = { CurrentScreen() },
) {
    Navigator(
        screen = screen,
        disposeBehavior = disposeBehavior,
        onBackPressed = null // wir nutzen unseren eigenen BackHandler weiter unten, der integrierte funktioniert nicht wie gewünscht!
    ) { navigator ->
        ResetSelectionToolbarOnScreenChange()
        content(navigator)
        NavigatorBackHandler(navigator)
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

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun AppNavigatorFadeAndScaleTransition(
    navigator: Navigator,
) {
    val fadeSpec = tween<Float>(
        durationMillis = 220,
        easing = FastOutSlowInEasing
    )
    val enterScaleSpec = tween<Float>(
        durationMillis = 220,
        easing = FastOutSlowInEasing
    )

    ScreenTransition(
        navigator = navigator,
        disposeScreenAfterTransitionEnd = true,
        transition = {
            (fadeIn(animationSpec = fadeSpec) +
                    scaleIn(
                        initialScale = 0.98f,
                        animationSpec = enterScaleSpec
                    )) togetherWith fadeOut(animationSpec = fadeSpec)
        }
    )
}
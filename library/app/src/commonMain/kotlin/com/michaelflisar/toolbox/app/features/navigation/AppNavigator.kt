package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorContent
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.navigator.OnBackPressed
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.ScreenTransition
import cafe.adriel.voyager.transitions.SlideTransition
import com.michaelflisar.toolbox.app.features.toolbar.selection.ResetSelectionToolbarOnScreenChange

@Composable
fun AppNavigator(
    screen: Screen,
    disposeBehavior: NavigatorDisposeBehavior = NavigatorDisposeBehavior(
        disposeSteps = false
    ),
    onBackPressed: OnBackPressed = { screen ->
        // does not work reliable (windows + esc key e.g.) => I use the BackHandler instead
        false
    },
    content: NavigatorContent = { CurrentScreen() },
) {
    Navigator(
        screen = screen,
        disposeBehavior = disposeBehavior,
        onBackPressed = onBackPressed
    ) { navigator ->
        ResetSelectionToolbarOnScreenChange()
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

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun AppNavigatorFadeAndScaleTransition(
    navigator: Navigator,
) {
    val animationSpecScale = tween<Float>(durationMillis = 150, delayMillis = 150, easing = LinearEasing)//spring<Float>(stiffness = Spring.StiffnessMediumLow)
    val animationSpecFade = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)//spring<Float>(stiffness = Spring.StiffnessMedium)
    ScreenTransition(
        navigator = navigator,
        disposeScreenAfterTransitionEnd = true,
        transition = {
            (
                    fadeIn(animationSpec = animationSpecFade) + scaleIn(
                        initialScale = 0.95f,
                        animationSpec = animationSpecScale
                    )
                            togetherWith fadeOut(animationSpec = animationSpecFade) +
                            scaleOut(
                                targetScale = 1.05f,
                                animationSpec = animationSpecScale
                            )
                    )
        }
    )
}
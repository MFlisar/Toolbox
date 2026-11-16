package com.michaelflisar.toolbox.app.features.navigation.display

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.michaelflisar.toolbox.app.features.navigation.LocalNav3ActiveScreen
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.NavScreenDefaults
import com.michaelflisar.toolbox.app.features.navigation.manager.LocalMainBackStackManager
import com.michaelflisar.toolbox.app.features.navigation.manager.MainBackStackManager

@Composable
internal fun MainNavDisplay(
    manager: MainBackStackManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { manager.onBack() },
    transitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
        NavScreenDefaults.crossfadeTransitionSpec(),
    popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
        NavScreenDefaults.crossfadeTransitionSpec(),
    predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform =
        NavScreenDefaults.crossfadePredictivePopTransitionSpec(),
    content: @Composable (NavScreen) -> Unit,
) {
    val navigationIndex = manager.selectedNavigationIndex
    NavDisplay(
        backStack = manager.activeBackStack,
        modifier = modifier,
        onBack = onBack,
        transitionSpec = transitionSpec,
        popTransitionSpec = popTransitionSpec,
        predictivePopTransitionSpec = predictivePopTransitionSpec,
        entryDecorators = listOf(rememberViewModelStoreNavEntryDecorator()),
        entryProvider = { key ->
            val screen = key as? NavScreen
                ?: error("Unsupported Nav3 key in MainNavDisplay: $key")
            NavEntry(
                key = key,
                metadata = screen.navigationMetadata,
            ) {
                manager.ProvideEntryState(screen, navigationIndex) {
                    CompositionLocalProvider(
                        LocalMainBackStackManager provides manager,
                        LocalNav3ActiveScreen provides screen,
                    ) {
                        content(screen)
                    }
                }
            }
        },
    )
}

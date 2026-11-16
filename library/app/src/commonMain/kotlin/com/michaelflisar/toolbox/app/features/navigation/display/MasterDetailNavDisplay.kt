package com.michaelflisar.toolbox.app.features.navigation.display

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveMasterDetailMode
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.NavScreenDefaults
import com.michaelflisar.toolbox.app.features.navigation.manager.LocalMainBackStackManager
import com.michaelflisar.toolbox.app.features.navigation.manager.MasterDetailState

@Composable
internal fun <RootPage : NavScreen, DetailPage : NavKey> MasterDetailNavDisplay(
    manager: MasterDetailState<RootPage, DetailPage>,
    masterDetailMode: StyledAdaptiveMasterDetailMode,
    modifier: Modifier = Modifier.Companion,
    transitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
        NavScreenDefaults.masterDetailTransitionSpec(masterDetailMode),
    popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
        NavScreenDefaults.masterDetailPopTransitionSpec(masterDetailMode),
    predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform =
        NavScreenDefaults.masterDetailPredictivePopTransitionSpec(masterDetailMode),
    rootContent: @Composable () -> Unit = {},
    detailContent: @Composable (DetailPage) -> Unit,
) {
    val mainBackStackManager = LocalMainBackStackManager.current
    NavDisplay(
        backStack = manager.backStack,
        modifier = modifier.clipToBounds(),
        onBack = { mainBackStackManager.onBack() },
        transitionSpec = transitionSpec,
        popTransitionSpec = popTransitionSpec,
        predictivePopTransitionSpec = predictivePopTransitionSpec,
        entryDecorators = listOf(rememberViewModelStoreNavEntryDecorator()),
        entryProvider = { key ->
            when {
                key == manager.rootPage -> NavEntry(key = key) {
                    rootContent()
                }

                manager.isDetailPage(key) -> {
                    val detailPage = manager.requireDetailPage(key)
                    NavEntry(key = key) {
                        detailContent(detailPage)
                    }
                }

                else -> error("Unsupported master-detail navigation key: $key")
            }
        },
    )
}
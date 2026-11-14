package com.michaelflisar.toolbox.app

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveMasterDetailMode
import com.michaelflisar.composestyled.ui.layout.StyledMasterDetail
import com.michaelflisar.toolbox.app.features.navigation.NavScreenDefaults
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.display.MasterDetailNavDisplay
import com.michaelflisar.toolbox.app.features.navigation.manager.MasterDetailState

/**
 * Shows the master and detail panes, keeping the master as the NavDisplay root in single-pane
 * mode so transitions animate between the master and its detail pages.
 */
@Composable
fun <RootPage : NavScreen, DetailPage : NavKey> MasterDetailContainer(
    manager: MasterDetailState<RootPage, DetailPage>,
    masterDetailMode: StyledAdaptiveMasterDetailMode,
    defaultDetailPage: (() -> DetailPage)? = null,
    modifier: Modifier = Modifier,
    masterFraction: Float = 0.4f,
    paneSpacing: Dp = StyledTheme.spacing.medium,
    transitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
        NavScreenDefaults.masterDetailTransitionSpec(masterDetailMode),
    popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
        NavScreenDefaults.masterDetailPopTransitionSpec(masterDetailMode),
    predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform =
        NavScreenDefaults.masterDetailPredictivePopTransitionSpec(masterDetailMode),
    masterContent: @Composable () -> Unit,
    emptyDetailContent: @Composable () -> Unit,
    detailContent: @Composable (DetailPage) -> Unit,
) {
    val hasDetails = manager.hasDetails
    val noContent: @Composable () -> Unit = {}
    val rootContent = if (defaultDetailPage == null) emptyDetailContent else noContent
    LaunchedEffect(masterDetailMode, defaultDetailPage, hasDetails) {
        if (
            masterDetailMode == StyledAdaptiveMasterDetailMode.SideBySide &&
            defaultDetailPage != null &&
            !hasDetails
        ) {
            manager.pushInitialDetailsPage(defaultDetailPage())
        }
    }

    if (masterDetailMode == StyledAdaptiveMasterDetailMode.SideBySide) {
        StyledMasterDetail(
            modifier = modifier,
            mode = masterDetailMode,
            detailData = manager.detail,
            masterFraction = masterFraction,
            paneSpacing = paneSpacing,
            master = masterContent,
            detail = {
                MasterDetailNavDisplay(
                    manager = manager,
                    masterDetailMode = masterDetailMode,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = transitionSpec,
                    popTransitionSpec = popTransitionSpec,
                    predictivePopTransitionSpec = predictivePopTransitionSpec,
                    rootContent = rootContent,
                    detailContent = detailContent,
                )
            },
        )
    } else {
        MasterDetailNavDisplay(
            manager = manager,
            masterDetailMode = masterDetailMode,
            modifier = modifier,
            transitionSpec = transitionSpec,
            popTransitionSpec = popTransitionSpec,
            predictivePopTransitionSpec = predictivePopTransitionSpec,
            rootContent = masterContent,
            detailContent = detailContent,
        )
    }
}
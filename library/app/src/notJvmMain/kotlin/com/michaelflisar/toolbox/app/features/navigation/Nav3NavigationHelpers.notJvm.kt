package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformNavigationBackHandler(
    navigationState: String,
    replaceNavigationState: Boolean,
    onMouseForward: () -> Unit,
    onBrowserHistoryStateChanged: (String?) -> Unit,
) = PlatformNavigationBackHandlerNonJvm(
    navigationState,
    replaceNavigationState,
    onMouseForward,
    onBrowserHistoryStateChanged,
)

internal actual fun PlatformNavigateBack(
    canGoBack: Boolean,
    hasBrowserHistory: Boolean,
    onBackStack: () -> Unit,
) = PlatformNavigateBackNonJvm(canGoBack, hasBrowserHistory, onBackStack)

internal actual fun PlatformNavigateHistory(
    delta: Int,
    hasNavigationHistory: Boolean,
    onHistoryNavigation: () -> Unit,
) = PlatformNavigateHistoryNonJvm(delta, hasNavigationHistory, onHistoryNavigation)
@Composable
internal expect fun PlatformNavigationBackHandlerNonJvm(
    navigationState: String,
    replaceNavigationState: Boolean,
    onMouseForward: () -> Unit,
    onBrowserHistoryStateChanged: (String?) -> Unit,
)

internal expect fun PlatformNavigateBackNonJvm(
    canGoBack: Boolean,
    hasBrowserHistory: Boolean,
    onBackStack: () -> Unit,
)

internal expect fun PlatformNavigateHistoryNonJvm(
    delta: Int,
    hasNavigationHistory: Boolean,
    onHistoryNavigation: () -> Unit,
)

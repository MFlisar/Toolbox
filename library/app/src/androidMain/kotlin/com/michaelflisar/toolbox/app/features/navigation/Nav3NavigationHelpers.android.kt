package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformNavigationBackHandlerNonJvm(
    navigationState: String,
    replaceNavigationState: Boolean,
    onMouseForward: () -> Unit,
    onBrowserHistoryStateChanged: (String?) -> Unit,
) = Unit

internal actual fun PlatformNavigateBackNonJvm(
    canGoBack: Boolean,
    hasBrowserHistory: Boolean,
    onBackStack: () -> Unit,
) {
    if (canGoBack) onBackStack()
}

internal actual fun PlatformNavigateHistoryNonJvm(
    delta: Int,
    hasNavigationHistory: Boolean,
    onHistoryNavigation: () -> Unit,
) {
    onHistoryNavigation()
}

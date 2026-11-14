@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import kotlinx.browser.window
import kotlin.js.toJsString
import org.w3c.dom.events.Event

@Composable
internal actual fun PlatformNavigationBackHandlerNonJvm(
    navigationState: String,
    replaceNavigationState: Boolean,
    onMouseForward: () -> Unit,
    onBrowserHistoryStateChanged: (String?) -> Unit,
) {
    val currentBrowserHistoryStateChanged = rememberUpdatedState(onBrowserHistoryStateChanged)
    val lastNavigationState = remember { mutableStateOf<String?>(null) }

    SideEffect {
        val lastState = lastNavigationState.value
        if (lastState == null) {
            window.history.replaceState(navigationState.toJsString(), "", window.location.href)
            lastNavigationState.value = navigationState
        } else if (navigationState != lastState) {
            if (replaceNavigationState) {
                window.history.replaceState(navigationState.toJsString(), "", window.location.href)
            } else {
                window.history.pushState(navigationState.toJsString(), "", window.location.href)
            }
            lastNavigationState.value = navigationState
        }
    }

    DisposableEffect(Unit) {
        val browserHistoryListener: (Event) -> Unit = { event ->
            val state = window.history.state?.toString()
            if (state != null) {
                lastNavigationState.value = state
            }
            currentBrowserHistoryStateChanged.value(state)
        }

        window.addEventListener("popstate", browserHistoryListener)

        onDispose {
            window.removeEventListener("popstate", browserHistoryListener)
        }
    }
}

internal actual fun PlatformNavigateBackNonJvm(
    canGoBack: Boolean,
    hasBrowserHistory: Boolean,
    onBackStack: () -> Unit,
) {
    if (!canGoBack) return
    if (hasBrowserHistory) {
        window.history.back()
    } else {
        onBackStack()
    }
}

internal actual fun PlatformNavigateHistoryNonJvm(
    delta: Int,
    hasNavigationHistory: Boolean,
    onHistoryNavigation: () -> Unit,
) {
    if (hasNavigationHistory && delta != 0) {
        window.history.go(delta)
    } else {
        onHistoryNavigation()
    }
}

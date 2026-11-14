package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import com.michaelflisar.toolbox.app.features.navigation.manager.LocalMainBackStackManager
import com.michaelflisar.toolbox.app.features.navigation.manager.NavigationBackPriority

@Composable
internal expect fun PlatformNavigationBackHandler(
    navigationState: String,
    replaceNavigationState: Boolean,
    onMouseForward: () -> Unit,
    onBrowserHistoryStateChanged: (String?) -> Unit,
)

internal expect fun PlatformNavigateBack(
    canGoBack: Boolean,
    hasBrowserHistory: Boolean,
    onBackStack: () -> Unit,
)

internal expect fun PlatformNavigateHistory(
    delta: Int,
    hasNavigationHistory: Boolean,
    onHistoryNavigation: () -> Unit,
)

/**
 * Registers a back handler with the root navigation manager.
 * Enabled handlers run before the root stack; [NavigationBackPriority.Overlay] handlers run before
 * screen handlers. The callback returns true when it consumed Back. For screen-priority handlers,
 * [canNavigateBack] controls whether the handler is exposed as an available toolbar action. Overlay
 * handlers can consume Back without exposing a toolbar action. By default, the handler belongs to
 * the current [NavScreen]; pass `owner = null` for an app-wide handler such as a sheet.
 */
@Composable
fun RegisterNavigationBackHandler(
    enabled: Boolean = true,
    canNavigateBack: Boolean = false,
    canNavigateForward: Boolean = false,
    priority: NavigationBackPriority = NavigationBackPriority.Screen,
    owner: NavScreen? = LocalNav3ActiveScreen.current,
    onBack: () -> Boolean,
) {
    val currentEnabled = rememberUpdatedState(enabled)
    val currentCanNavigateBack = rememberUpdatedState(canNavigateBack)
    val currentCanNavigateForward = rememberUpdatedState(canNavigateForward)
    val currentOnBack = rememberUpdatedState(onBack)
    val manager = LocalMainBackStackManager.current
    val ownerNavigationIndex = if (owner == null) null else manager.selectedNavigationIndex

    DisposableEffect(manager, owner, ownerNavigationIndex, priority) {
        val unregister = manager.registerBackHandler(
            owner = owner,
            ownerNavigationIndex = ownerNavigationIndex,
            priority = priority,
            isEnabled = { currentEnabled.value },
            canNavigateBack = { currentCanNavigateBack.value },
            canNavigateForward = { currentCanNavigateForward.value },
            onBack = { currentEnabled.value && currentOnBack.value() },
        )
        onDispose(unregister)
    }
}

/**
 * Coordinates serialized navigation snapshots with a platform browser-history adapter.
 *
 * The caller owns serialization and the actual browser APIs. Browser history events should call
 * [restoreFromBrowser], while observed Nav3 state changes should call [recordNavigation].
 */
class Nav3BrowserHistory(
    private val pushState: (serializedState: String) -> Unit,
    private val replaceState: (serializedState: String) -> Unit,
    private val restoreState: (serializedState: String) -> Unit,
) {
    private var currentState: String? = null
    private var pendingRestoredState: String? = null

    /** Replaces the current browser entry with the app's initial navigation state. */
    fun initialize(serializedState: String) {
        replaceState(serializedState)
        currentState = serializedState
        pendingRestoredState = null
    }

    /** Records a Nav3 state change as a new browser-history entry. */
    fun recordNavigation(serializedState: String) {
        val restoredState = pendingRestoredState
        if (restoredState != null) {
            if (serializedState == restoredState) {
                currentState = serializedState
                pendingRestoredState = null
            }
            return
        }

        if (serializedState == currentState) return
        pushState(serializedState)
        currentState = serializedState
    }

    /**
     * Restores a state received from a browser history event.
     *
     * Returns false when the browser entry does not contain app navigation state.
     */
    fun restoreFromBrowser(serializedState: String?): Boolean {
        if (serializedState == null) return false
        if (serializedState == currentState) return true

        pendingRestoredState = serializedState
        try {
            restoreState(serializedState)
        } catch (exception: Exception) {
            pendingRestoredState = null
            throw exception
        }
        return true
    }
}

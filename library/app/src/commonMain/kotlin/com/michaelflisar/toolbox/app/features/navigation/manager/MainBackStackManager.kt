package com.michaelflisar.toolbox.app.features.navigation.manager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.PlatformNavigateBack
import com.michaelflisar.toolbox.app.features.navigation.PlatformNavigateHistory
import kotlin.random.Random
import kotlin.math.abs

/** Manager for navigation operations on the currently selected top-level destination. */
val LocalMainBackStackManager = compositionLocalOf<MainBackStackManager> {
    error("MainBackStackManager is not available outside MainAppScaffold")
}

/**
 * Back overlays such as sheets and dialogs take precedence over screen handlers, but do not expose
 * a toolbar back action while they are open.
 */
enum class NavigationBackPriority {
    Screen,
    Overlay,
}

private data class RegisteredNavigationHandler(
    val owner: NavScreen?,
    val ownerNavigationIndex: Int?,
    val priority: NavigationBackPriority,
    val isEnabled: () -> Boolean,
    val canNavigateBack: () -> Boolean,
    val canNavigateForward: () -> Boolean,
    val onBack: () -> Boolean,
)

class MainBackStackManager internal constructor(
    private val stackManagers: List<Nav3StackHistory>,
    private val navigationKeys: List<NavScreen>,
    private val selectedIndex: MutableIntState,
    val debugDrawerExpanded: MutableState<Boolean>,
    private val saveableStateHolder: SaveableStateHolder,
    internal val browserHistoryController: Nav3BrowserHistoryController,
    internal val browserHistoryDispatcher: Nav3BrowserHistoryDispatcher,
    val forwardHistoryEnabled: Boolean,
    private val resetInactiveNavigationStacksOnSelection: Boolean,
) {
    private val backHandlers = mutableStateListOf<RegisteredNavigationHandler>()

    val selectedNavigationIndex: Int
        get() = selectedIndex.intValue.coerceIn(0, navigationKeys.lastIndex.coerceAtLeast(0))

    internal val activeStackManager: Nav3StackHistory
        get() = stackManagers.getOrNull(selectedNavigationIndex)
            ?: error("No main back-stack exists for index $selectedNavigationIndex.")

    val activeBackStack: NavBackStack<NavKey>
        get() = activeStackManager.backStack

    val navigationState: String
        get() = browserHistoryController.currentState

    val replaceNavigationState: Boolean
        get() = browserHistoryController.replaceCurrentState

    val canGoBack: Boolean
        get() = activeBackStack.size > 1 ||
            activeBackHandlers().any {
                it.priority == NavigationBackPriority.Screen &&
                    it.isEnabled() &&
                    it.canNavigateBack()
            }

    /** Enabled overlays need system Back handling even when the active Nav3 stack is at its root. */
    internal val hasEnabledOverlayBackHandler: Boolean
        get() = activeBackHandlers().any {
            it.priority == NavigationBackPriority.Overlay && it.isEnabled()
        }

    val canGoForward: Boolean
        get() = forwardHistoryEnabled &&
            (activeStackManager.canGoForward ||
                activeBackHandlers().any { it.isEnabled() && it.canNavigateForward() })

    internal fun registerBackHandler(
        owner: NavScreen?,
        ownerNavigationIndex: Int?,
        priority: NavigationBackPriority,
        isEnabled: () -> Boolean,
        canNavigateBack: () -> Boolean,
        canNavigateForward: () -> Boolean,
        onBack: () -> Boolean,
    ): () -> Unit {
        val handler = RegisteredNavigationHandler(
            owner,
            ownerNavigationIndex,
            priority,
            isEnabled,
            canNavigateBack,
            canNavigateForward,
            onBack,
        )
        backHandlers += handler
        return { backHandlers.remove(handler) }
    }

    internal fun registerBrowserHistoryHandler(
        owner: NavScreen,
        ownerNavigationIndex: Int,
        handler: (state: String?, direction: Int, synchronizeHistoryState: Boolean) -> Boolean,
    ): () -> Unit = browserHistoryDispatcher.register { state, direction, synchronizeHistoryState ->
        if (
            ownerNavigationIndex == selectedNavigationIndex &&
            activeBackStack.lastOrNull() == owner
        ) {
            handler(state, direction, synchronizeHistoryState)
        } else {
            false
        }
    }

    @Composable
    internal fun ProvideEntryState(
        screen: NavScreen,
        navigationIndex: Int,
        content: @Composable () -> Unit,
    ) {
        val stateKey = "$navigationIndex:${screen::class.qualifiedName}:$screen"
        saveableStateHolder.SaveableStateProvider(stateKey, content)
    }

    fun selectNavigationItem(key: NavScreen) {
        val index = navigationKeys.indexOf(key)
        if (index < 0 || index == selectedNavigationIndex) return

        if (resetInactiveNavigationStacksOnSelection) {
            activeStackManager.resetToRoot()
        }
        selectedIndex.intValue = index
    }

    fun push(page: NavScreen) = activeStackManager.push(page)

    fun replaceTop(page: NavScreen): Boolean = activeStackManager.replaceTop(page)

    fun popBackStack(): Boolean = activeStackManager.popBackStack()

    fun popToRoot(): Boolean = activeStackManager.popToRoot()

    fun onBack() {
        if (!dispatchBackHandler()) {
            PlatformNavigateBack(
                canGoBack = activeStackManager.canGoBack,
                hasBrowserHistory = browserHistoryController.canGoBack,
                onBackStack = {
                    if (!navigateActiveStackHistory(-1)) {
                        activeStackManager.popBackStack(replaceCurrent = true)
                    }
                },
            )
        }
    }

    fun onMouseForward() {
        onForward()
    }

    fun onForward(): Boolean {
        if (!forwardHistoryEnabled) return false
        var handled = false
        PlatformNavigateHistory(
            delta = 1,
            hasNavigationHistory = browserHistoryController.canGoForward,
            onHistoryNavigation = {
                handled = navigateActiveStackHistory(1)
            },
        )
        return handled || browserHistoryController.canGoForward
    }

    fun onBrowserHistoryStateChanged(state: String?) {
        if (state == null) return

        val direction = browserHistoryController.moveTo(state)
        if (direction == 0) return

        val handled = browserHistoryDispatcher.handleHistoryState(
            state = state,
            direction = direction,
            synchronizeHistoryState = false,
        )
        if (!handled) {
            val offset = direction.compareTo(0)
            repeat(abs(direction)) {
                if (!browserHistoryDispatcher.handleHistoryDirection(
                        offset,
                        synchronizeHistoryState = false,
                    )
                ) {
                    activeStackManager.moveHistory(
                        offset,
                        synchronizeHistoryState = false,
                    )
                }
            }
        }
    }

    fun navigateActiveStackHistory(offset: Int): Boolean {
        if (browserHistoryDispatcher.handleHistoryDirection(
                offset,
                synchronizeHistoryState = true,
            )
        ) {
            return true
        }
        return activeStackManager.moveHistory(
            offset,
            synchronizeHistoryState = true,
        )
    }

    private fun activeBackHandlers(): List<RegisteredNavigationHandler> {
        val activeScreen = activeBackStack.lastOrNull() as? NavScreen
        return backHandlers.asReversed()
            .filter {
                it.owner == null ||
                    (it.ownerNavigationIndex == selectedNavigationIndex && it.owner == activeScreen)
            }
            .sortedByDescending { it.priority.ordinal }
    }

    private fun dispatchBackHandler(): Boolean {
        activeBackHandlers().forEach { handler ->
            if (handler.isEnabled() && handler.onBack()) return true
        }
        return false
    }

}

@Composable
fun rememberMainBackStackManager(
    navigationKeys: List<NavScreen>,
    backStackConfiguration: SavedStateConfiguration,
    startDestination: NavScreen?,
    forwardHistoryEnabled: Boolean = true,
    resetInactiveNavigationStacksOnSelection: Boolean = false,
): MainBackStackManager {
    require(navigationKeys.isNotEmpty() || startDestination != null) {
        "Provide a startDestination when there are no navigation items."
    }
    require(navigationKeys.distinct().size == navigationKeys.size) {
        "Navigation item keys must be unique."
    }

    val backStacks = navigationKeys.associateWith { navigationKey ->
        key(navigationKey) {
            rememberNavBackStack(backStackConfiguration, navigationKey)
        }
    }
    val singleBackStack = if (navigationKeys.isEmpty()) {
        val root = checkNotNull(startDestination)
        key(root) {
            rememberNavBackStack(backStackConfiguration, root)
        }
    } else {
        null
    }

    val selectedIndex = rememberSaveable { mutableIntStateOf(0) }
    val debugDrawerExpanded = rememberSaveable { mutableStateOf(false) }
    val selectedNavigationIndex = selectedIndex.intValue
        .coerceIn(0, navigationKeys.lastIndex.coerceAtLeast(0))
    val allBackStacks = if (singleBackStack != null) {
        listOf(singleBackStack)
    } else {
        navigationKeys.map { backStacks.getValue(it) }
    }

    val browserSessionId = remember { Random.nextLong().toULong().toString(16) }
    val initialBrowserState = remember(browserSessionId) { "$browserSessionId-0" }
    val saveableStateHolder = rememberSaveableStateHolder()
    val browserHistoryController = remember {
        Nav3BrowserHistoryController(initialBrowserState, browserSessionId)
    }
    val browserHistoryDispatcher = remember { Nav3BrowserHistoryDispatcher() }
    val stackManagers = remember(allBackStacks, browserHistoryController, forwardHistoryEnabled) {
        allBackStacks.map { backStack ->
            Nav3StackHistory(backStack, browserHistoryController, forwardHistoryEnabled)
        }
    }

    SideEffect {
        stackManagers.forEach { stackManager ->
            stackManager.recordCurrentNavigation()
        }
    }

    return remember(
        stackManagers,
        navigationKeys,
        selectedIndex,
        debugDrawerExpanded,
        saveableStateHolder,
        browserHistoryController,
        browserHistoryDispatcher,
        forwardHistoryEnabled,
        resetInactiveNavigationStacksOnSelection,
    ) {
        MainBackStackManager(
            stackManagers,
            navigationKeys,
            selectedIndex,
            debugDrawerExpanded,
            saveableStateHolder,
            browserHistoryController,
            browserHistoryDispatcher,
            forwardHistoryEnabled,
            resetInactiveNavigationStacksOnSelection,
        )
    }
}

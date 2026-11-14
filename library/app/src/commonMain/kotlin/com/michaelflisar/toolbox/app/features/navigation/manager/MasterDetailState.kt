package com.michaelflisar.toolbox.app.features.navigation.manager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveMasterDetailMode
import com.michaelflisar.toolbox.app.features.navigation.LocalNav3ActiveScreen
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.PlatformNavigateBack
import com.michaelflisar.toolbox.app.features.navigation.PlatformNavigateHistory
import com.michaelflisar.toolbox.app.features.navigation.RegisterNavigationBackHandler
import kotlin.reflect.KClass

internal class Nav3BrowserHistoryController(
    initialState: String,
    private val sessionId: String,
) {
    private val states = mutableStateListOf(initialState)
    private val index = mutableIntStateOf(0)
    private val currentStateValue = mutableStateOf(initialState)
    private val replaceCurrentValue = mutableStateOf(false)
    private var nextStateIndex = 0

    val currentState: String
        get() = currentStateValue.value

    val replaceCurrentState: Boolean
        get() = replaceCurrentValue.value

    val canGoBack: Boolean
        get() = index.intValue > 0

    val canGoForward: Boolean
        get() = index.intValue < states.lastIndex

    fun recordNavigation(replaceCurrent: Boolean = false): String {
        nextStateIndex += 1
        val state = "$sessionId-$nextStateIndex"
        if (replaceCurrent) {
            states[index.intValue] = state
        } else {
            while (states.size > index.intValue + 1) {
                states.removeAt(states.lastIndex)
            }
            states += state
            index.intValue = states.lastIndex
        }
        currentStateValue.value = state
        replaceCurrentValue.value = replaceCurrent
        return state
    }

    fun moveTo(state: String): Int {
        val targetIndex = states.indexOf(state)
        if (targetIndex < 0) return 0
        val direction = targetIndex - index.intValue
        index.intValue = targetIndex
        currentStateValue.value = state
        replaceCurrentValue.value = false
        return direction
    }

    fun distanceTo(state: String): Int? {
        val targetIndex = states.indexOf(state)
        return if (targetIndex < 0) null else targetIndex - index.intValue
    }
}

internal class Nav3BrowserHistoryDispatcher {
    private val handlers =
        mutableListOf<(state: String?, direction: Int, synchronizeHistoryState: Boolean) -> Boolean>()

    fun register(
        handler: (state: String?, direction: Int, synchronizeHistoryState: Boolean) -> Boolean,
    ): () -> Unit {
        handlers += handler
        return { handlers.remove(handler) }
    }

    fun handleHistoryDirection(
        direction: Int,
        synchronizeHistoryState: Boolean,
    ): Boolean = handleHistoryState(null, direction, synchronizeHistoryState)

    fun handleHistoryState(
        state: String?,
        direction: Int,
        synchronizeHistoryState: Boolean,
    ): Boolean {
        handlers.toList().asReversed().forEach { handler ->
            if (handler(state, direction, synchronizeHistoryState)) return true
        }
        return false
    }
}

/** Saveable master-detail child navigation owned by one root [NavScreen]. */
class MasterDetailState<RootPage : NavScreen, DetailPage : NavKey> internal constructor(
    val backStack: NavBackStack<NavKey>,
    val rootPage: RootPage,
    private val detailPageType: KClass<DetailPage>,
    private val masterDetailMode: State<StyledAdaptiveMasterDetailMode>,
    private val browserHistory: Nav3BrowserHistoryController,
    private val forwardHistoryEnabled: Boolean,
) {
    private val history = Nav3StackHistory(backStack, browserHistory, forwardHistoryEnabled)
    private var popAllTargetState: String? = null

    val hasDetails: Boolean
        get() = detailPageType.isInstance(backStack.lastOrNull())

    val canNavigateBack: Boolean
        get() = hasDetails && !isSideBySide

    val canNavigateForward: Boolean
        get() = !isSideBySide && history.canGoForward

    @Suppress("UNCHECKED_CAST")
    val detail: DetailPage?
        get() = if (hasDetails) backStack.last() as DetailPage else null

    internal fun isDetailPage(page: NavKey): Boolean = detailPageType.isInstance(page)

    @Suppress("UNCHECKED_CAST")
    internal fun requireDetailPage(page: NavKey): DetailPage {
        require(detailPageType.isInstance(page)) {
            "Expected a detail page of type ${detailPageType.qualifiedName}, but got $page."
        }
        return page as DetailPage
    }

    private val isSideBySide: Boolean
        get() = masterDetailMode.value == StyledAdaptiveMasterDetailMode.SideBySide

    fun switchDetailsPage(page: DetailPage) {
        val currentPage = backStack.lastOrNull()
        if (currentPage == page) return

        when {
            currentPage == rootPage -> {
                backStack.add(page)
                history.recordCurrentNavigation()
            }

            detailPageType.isInstance(currentPage) -> {
                backStack[backStack.lastIndex] = page
                history.recordCurrentNavigation(replaceCurrent = true)
            }

            else -> error("The master-detail back stack is not at its root or a detail page.")
        }
    }

    fun pushDetailsPage(page: DetailPage) {
        pushDetailsPage(page, recordBrowserHistory = true)
    }

    internal fun pushInitialDetailsPage(page: DetailPage) {
        pushDetailsPage(page, recordBrowserHistory = false)
    }

    private fun pushDetailsPage(page: DetailPage, recordBrowserHistory: Boolean) {
        val currentPage = backStack.lastOrNull()
        check(currentPage == rootPage || detailPageType.isInstance(currentPage)) {
            "The master-detail back stack is not at its root or a detail page."
        }

        backStack.add(page)
        history.recordCurrentNavigation(recordBrowserHistory = recordBrowserHistory)
    }

    fun popAllDetailPages(): Boolean {
        if (!hasDetails) return false

        val rootState = history.initialNavigationState
        val delta = browserHistory.distanceTo(rootState)
        if (delta != null && delta != 0) {
            popAllTargetState = rootState
            PlatformNavigateHistory(
                delta = delta,
                hasNavigationHistory = true,
                onHistoryNavigation = { resetToRoot(rootState) },
            )
        } else {
            resetToRoot(browserHistory.recordNavigation(replaceCurrent = true))
        }
        return true
    }

    fun onBack(): Boolean {
        if (isSideBySide) return false
        if (!hasDetails) return false

        var handled = false
        PlatformNavigateBack(
            canGoBack = history.canGoBack,
            hasBrowserHistory = browserHistory.canGoBack,
            onBackStack = {
                handled = if (history.moveHistory(-1, synchronizeHistoryState = true)) {
                    true
                } else if (backStack.size > 1) {
                    backStack.removeAt(backStack.lastIndex)
                    history.recordCurrentNavigation(replaceCurrent = true)
                    true
                } else {
                    false
                }
            },
        )
        return handled || history.canGoBack
    }

    internal fun handleBrowserHistoryDirection(
        state: String?,
        direction: Int,
        synchronizeHistoryState: Boolean,
    ): Boolean {
        if (isSideBySide || direction == 0) return false
        val handled = history.handleBrowserHistoryDirection(state, direction, synchronizeHistoryState)
        if (handled && popAllTargetState == state && state != null && !hasDetails) {
            history.resetHistoryAtCurrent(state)
            popAllTargetState = null
        }
        return handled
    }

    private fun resetToRoot(navigationState: String) {
        backStack.clear()
        backStack.add(rootPage)
        history.resetHistoryAtCurrent(navigationState)
        popAllTargetState = null
    }
}

@Composable
fun <RootPage : NavScreen, DetailPage : NavKey> rememberMasterDetailState(
    configuration: SavedStateConfiguration,
    rootPage: RootPage,
    detailPageType: KClass<DetailPage>,
    masterDetailMode: State<StyledAdaptiveMasterDetailMode>,
): MasterDetailState<RootPage, DetailPage> {
    val backStack = rememberNavBackStack(configuration, rootPage)
    val mainBackStackManager = LocalMainBackStackManager.current
    val browserHistory = mainBackStackManager.browserHistoryController
    val owner = LocalNav3ActiveScreen.current
        ?: error("Master-detail state must be created inside a NavScreen entry.")
    require(owner == rootPage) {
        "Master-detail state rootPage must match its owning NavScreen."
    }
    val manager = remember(
        backStack,
        rootPage,
        detailPageType,
        masterDetailMode,
        browserHistory,
        mainBackStackManager.forwardHistoryEnabled,
    ) {
        MasterDetailState(
            backStack,
            rootPage,
            detailPageType,
            masterDetailMode,
            browserHistory,
            mainBackStackManager.forwardHistoryEnabled,
        )
    }
    val ownerNavigationIndex = mainBackStackManager.selectedNavigationIndex

    RegisterNavigationBackHandler(
        enabled = manager.canNavigateBack,
        canNavigateBack = manager.canNavigateBack,
        canNavigateForward = manager.canNavigateForward,
        owner = owner,
        onBack = manager::onBack,
    )
    DisposableEffect(manager, mainBackStackManager, ownerNavigationIndex) {
        val unregister = mainBackStackManager.registerBrowserHistoryHandler(
            owner = owner,
            ownerNavigationIndex = ownerNavigationIndex,
            handler = manager::handleBrowserHistoryDirection,
        )
        onDispose(unregister)
    }
    return manager
}

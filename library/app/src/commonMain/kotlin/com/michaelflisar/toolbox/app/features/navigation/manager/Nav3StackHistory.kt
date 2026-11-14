package com.michaelflisar.toolbox.app.features.navigation.manager

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

private data class Nav3BackStackHistoryEntry(
    val pages: List<NavKey>,
    val navigationState: String,
)

/**
 * Internal per-stack history shared by the root manager and child navigation features.
 */
internal class Nav3StackHistory(
    val backStack: NavBackStack<NavKey>,
    private val browserHistory: Nav3BrowserHistoryController,
    private val forwardHistoryEnabled: Boolean = true,
) {
    private val history = mutableStateListOf(
        Nav3BackStackHistoryEntry(backStack.toList(), browserHistory.currentState),
    )
    private val historyIndex = mutableIntStateOf(0)

    val canGoBack: Boolean
        get() = backStack.size > 1

    val canGoForward: Boolean
        get() = forwardHistoryEnabled && historyIndex.intValue < history.lastIndex

    internal fun recordCurrentNavigation(
        replaceCurrent: Boolean = false,
        recordBrowserHistory: Boolean = true,
    ): Boolean {
        val pages = backStack.toList()
        if (pages == history[historyIndex.intValue].pages) return false

        val state = if (recordBrowserHistory) {
            browserHistory.recordNavigation(replaceCurrent)
        } else {
            browserHistory.currentState
        }
        if (replaceCurrent) {
            history[historyIndex.intValue] = Nav3BackStackHistoryEntry(pages, state)
        } else {
            while (history.size > historyIndex.intValue + 1) {
                history.removeAt(history.lastIndex)
            }
            history += Nav3BackStackHistoryEntry(pages, state)
            historyIndex.intValue = history.lastIndex
        }
        return true
    }

    fun push(page: NavKey) {
        backStack.add(page)
        recordCurrentNavigation()
    }

    fun replaceTop(page: NavKey): Boolean {
        if (backStack.isEmpty() || backStack.last() == page) return false
        backStack[backStack.lastIndex] = page
        recordCurrentNavigation()
        return true
    }

    fun popBackStack(replaceCurrent: Boolean = false): Boolean {
        if (!canGoBack) return false
        backStack.removeAt(backStack.lastIndex)
        recordCurrentNavigation(replaceCurrent = replaceCurrent)
        return true
    }

    fun popToRoot(): Boolean {
        if (backStack.size <= 1) return false
        while (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
        recordCurrentNavigation()
        return true
    }

    fun resetToRoot(): Boolean {
        val root = backStack.firstOrNull() ?: return false
        val hasHistoryToClear = history.size > 1 || historyIndex.intValue > 0
        if (backStack.size <= 1 && !hasHistoryToClear) return false

        backStack.clear()
        backStack.add(root)
        val state = browserHistory.recordNavigation(replaceCurrent = true)
        resetHistoryAtCurrent(state)
        return true
    }

    fun moveHistory(offset: Int, synchronizeHistoryState: Boolean = true): Boolean {
        val targetIndex = historyIndex.intValue + offset
        if (history.getOrNull(targetIndex) == null) return false
        moveToIndex(targetIndex, synchronizeHistoryState)
        return true
    }

    fun resetHistoryAtCurrent(navigationState: String = browserHistory.currentState) {
        history.clear()
        history += Nav3BackStackHistoryEntry(backStack.toList(), navigationState)
        historyIndex.intValue = 0
    }

    val initialNavigationState: String
        get() = history.first().navigationState

    internal fun handleBrowserHistoryDirection(
        state: String?,
        direction: Int,
        synchronizeHistoryState: Boolean,
    ): Boolean {
        if (direction == 0) return false
        val targetIndex = state?.let { targetState ->
            history.indexOfFirst { it.navigationState == targetState }.takeIf { it >= 0 }
        }
        if (targetIndex != null) {
            moveToIndex(targetIndex, synchronizeHistoryState = false)
            return true
        }
        return moveHistory(direction.compareTo(0), synchronizeHistoryState)
    }

    private fun moveToIndex(targetIndex: Int, synchronizeHistoryState: Boolean) {
        val entry = history[targetIndex]
        historyIndex.intValue = targetIndex
        backStack.clear()
        backStack.addAll(entry.pages)
        if (synchronizeHistoryState) {
            browserHistory.moveTo(entry.navigationState)
        }
    }
}

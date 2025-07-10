package com.michaelflisar.toolbox.app.features.appstate

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.IntSize
import com.michaelflisar.composechangelog.classes.ChangelogState
import com.michaelflisar.composechangelog.classes.rememberChangelogState
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.platform.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun rememberAppState(
    toolbar: AppState.Toolbar = rememberAppToolbar(),
    scope: CoroutineScope = rememberCoroutineScope(),
    size: MutableState<IntSize> = remember { mutableStateOf(IntSize.Zero) },
    changelogState: ChangelogState = rememberChangelogState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): AppState {
    return AppState(scope, size, toolbar, changelogState, snackbarHostState)
}

@Composable
fun rememberAppToolbar(
    title: String = CommonApp.setup.name(),
    subtitle: String = "",
    showBackButton: Boolean = false,
    menuProvider: @Composable (() -> Unit)? = null
): AppState.Toolbar {
    return AppState.Toolbar(
        title = mutableStateOf(title),
        subtitle = mutableStateOf(subtitle),
        showBackButton = mutableStateOf(showBackButton),
        menuProvider = menuProvider,
        selection = mutableStateOf(null)
    )
}

class AppState internal constructor(
    val scope: CoroutineScope,
    val size: MutableState<IntSize>,
    val toolbar: Toolbar,
    val changelogState: ChangelogState,
    internal val snackbarHostState: SnackbarHostState
) {
    class Toolbar(
        val title: MutableState<String>,
        val subtitle: MutableState<String>,
        val showBackButton: MutableState<Boolean>,
        private val menuProvider: @Composable (() -> Unit)? = null,
        val selection: MutableState<Selection?>
    ) {
        @Composable
        fun Menu() {
            if (selection.value == null)
                menuProvider?.invoke()
        }

        class Selection(
            private val selectionMode: MutableState<Boolean> = mutableStateOf(false),
            private val selection: MutableState<Set<Long>> = mutableStateOf(emptySet())
        ) {

            val isInSelectionMode by selectionMode
            val selectedIds by selection

            private val _count: MutableState<Int> = mutableIntStateOf(0)
            val count by _count

            var selectionMenuProvider: @Composable (() -> Unit)? = null

            @Composable
            fun Menu() {
                selectionMenuProvider?.invoke()
            }

            fun isSelected(id: Long) = selection.value.contains(id)

            fun enableSelectionMode(count: Int, menuProvider: @Composable () -> Unit) {
                this.selectionMenuProvider = menuProvider
                _count.value = count
                selectionMode.value = true
            }

            fun toggle(id: Long, menuProvider: @Composable () -> Unit) {
                if (isInSelectionMode) {
                    toggle(id)
                    //if (selection.value.isEmpty()) {
                    //    selectionMode.value = false
                    //}
                } else {
                    this.selectionMenuProvider = menuProvider
                    selection.value += id
                    selectionMode.value = true
                }
            }

            fun toggle(id: Long) {
                if (isSelected(id)) {
                    selection.value -= id
                } else {
                    selection.value += id
                }
            }

            fun remove(ids: List<Long>) {
                selection.value -= ids
            }

            fun updateCount(count: Int) {
                _count.value = count
            }

            fun clear() {
                selectionMode.value = false
                selection.value = emptySet()
            }
        }
    }

    val landscape: Boolean
        get() = size.value.width > size.value.height

    val portrait: Boolean
        get() = !landscape

    private val pending: ArrayList<Job> = ArrayList()

    fun showSnackbar(
        info: String,
        actionLabel: String? = null,
        cancelAllPending: Boolean = true,
        duration: SnackbarDuration = SnackbarDuration.Short,
        scope: CoroutineScope = this.scope
    ) {
        if (cancelAllPending) {
            pending.forEach { it.cancel() }
            pending.clear()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
        pending += scope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(info, actionLabel, duration = duration)
        }
    }

    fun showToast(
        info: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
        scope: CoroutineScope = this.scope
    ) {
        // duration.ordinal passt zufällig perfekt zu snackbar ordinal bei toasts!
        Platform.showToast?.invoke(info, duration.ordinal) ?: showSnackbar(info, duration = duration, scope = scope)
    }
}
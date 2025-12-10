package com.michaelflisar.toolbox.app.features.appstate

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.IntSize
import com.michaelflisar.composechangelog.classes.ChangelogState
import com.michaelflisar.composechangelog.classes.rememberChangelogState
import com.michaelflisar.composedialogs.core.DialogStateNoData
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.app.features.toolbar.selection.LocalSelectionToolbarState
import com.michaelflisar.toolbox.app.platform.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun rememberAppState(
    scope: CoroutineScope = rememberCoroutineScope(),
    size: MutableState<IntSize> = remember { mutableStateOf(IntSize.Zero) },
    changelogState: ChangelogState = rememberChangelogState(),
    showProVersionDialog: DialogStateNoData = rememberDialogState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): AppState {
    return AppState(scope, size, changelogState, showProVersionDialog, snackbarHostState)
}

class AppState internal constructor(
    val scope: CoroutineScope,
    val size: MutableState<IntSize>,
    val changelogState: ChangelogState,
    val showProVersionDialog: DialogStateNoData,
    internal val snackbarHostState: SnackbarHostState
) {
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
        scope: CoroutineScope = this.scope,
        onResult: (SnackbarResult) -> Unit = {}
    ) {
        if (cancelAllPending) {
            pending.forEach { it.cancel() }
            pending.clear()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
        pending += scope.launch(Dispatchers.Main) {
            val result = snackbarHostState.showSnackbar(info, actionLabel, duration = duration)
            onResult(result)
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
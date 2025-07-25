package com.michaelflisar.toolbox.windows.classes

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.michaelflisar.toolbox.windows.prefs.DesktopAppPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.skiko.MainUIDispatcher

val LocalAppState = compositionLocalOf<AppState> { error("No value provided") }

@Composable
fun rememberAppState(
    prefs: DesktopAppPrefs
): AppState {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val close = remember { mutableStateOf(false) }
    return AppState(prefs, scope, snackbarHostState, close)
}

@Immutable
data class AppState(
    val prefs: DesktopAppPrefs,
    private val scope: CoroutineScope,
    internal val snackbarHostState: SnackbarHostState,
    internal val close: MutableState<Boolean>
) {
    fun close() {
        close.value = true
    }

    private val pending: ArrayList<Job> = ArrayList()

    fun showSnackbar(
        info: String,
        actionLabel: String? = null,
        cancelAllPending: Boolean = true,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        if (cancelAllPending) {
            pending.forEach { it.cancel() }
            pending.clear()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
        pending += scope.launch(MainUIDispatcher) {
            snackbarHostState.showSnackbar(info, actionLabel, duration = duration)
        }
    }
}
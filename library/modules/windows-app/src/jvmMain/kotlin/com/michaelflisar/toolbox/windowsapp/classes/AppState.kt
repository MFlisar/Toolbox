package com.michaelflisar.toolbox.windowsapp.classes

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skiko.MainUIDispatcher

val LocalAppState = compositionLocalOf<AppState> { error("No value provided") }

@Composable
fun rememberAppState(): AppState {
    val scope = rememberCoroutineScope()
    val state = remember { mutableStateOf<Status>(Status.None) }
    val customStatusInfos = remember { mutableStateOf<List<StatusInfo>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val close = remember { mutableStateOf(false) }
    return AppState(scope, state, customStatusInfos, snackbarHostState, close)
}

@Immutable
data class AppState(
    private val scope: CoroutineScope,
    val state: MutableState<Status>,
    val customStatusInfos: MutableState<List<StatusInfo>>,
    val snackbarHostState: SnackbarHostState,
    val close: MutableState<Boolean>
) {
    fun setState(status: Status) {
        scope.launch(MainUIDispatcher) {
            state.value = status
        }
    }

    fun showSnackbar(
        scope: CoroutineScope,
        info: String,
        actionLabel: String? = null,
        cancelAllPending: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        if (cancelAllPending) {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
        scope.launch(MainUIDispatcher) {
            snackbarHostState.showSnackbar(info, actionLabel, duration = duration)
        }
    }
}
package com.michaelflisar.publicutilities.windowsapp.classes

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import com.michaelflisar.publicutilities.windowsapp.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skiko.MainUIDispatcher
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

val LocalAppState = compositionLocalOf<AppState> { error("No value provided") }

@Composable
fun rememberAppState(
    settingsFile: File = File(Paths.get("").absolutePathString(), "settings.dat")
): AppState {
    val scope = rememberCoroutineScope()
    val settings = remember(settingsFile) { mutableStateOf(Settings.read(settingsFile)) }
    val state = remember { mutableStateOf<Status>(Status.None) }
    val customStatusInfos = remember { mutableStateOf<List<StatusInfo>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val close = remember { mutableStateOf(false) }
    return AppState(scope, settings, state, customStatusInfos, snackbarHostState, close)
}

@Immutable
data class AppState(
    private val scope: CoroutineScope,
    val settings: MutableState<Settings>,
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
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        scope.launch(MainUIDispatcher) {
            snackbarHostState.showSnackbar(info, actionLabel, duration = duration)
        }
    }
}
package com.michaelflisar.toolbox.app.features.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.composedebugdrawer.core.rememberDebugDrawerState
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.appstate.AppState
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState

@Composable
fun RootLocalProvider(
    appState: AppState,
    setRootLocals: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (!setRootLocals) {
        content()
        return
    }

    val setup = AppSetup.get()

    val stateInitiallyExpandedIds = remember { mutableStateOf<List<String>?>(null) }
    LaunchedEffect(stateInitiallyExpandedIds.value) {
        if (stateInitiallyExpandedIds.value == null) {
            stateInitiallyExpandedIds.value =
                setup.debugPrefs.debugDrawerExpandedIds.read().toList()
        }
    }
    val initiallyExpandedIds = stateInitiallyExpandedIds.value ?: return

    val drawerState = rememberDebugDrawerState(
        expandSingleOnly = true,
        initialExpandedIds = initiallyExpandedIds
    )

    CompositionLocalProvider(
        LocalAppState provides appState,
        LocalDebugDrawerState provides drawerState
    ) {
        content()
    }
}
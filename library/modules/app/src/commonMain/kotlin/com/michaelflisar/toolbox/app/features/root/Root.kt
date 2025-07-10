package com.michaelflisar.toolbox.app.features.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.statesaver.kotpreferences.ChangelogStateSaverKotPreferences
import com.michaelflisar.composedebugdrawer.core.DebugDrawer
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.composedebugdrawer.core.rememberDebugDrawerState
import com.michaelflisar.composedialogs.core.BackHandler
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsState
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.features.appstate.AppState
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.DebugDrawer
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.app.resources.Res
import com.michaelflisar.toolbox.app.resources.settings_changelog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal expect fun rememberComposeTheme(setup: AppSetup): ComposeTheme.State

/**
 * the main app composable
 *
 * @param appState the app state to use, which is provided via composition local
 * @param dialogs to show, which are rendered inside the root composable
 * @param debugDrawer the content of the debug drawer
 * @param content the main content of the app
 */
@Composable
fun Root(
    appState: AppState,
    dialogs: @Composable () -> Unit = {},
    debugDrawer: @Composable (drawerState: DebugDrawerState) -> Unit = {},
    setRootLocals: Boolean = true,
    content: @Composable () -> Unit
) {
    val setup = CommonApp.setup
    val composeThemeState = rememberComposeTheme(setup)
    val showDebugDrawer by setup.debugPrefs.showDebugDrawer.collectAsState()

    ComposeTheme(
        state = composeThemeState,
        shapes = MaterialTheme.shapes,
        typography = MaterialTheme.typography
    ) {
        RootLocalProvider(appState, setRootLocals) {
            val drawerState = LocalDebugDrawerState.current
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { appState.size.value = it },
            ) {
                DebugDrawer(
                    enabled = setup.supportDebugDrawer && showDebugDrawer == true,
                    drawerState = drawerState,
                    drawerContent = { DebugDrawer(drawerState, debugDrawer) },
                    content = {
                        content()
                        RootBackHandler()
                        RootDialogs()
                        dialogs()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootDialogs() {

    val appState = LocalAppState.current
    val setup = CommonApp.setup
    val changelogSetup = setup.changelogSetup
    if (changelogSetup == null) {
        return // no changelog setup, so we don't show anything
    }

    val changelogStateSaverKotPrefs = remember {
        ChangelogStateSaverKotPreferences(setup.prefs.lastShownVersionForChangelog)
    }

    LaunchedEffect(Unit) {
        appState.changelogState.checkShouldShowChangelogOnStart(
            stateSaver = changelogStateSaverKotPrefs,
            versionName = setup.versionName,
            versionFormatter = changelogSetup.versionFormatter
        )
    }

    if (appState.changelogState.visible) {
        ModalBottomSheet(
            onDismissRequest = {
                appState.changelogState.hide()
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = 8.dp,
                        vertical = 16.dp
                    )
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(Res.string.settings_changelog),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Changelog(
                    appState.changelogState,
                    CommonApp.setup.changelogSetup!!,
                    Modifier.fillMaxWidth()
                )
            }
        }
    }
}


internal object RootBackHandler {
    fun navigatorShouldHandlerBackPress(
        appState: AppState,
        debugDrawerState: DebugDrawerState
    ): Boolean {
        return !backHandlerEnabled(appState, debugDrawerState)
    }

    fun backHandlerEnabled(
        appState: AppState,
        debugDrawerState: DebugDrawerState
    ): Boolean {
        return appState.toolbar.selection.value?.isInSelectionMode == true || debugDrawerState.drawerState.isOpen
    }
}

@Composable
internal fun RootBackHandler(onBack: (() -> Unit)? = null) {
    val appState = LocalAppState.current
    val debugDrawerState = LocalDebugDrawerState.current

    val scope = rememberCoroutineScope()

    BackHandler(
        enabled = RootBackHandler.backHandlerEnabled(
            appState,
            debugDrawerState
        )
    ) {
        L.d { "AppBackHandler called..." }
        if (debugDrawerState.drawerState.isOpen) {
            scope.launch {
                debugDrawerState.drawerState.close()
            }
            L.d { "BackHandler - Debug Drawer..." }
        } else if (appState.toolbar.selection.value?.isInSelectionMode == true) {
            appState.toolbar.selection.value?.clear()
            L.d { "BackHandler - Toolbar Selection Mode..." }
        } else if (onBack != null) {
            onBack()
            L.d { "BackHandler - onBack..." }
        }
    }

}

@Composable
internal fun rememberComposeThemeDefault(setup: AppSetup): ComposeTheme.State {
    val theme = setup.prefs.theme.collectAsStateNotNull()
    val contrast = setup.prefs.contrast.asMutableStateNotNull()
    val dynamic = setup.prefs.dynamicTheme.asMutableStateNotNull()
    val customTheme = setup.prefs.customTheme.asMutableStateNotNull()
    return ComposeTheme.State(theme, contrast, dynamic, customTheme)
}
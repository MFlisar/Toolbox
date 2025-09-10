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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.statesaver.kotpreferences.ChangelogStateSaverKotPreferences
import com.michaelflisar.composedebugdrawer.core.DebugDrawer
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.features.appstate.AppState
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.app.features.navigation.NavBackHandler
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.settings_changelog
import org.jetbrains.compose.resources.stringResource

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
    setRootLocals: Boolean,
    dialogs: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val setup = CommonApp.setup
    val showDebugDrawer by setup.debugPrefs.showDebugDrawer.collectAsStateNotNull()
    RootLocalProvider(appState, setRootLocals) {
        val drawerState = LocalDebugDrawerState.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { appState.size.value = it },
        ) {
            DebugDrawer(
                enabled = setup.debugDrawer != null && showDebugDrawer,
                drawerState = drawerState,
                drawerContent = {
                    setup.debugDrawer?.invoke(drawerState)
                },
                content = {
                    content()
                    NavBackHandler()
                    RootDialogs()
                    dialogs()
                }
            )
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
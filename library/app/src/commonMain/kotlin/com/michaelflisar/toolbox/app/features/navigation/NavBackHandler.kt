package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.logIf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal object NavBackHandler {

    fun handleBackPress(scope: CoroutineScope, debugDrawerState: DebugDrawerState) {
        scope.launch {
            debugDrawerState.drawerState.close()
        }
    }

    @Composable
    fun canHandleBackPress(): Boolean {
        val debugDrawerState = LocalDebugDrawerState.current
        return remember {
            derivedStateOf {
                debugDrawerState.drawerState.isOpen
            }
        }.value
    }
}

/*
 * handles back press for debug drawer + local navigator automatically
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavBackHandler(
    canGoBack: Boolean = false,
    onBack: () -> Unit = {}
) {
    val debugDrawerState = LocalDebugDrawerState.current
    val navigator = LocalNavigator.currentOrThrow

    val scope = rememberCoroutineScope()
    val navBackHandlerCanHandleBackPress = NavBackHandler.canHandleBackPress()
    val enabled by remember(navBackHandlerCanHandleBackPress, canGoBack, navigator.canPop) {
        derivedStateOf {
            navBackHandlerCanHandleBackPress || canGoBack || navigator.canPop
        }
    }

    LaunchedEffect(enabled) {
        L.logIf(ToolboxLogging.Tag.Navigation)?.i { "NavBackHandler: enabled = $enabled" }
    }

    BackHandler(
        enabled = enabled
    ) {
        L.logIf(ToolboxLogging.Tag.Navigation)?.i { "NavBackHandler called..." }
        if (navBackHandlerCanHandleBackPress) {
            NavBackHandler.handleBackPress(scope, debugDrawerState)
            L.logIf(ToolboxLogging.Tag.Navigation)?.i { "NavBackHandler::handleBackPress called..." }
        } else {
            if (canGoBack) {
                onBack()
                L.logIf(ToolboxLogging.Tag.Navigation)?.i { "NavBackHandler - onBack called..." }
            } else {
                navigator.pop()
                L.logIf(ToolboxLogging.Tag.Navigation)?.i { "NavBackHandler - navigator.pop()..." }
            }
        }
    }
}
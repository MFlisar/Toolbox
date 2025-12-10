package com.michaelflisar.toolbox.app.features.backhandlerregistry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.logIf

val LocalBackHandlerRegistry =
    staticCompositionLocalOf<BackHandlerRegistry> { error("No BackHandlerRegistry provided") }

class BackHandler(
    val canHandle: () -> Boolean,
    val handle: () -> Unit,
    val visibleInToolbar: Boolean
)

class BackHandlerRegistry {
    private val handlers = mutableStateListOf<BackHandler>()

    fun register(handler: BackHandler): () -> Unit {
        handlers.add(handler)
        return { handlers.remove(handler) } // returns an unregister function
    }

    fun handleBackPress(): Boolean {
        // Iterate in reverse order (last registered first)
        for (handler in handlers.asReversed()) {
            if (handler.canHandle()) {
                handler.handle()
                return true
            }
        }
        return false
    }

    fun wouldConsumeBackPress(checkIfVisibleInToolbar: Boolean = false): Boolean {
        return handlers.asReversed().any { it.canHandle() && (!checkIfVisibleInToolbar || it.visibleInToolbar) }
    }
}

@Composable
fun rememberBackHandlerRegistry(): BackHandlerRegistry {
    return remember { BackHandlerRegistry() }
}

/**
 * wird benutzt um globele App Events zu registrieren
 * NavigationDrawer, SelectionToolbar, etc...
 * kann natürlich bei Bedarf von anderen globalen Handlern genutzt werden
 */
@Composable
fun RegisterBackHandler(
    key: Any? = Unit,
    canHandle: () -> Boolean,
    handle: () -> Unit,
    visibleInToolbar: Boolean= false
) {
    val backHandlerRegistry = LocalBackHandlerRegistry.current
    DisposableEffect(key) {
        val unregister = backHandlerRegistry.register(BackHandler(canHandle, handle, visibleInToolbar))
        onDispose { unregister() }
    }
}

/**
 * wird benutzt um globele App Events zu registrieren
 * NavigationDrawer, SelectionToolbar, etc...
 * kann natürlich bei Bedarf von anderen globalen Handlern genutzt werden
 */
@Composable
fun RegisterBackHandler(
    key1: Any ?,
    key2: Any?,
    canHandle: () -> Boolean,
    handle: () -> Unit,
    visibleInToolbar: Boolean= false
) {
    val backHandlerRegistry = LocalBackHandlerRegistry.current
    DisposableEffect(key1, key2) {
        val unregister = backHandlerRegistry.register(BackHandler(canHandle, handle, visibleInToolbar))
        onDispose { unregister() }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavigatorBackHandler(
    navigator: Navigator,
) {
    val backHandlerRegistry = LocalBackHandlerRegistry.current
    BackHandler(enabled = backHandlerRegistry.wouldConsumeBackPress() || navigator.canPop) {
        if (backHandlerRegistry.handleBackPress()) {
            L.logIf(ToolboxLogging.Tag.Navigation)?.i { "AppNavigator::BackHandler handled by BackHandlerRegistry" }
            return@BackHandler
        }
        L.logIf(ToolboxLogging.Tag.Navigation)?.i { "AppNavigator::BackHandler forwarded to navigator.pop()" }
        navigator.pop()
    }
}



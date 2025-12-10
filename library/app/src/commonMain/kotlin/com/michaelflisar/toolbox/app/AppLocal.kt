package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.features.backhandlerregistry.LocalBackHandlerRegistry
import com.michaelflisar.toolbox.app.features.backhandlerregistry.RegisterBackHandler
import com.michaelflisar.toolbox.app.features.backhandlerregistry.rememberBackHandlerRegistry
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.toolbar.LocalToolbarMainMenuItems
import com.michaelflisar.toolbox.app.features.toolbar.MainMenuItems
import com.michaelflisar.toolbox.app.features.toolbar.selection.LocalSelectionToolbarState
import com.michaelflisar.toolbox.logIf

/**
 * stellt app spezifische Locals zur Verfügung welche global und überall verfügbar sein sollen
 * bereits außerhalb von root!
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProvideAppLocals(
    content: @Composable () -> Unit,
) {
    val backHandlerRegistry = rememberBackHandlerRegistry()
    CompositionLocalProvider(
        LocalBackHandlerRegistry provides backHandlerRegistry
    ) {
        // Level 1: Selection Toolbar
        // Level 2: später registriert sich ggf. auch der Debug Drawer
        // Level 3: Settings Page registriert die eigene logik auch darin
        val selectionToolbarState = LocalSelectionToolbarState.current
        RegisterBackHandler(
            canHandle = {
                val handle = selectionToolbarState.isInSelectionMode
                println("SelectionToolbarState: canHandleBackPress = $handle")
                handle
            },
            handle = {
                L.logIf(ToolboxLogging.Tag.Navigation)?.i { "BackHandlerRegistry - SelectionToolbarState handles back press" }
                selectionToolbarState.clearSelection(finish = true)
            }
        )

        content()
    }
}
package com.michaelflisar.toolbox.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.demo.pages.PageHomeScreen
import com.michaelflisar.toolbox.demo.pages.PageSettingsScreen

object SharedActions {

    fun pageHome() = ActionItem.Page(
        title = "Home",
        imageVector = Icons.Default.Home,
        screen = PageHomeScreen
    )

    fun pageSettings() = ActionItem.Page(
        title = "Settings",
        imageVector = Icons.Default.Settings,
        screen = PageSettingsScreen
    )

    @Composable
    fun actionProVersion(): ActionItem.Action {
        val appState = LocalAppState.current
        return ActionItem.Action(
            title = "Pro Version",
            imageVector = Icons.Default.Shop,
            action = {
                appState.showSnackbar("Pro Version clicked")
            }
        )
    }

    @Composable
    fun actionChangelog(): ActionItem.Action {
        val appState = LocalAppState.current
        return ActionItem.Action(
            title = "Changelog",
            imageVector = Icons.Default.TextSnippet,
            action = {
                appState.changelogState.show()
            }
        )
    }

    @Composable
    fun actionTest(): ActionItem.Action {
        val appState = LocalAppState.current
        return ActionItem.Action(
            title = "TEST Action",
            imageVector = Icons.Default.ArrowRight,
            action = {
                appState.showSnackbar("Test clicked")
            }
        )
    }
}
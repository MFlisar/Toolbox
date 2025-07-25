package com.michaelflisar.toolbox.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.demo.pages.Page2Screen
import com.michaelflisar.toolbox.demo.pages.PageHomeScreen
import com.michaelflisar.toolbox.demo.pages.PageTestsRootScreenContainer
import com.michaelflisar.toolbox.demo.pages.PageSettingsScreen
import com.michaelflisar.toolbox.extensions.toIconComposable

object SharedActions {

    @Composable
    fun pageHome() = PageHomeScreen.toActionItem()

    @Composable
    fun page2() = Page2Screen.toActionItem()

    @Composable
    fun pageMultiLevelRoot() = PageTestsRootScreenContainer.toActionItem()

    @Composable
    fun pageSettings() = PageSettingsScreen.toActionItem()

    @Composable
    fun actionProVersion(): ActionItem.Action {
        val appState = LocalAppState.current
        return ActionItem.Action(
            title = "Pro Version",
            icon = Icons.Default.Shop.toIconComposable(),
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
            icon = Icons.Default.TextSnippet.toIconComposable(),
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
            icon = Icons.Default.ArrowRight.toIconComposable(),
            action = {
                appState.showSnackbar("Test clicked")
            }
        )
    }
}
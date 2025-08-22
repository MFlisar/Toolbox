package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.navigation.screen.INavScreen

interface INavigationDefinition {

    val pagesMain: List<INavScreen>
    val pageSetting: INavScreen

    // -------------------------
    // Actions
    // -------------------------

    @Composable
    fun actionProVersion(): ActionItem.Action

    @Composable
    fun actionCustom(): List<ActionItem.Action>

}
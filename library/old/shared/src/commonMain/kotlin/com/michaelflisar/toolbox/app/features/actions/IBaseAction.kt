package com.michaelflisar.toolbox.app.features.actions

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.feature.menu.MenuScope

interface IBaseAction {

    @Composable
    fun toNavItem(): INavItem

    @Composable
    fun toMenuItem(): MenuItem

    @Composable
    fun PopupMenuItem(scope: MenuScope)
}
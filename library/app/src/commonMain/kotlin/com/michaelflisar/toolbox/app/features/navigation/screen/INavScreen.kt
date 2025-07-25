package com.michaelflisar.toolbox.app.features.navigation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.parcelize.Parcelable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.menu.MenuItem

sealed interface INavScreen : Screen, Parcelable {

    @Composable
    fun provideData(): State<NavScreenData>

    @Composable
    fun provideMenu(): List<MenuItem>

    @Composable
    fun toActionItem(): ActionItem.Page {
        val data = provideData()
        return ActionItem.Page(
            title = data.value.title,
            icon = data.value.icon,
            screen = this
        )
    }

}
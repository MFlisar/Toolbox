package com.michaelflisar.toolbox.app.features.navigation.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.Parcelable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.feature.menu.MenuItem
import com.michaelflisar.toolbox.feature.menu.MenuScope

interface INavScreen : Screen, Parcelable {

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

    @Composable
    fun toMenuItem(
        hideIconInDialog: Boolean = true,
        hideTitleIfIconIsAvailable: Boolean = false,
    ): MenuItem {
        return toActionItem().toMenuItem(
            hideIconInDialog = hideIconInDialog,
            hideTitleIfIconIsAvailable = hideTitleIfIconIsAvailable
        )
    }

    @Composable
    fun toNavItem() = toActionItem().toNavItem()

    @Composable
    fun PopupMenuItem(scope: MenuScope) {
        val titleData = provideData()
        val navigator = LocalNavigator.currentOrThrow
        with(scope) {
            MenuItem(
                text = { Text(titleData.value.title) },
                icon = titleData.value.icon
            ) {
                navigator.replaceAll(this@INavScreen)
            }
        }
    }

    val navScreenBackPressHandler: NavScreenBackPressHandler?
        get() = null

}
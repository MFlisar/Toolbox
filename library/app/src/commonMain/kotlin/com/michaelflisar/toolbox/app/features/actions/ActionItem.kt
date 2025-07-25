package com.michaelflisar.toolbox.app.features.actions

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.device.Device
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.toolbar.onToolbar
import com.michaelflisar.toolbox.app.features.toolbar.toolbar
import com.michaelflisar.toolbox.extensions.Render
import com.michaelflisar.toolbox.extensions.isDark

sealed class ActionItem {

    class Page(
        val title: String,
        val icon: IconComposable? = null,
        val screen: Screen,
    ) : ActionItem() {

        fun toNavItem(): NavItem {
            return NavItem(
                title = title,
                icon = icon,
                screen = screen
            )
        }

        @Composable
        fun toMenuItem(
            hideIconInDialog: Boolean = true,
            hideTitleIfIconIsAvailable: Boolean = false,
        ): MenuItem {
            val state = rememberDialogState()
            if (state.visible) {
                val style = when (CurrentDevice.base) {
                    BaseDevice.Desktop,
                    BaseDevice.Web -> {
                        DialogDefaults.styleBottomSheet(
                            peekHeight = null,
                            expandInitially = true,
                            animateShow = true
                        )
                    }
                    BaseDevice.Mobile -> {
                        DialogDefaults.styleFullscreenDialog(
                            darkStatusBar = MaterialTheme.colorScheme.toolbar.isDark(),
                            toolbarColor = MaterialTheme.colorScheme.toolbar,
                            toolbarActionColor = MaterialTheme.colorScheme.onToolbar,
                            //containerColor = MaterialTheme.colorScheme.toolbar,
                            iconColor = MaterialTheme.colorScheme.onToolbar,
                            titleColor = MaterialTheme.colorScheme.onToolbar,
                            //contentColor = MaterialTheme.colorScheme.onToolbar
                        )
                    }
                }
                Dialog(
                    state = state,
                    title = { Text(title) },
                    icon = icon?.takeIf { !hideIconInDialog }?.let { { it.Render() } },
                    style = style,
                    buttons = DialogDefaults.buttonsDisabled()
                ) {
                    //AppNavigator(screen)
                    screen.Content()
                }
            }
            return MenuItem.Item(
                title.takeIf { !hideTitleIfIconIsAvailable || icon == null } ?: "",
                icon
            ) {
                state.show()
            }
        }
    }

    class Action(
        val title: String,
        val icon: IconComposable? = null,
        val action: () -> Unit,
    ) : ActionItem() {

        fun toNavItem(): NavItemAction {
            return NavItemAction(
                title = title,
                icon = icon,
                action = action
            )
        }

        fun toMenuItem(): MenuItem {
            return MenuItem.Item(title, icon) {
                action()
            }
        }
    }
}
package com.michaelflisar.toolbox.app.features.actions

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.defaultDialogStyle
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composedialogs.core.style.DialogStyleDefaults
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.toolbar.onToolbar
import com.michaelflisar.toolbox.app.features.toolbar.toolbar
import com.michaelflisar.toolbox.isDark

sealed class ActionItem {

    class Page(
        val title: String,
        val imageVector: ImageVector?,
        val screen: Screen
    ) : ActionItem() {

        fun toNavItem(): NavItem {
            return NavItem(
                title = title,
                imageVector = imageVector,
                screen = screen
            )
        }

        @Composable
        fun toMenuItem(): MenuItem {
            val state = rememberDialogState()
            if (state.visible) {
                Dialog(
                    state = state,
                    title = { Text(title) },
                    icon = imageVector?.let { { Icon(imageVector, null) } },
                    //style = DialogDefaults.styleFullscreenDialog(
                    //    darkStatusBar = MaterialTheme.colorScheme.toolbar.isDark(),
                    //    toolbarColor = MaterialTheme.colorScheme.toolbar,
                    //    toolbarActionColor = MaterialTheme.colorScheme.onToolbar,
                    //    //containerColor = MaterialTheme.colorScheme.toolbar,
                    //    iconColor = MaterialTheme.colorScheme.onToolbar,
                    //    titleColor = MaterialTheme.colorScheme.onToolbar,
                    //    //contentColor = MaterialTheme.colorScheme.onToolbar
                    //),
                    style = DialogDefaults.styleBottomSheet(
                        peekHeight = null,
                        expandInitially = true,
                        animateShow = true
                    ),
                    buttons = DialogDefaults.buttonsDisabled()
                ) {
                    screen.Content()
                }
            }
            return MenuItem.Item(title, imageVector) {
                state.show()
            }
        }
    }

    class Action(
        val title: String,
        val imageVector: ImageVector? = null,
        val action: () -> Unit
    ) : ActionItem() {

        fun toNavItem(): NavItemAction {
            return NavItemAction(
                title = title,
                imageVector = imageVector,
                action = action
            )
        }

        fun toMenuItem(): MenuItem {
            return MenuItem.Item(title, imageVector) {
                action()
            }
        }
    }
}
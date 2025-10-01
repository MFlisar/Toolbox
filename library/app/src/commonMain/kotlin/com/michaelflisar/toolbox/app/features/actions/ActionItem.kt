package com.michaelflisar.toolbox.app.features.actions

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
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
                        val navScreen = screen as? NavScreen
                        val screenCanHandleBackPress = navScreen?.navScreenBackPressHandler?.canHandle()
                        DialogDefaults.styleFullscreenDialog(
                            navigationIcon = {
                                Crossfade(
                                    targetState = screenCanHandleBackPress == true
                                ) { canHandle ->
                                    if (canHandle) {
                                        IconButton(
                                            onClick = {
                                                navScreen?.navScreenBackPressHandler?.handle()
                                            }
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                        }
                                    } else {
                                        IconButton(
                                            onClick = {
                                                state.dismiss()
                                            }
                                        ) {
                                            Icon(Icons.Default.Close, null)
                                        }
                                    }
                                }
                            },
                            darkStatusBar = MaterialTheme.colorScheme.toolbar.isDark(),
                            toolbarColor = MaterialTheme.colorScheme.toolbar,
                            toolbarActionColor = MaterialTheme.colorScheme.onToolbar,
                            //containerColor = MaterialTheme.colorScheme.toolbar,
                            iconColor = MaterialTheme.colorScheme.onToolbar,
                            titleColor = MaterialTheme.colorScheme.onToolbar,
                            //contentColor = MaterialTheme.colorScheme.onToolbar
                            applyContentPadding = false
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
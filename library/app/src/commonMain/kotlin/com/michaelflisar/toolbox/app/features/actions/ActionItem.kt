package com.michaelflisar.toolbox.app.features.actions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.features.backhandlerregistry.LocalBackHandlerRegistry
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.navigation.screen.INavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.toolbar.onToolbar
import com.michaelflisar.toolbox.app.features.toolbar.toolbar
import com.michaelflisar.toolbox.extensions.Icon
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

        @OptIn(ExperimentalMaterial3Api::class)
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
                        val backHandlerRegistry = LocalBackHandlerRegistry.current
                        val screenCanHandleBackPress = backHandlerRegistry.wouldConsumeBackPress(true)

                        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                            canScroll = { !screenCanHandleBackPress}
                        )
                        val isNearerToExpanded = remember(scrollBehavior) {
                            derivedStateOf { scrollBehavior.state.collapsedFraction < .5f }
                        }
                        LaunchedEffect(screenCanHandleBackPress) {
                            if (screenCanHandleBackPress) {
                                animate(
                                    initialValue = scrollBehavior.state.heightOffset,
                                    targetValue = scrollBehavior.state.heightOffsetLimit,
                                    animationSpec = tween(300)
                                ) { value, _ ->
                                    scrollBehavior.state.heightOffset = value
                                }
                                //scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffsetLimit
                            }
                        }
                        DialogDefaults.styleFullscreenDialog(
                            toolbarScrollBehaviour = scrollBehavior,
                            navigationIcon = {
                                Crossfade(
                                    targetState = screenCanHandleBackPress
                                ) { canHandle ->
                                    if (canHandle) {
                                        IconButton(
                                            onClick = {
                                                backHandlerRegistry.handleBackPress()
                                            }
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                        }
                                    } else {
                                        AnimatedVisibility(
                                            visible = !isNearerToExpanded.value,
                                            enter = fadeIn(),
                                            exit = fadeOut()
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    state.dismiss()
                                                }
                                            ) {
                                                Icon(Icons.Default.Close, null)
                                            }
                                        }
                                    }
                                }
                            },
                            toolbarColor = MaterialTheme.colorScheme.toolbar,
                            toolbarColorExpanded = MaterialTheme.colorScheme.background,
                            toolbarContentColor = MaterialTheme.colorScheme.onToolbar,
                            toolbarContentColorExpanded = MaterialTheme.colorScheme.onBackground,
                            toolbarActionColor = MaterialTheme.colorScheme.onToolbar,
                            toolbarActionColorExpanded = MaterialTheme.colorScheme.onBackground,
                            iconColor = MaterialTheme.colorScheme.onToolbar,
                            applyContentPadding = false
                        )
                    }
                }
                Dialog(
                    state = state,
                    title = { Text(title) },
                    icon = icon?.takeIf { !hideIconInDialog }?.let { {
                        Icon(it)
                    } },
                    style = style,
                    buttons = DialogDefaults.buttonsDisabled()
                ) {
                    //AppNavigator(screen)
                    //Column {
                    //    if (screen is INavScreen)
                    //        screen.Toolbar()
                        screen.Content()
                    //}

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
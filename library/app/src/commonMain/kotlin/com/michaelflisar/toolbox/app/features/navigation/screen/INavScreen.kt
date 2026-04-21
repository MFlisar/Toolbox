package com.michaelflisar.toolbox.app.features.navigation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogState
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.parcelize.Parcelable
import com.michaelflisar.toolbox.app.features.backhandlerregistry.LocalBackHandlerRegistry
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.Current
import com.michaelflisar.toolbox.app.features.device.Device
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.toolbar.onToolbar
import com.michaelflisar.toolbox.app.features.toolbar.toolbar
import com.michaelflisar.toolbox.extensions.Icon
import com.michaelflisar.toolbox.feature.menu.MenuItem
import com.michaelflisar.toolbox.feature.menu.MenuScope

interface INavScreen : Screen, Parcelable {

    @Composable
    fun provideData(): NavScreenData

    @Composable
    fun Toolbar()

    @Composable
    fun toNavItem(): NavItem {
        val data = if (this is NavScreenContainer) {
            provideRootData()
        } else provideData()
        return NavItem(
            title = data.name,
            icon = data.icon,
            screen = this
        )
    }

    @Composable
    fun toMenuItem(
        openInDialog: Boolean = false,
        expandableHeaderOnMobileInDialog: Boolean = false,
    ): MenuItem.Item {
        val data = if (this is NavScreenContainer) {
            provideRootData()
        } else provideData()

        return if (!openInDialog) {
            val navigator = LocalNavigator.currentOrThrow
            MenuItem.Item(
                text = data.name,
                icon = data.icon
            ) {
                navigator.replaceAll(this@INavScreen)
            }
        } else {
            val state = rememberDialogState()
            ShowAsDialog(
                state = state,
                hideIconInDialog = true,
                expandableHeaderOnMobile = expandableHeaderOnMobileInDialog
            )
            MenuItem.Item(
                text = data.name,
                icon = data.icon
            ) {
                state.show()
            }
        }
    }

    @Composable
    fun PopupMenuItem(scope: MenuScope) {
        val titleData = provideData()
        val navigator = LocalNavigator.currentOrThrow
        with(scope) {
            MenuItem(
                text = { Text(titleData.name) },
                icon = titleData.icon
            ) {
                navigator.replaceAll(this@INavScreen)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ShowAsDialog(
        state: DialogState,
        hideIconInDialog: Boolean,
        expandableHeaderOnMobile: Boolean,
    ) {
        if (state.visible) {

            val data = if (this is NavScreenContainer) {
                provideRootData()
            } else provideData()

            val style = when (Device.Current.base) {
                BaseDevice.Desktop,
                BaseDevice.Web,
                    -> {
                    DialogDefaults.styleBottomSheet(
                        peekHeight = null,
                        expandInitially = true,
                        animateShow = true
                    )
                }

                BaseDevice.Mobile -> {
                    val backHandlerRegistry = LocalBackHandlerRegistry.current
                    val screenCanHandleBackPress =
                        backHandlerRegistry.wouldConsumeBackPress(true)

                    val scrollBehavior = if (expandableHeaderOnMobile) {
                        TopAppBarDefaults.enterAlwaysScrollBehavior(
                            canScroll = { !screenCanHandleBackPress }
                        )
                    } else null
                    val isNearerToExpanded = remember(scrollBehavior) {
                        derivedStateOf { (scrollBehavior?.state?.collapsedFraction ?: 1f) < .5f }
                    }
                    if (scrollBehavior != null) {
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
                                        androidx.compose.material3.Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    AnimatedVisibility(
                                        visible = true,//!isNearerToExpanded.value,
                                        enter = fadeIn(),
                                        exit = fadeOut()
                                    ) {
                                        IconButton(
                                            onClick = {
                                                state.dismiss()
                                            }
                                        ) {
                                            androidx.compose.material3.Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null
                                            )
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
                title = { Text(data.name) },
                icon = data.icon?.takeIf { !hideIconInDialog }?.let {
                    {
                        Icon(it)
                    }
                },
                style = style,
                buttons = DialogDefaults.buttonsDisabled()
            ) {
                this.Content()
            }
        }
    }
}
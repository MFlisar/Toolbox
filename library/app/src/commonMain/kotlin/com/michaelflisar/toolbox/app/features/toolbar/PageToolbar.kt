package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.features.backhandlerregistry.LocalBackHandlerRegistry
import com.michaelflisar.toolbox.app.features.navigation.findLocalByScreenOrThrow
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import com.michaelflisar.toolbox.app.features.navigation.navItemContainer
import com.michaelflisar.toolbox.app.features.navigation.screen.INavScreen
import com.michaelflisar.toolbox.app.features.scaffold.LocalPlatformStyle
import com.michaelflisar.toolbox.app.features.toolbar.composables.ToolbarBackButton
import com.michaelflisar.toolbox.app.features.toolbar.composables.ToolbarTitle
import com.michaelflisar.toolbox.app.platform.ResolvedPlatformStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageToolbar(
    screen: INavScreen,
    title: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    subTitle: String? = null,
    endContent: @Composable RowScope.() -> Unit = {},
    canGoBack: Boolean = false,
    applyBackgroundColor: Boolean = false,
    onBack: (() -> Unit)? = null,
) {
    val backHandlerRegistry = LocalBackHandlerRegistry.current
    val navigator = LocalNavigator.currentOrThrow.findLocalByScreenOrThrow
    val navScreenContainer = LocalNavigator.currentOrThrow.navItemContainer
    val navScreenRootNavigator = navScreenContainer?.rootNavigator?.value
    TopAppBar(
        modifier = modifier,
        title = {
            val platformStyle = LocalPlatformStyle.current
            if (platformStyle == ResolvedPlatformStyle.Desktop) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    ToolbarTitle(
                        title = title,
                        subTitle = subTitle,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                }
            } else {
                ToolbarTitle(
                    modifier = Modifier,
                    title = title,
                    subTitle = subTitle,
                    horizontalAlignment = Alignment.Start
                )
            }
        },
        expandedHeight = TopAppBarDefaults.TopAppBarExpandedHeight,
        windowInsets = TopAppBarDefaults.windowInsets,
        scrollBehavior = null,
        navigationIcon = {
            // canPop nur ändern solange der Screen auch wirklich der aktuelle ist,
            // damit nicht plötzlich der Back Button verschwindet,
            // wenn man bspw. zu einem anderen Screen wechselt
            val canPop = remember { mutableStateOf(navigator.canPop) }
            LaunchedEffect(navigator.lastNavItem, navScreenContainer) {
                if (navigator.lastNavItem == screen) {
                    canPop.value =
                        navigator.canPop || navScreenContainer?.supportRootBackButton == true
                }
            }

            if (canGoBack || canPop.value || backHandlerRegistry.wouldConsumeBackPress(true)) {
                ToolbarBackButton(
                    modifier = Modifier,
                    onClick = {
                        if (!backHandlerRegistry.handleBackPress()) {
                            if (canGoBack)
                                onBack?.invoke()
                            else {
                                if (navigator.canPop)
                                    navigator.pop()
                                else if (navScreenContainer?.supportRootBackButton == true && navScreenRootNavigator != null)
                                    navScreenRootNavigator.pop()
                            }

                        }
                    }
                )
            }
        },
        actions = {
            endContent()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (applyBackgroundColor) MaterialTheme.colorScheme.toolbar else Color.Transparent,
            scrolledContainerColor = if (applyBackgroundColor) MaterialTheme.colorScheme.toolbar else Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onToolbar,
            navigationIconContentColor = MaterialTheme.colorScheme.onToolbar,
            actionIconContentColor = MaterialTheme.colorScheme.onToolbar,
        )
    )
}
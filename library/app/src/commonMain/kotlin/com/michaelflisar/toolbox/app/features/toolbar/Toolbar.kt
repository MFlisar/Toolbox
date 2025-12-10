package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.features.backhandlerregistry.LocalBackHandlerRegistry
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.menu.Menu
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.menu.removeConsecutiveSeparators
import com.michaelflisar.toolbox.app.features.navigation.findLocalByScreenOrThrow
import com.michaelflisar.toolbox.extensions.toIconComposable

val LocalToolbarMainMenuItems = staticCompositionLocalOf<MainMenuItems> { MainMenuItems() }

@Stable
class MainMenuItems(
    val itemProVersion: MenuItem? = null,
    val itemSettings: MenuItem? = null,
    val customActions: List<MenuItem> = emptyList(),
) {
    fun combineWith(
        items: List<MenuItem>,
        dividerAfterProVersion: Boolean = true,
        dividerBeforeSettings: Boolean = true,
    ): List<MenuItem> {
        val list = mutableListOf<MenuItem>()
        itemProVersion?.let {
            list.add(it)
            if (dividerAfterProVersion)
                list.add(MenuItem.Separator())
        }
        list.addAll(customActions)
        list.addAll(items)
        itemSettings?.let {
            if (dividerBeforeSettings)
                list.add(MenuItem.Separator())
            list.add(it)
        }
        return list.removeConsecutiveSeparators()
    }

    fun getAsOverflowMenuItems(
        dividerAfterProVersion: Boolean = true,
        dividerBeforeSettings: Boolean = true,
    ): List<MenuItem> {
        val items = combineWith(
            items = emptyList(),
            dividerAfterProVersion = dividerAfterProVersion,
            dividerBeforeSettings = dividerBeforeSettings
        )
        if (items.isEmpty())
            return emptyList()
        return listOf(
            MenuItem.Group(
                icon = Icons.Default.MoreVert.toIconComposable(),
                items = items
            )
        )
    }
}

@Composable
fun MainMenuItemsContentOnly(
    dividerAfterProVersion: Boolean = true,
    dividerBeforeSettings: Boolean = true,
) {
    val mainMenuItems = LocalToolbarMainMenuItems.current
    val items =
        mainMenuItems.combineWith(emptyList(), dividerAfterProVersion, dividerBeforeSettings)
    return if (items.isEmpty()) {
        return
    } else {
        Menu(items)
    }
}

@Composable
fun SharedToolbarContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier.background(color = MaterialTheme.colorScheme.toolbar)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
    endContent: @Composable RowScope.() -> Unit = {},
    canGoBack: Boolean = false,
    onBack: (() -> Unit)? = null,
) {
    val backHandlerRegistry = LocalBackHandlerRegistry.current
    val navigator = LocalNavigator.currentOrThrow.findLocalByScreenOrThrow
    TopAppBar(
        modifier = modifier,
        title = {
            if (CurrentDevice.base == BaseDevice.Desktop) {
                Box(
                    modifier = modifier.fillMaxWidth(),
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
            if (canGoBack || navigator.canPop || backHandlerRegistry.wouldConsumeBackPress(true)) {
                ToolbarBackButton(
                    onClick = {
                        if (!backHandlerRegistry.handleBackPress())
                        {
                            if (canGoBack)
                                onBack?.invoke()
                            else
                                navigator.pop()
                        }
                    }
                )
            }
        },
        actions = {
            endContent()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onToolbar,
            navigationIconContentColor = MaterialTheme.colorScheme.onToolbar,
            actionIconContentColor = MaterialTheme.colorScheme.onToolbar,
        )
    )
}
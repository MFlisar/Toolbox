package com.michaelflisar.toolbox.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.WebAsset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.classes.resetWindowPosition
import com.michaelflisar.toolbox.app.classes.resetWindowSize
import com.michaelflisar.toolbox.app.features.appstate.LocalJewelAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.menu.removeConsecutiveSeparators
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionSetup
import com.michaelflisar.toolbox.app.jewel.LocalJewelWindowState
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.utils.JvmUtil
import kotlinx.coroutines.launch

object DesktopAppDefaults {

    fun getDefaultStatusBarItemsLeft(
        showAppVersionLeft: Boolean = true,
        onAppVersionClick: (() -> Unit)? = null,
    ): List<DesktopStatusBarItem> {
        val setup = AppSetup.get()
        return listOfNotNull(
            DesktopStatusBarItem.Text(
                "App Version: ${setup.versionName}",
                onClick = onAppVersionClick
            ).takeIf { showAppVersionLeft }
        )
    }

    fun getDefaultStatusBarItemsRight(
        showJavaVersionRight: Boolean = true,
        showUserNameRight: Boolean = true,
        showHostNameRight: Boolean = true,
        onJavaVersionClick: (() -> Unit)? = null,
        onUserNameClick: (() -> Unit)? = null,
        onHostNameClick: (() -> Unit)? = null,
    ): List<DesktopStatusBarItem> {
        return listOfNotNull(
            DesktopStatusBarItem.Text(JvmUtil.javaVersion(), onClick = onJavaVersionClick)
                .takeIf { showJavaVersionRight },
            DesktopStatusBarItem.Text(JvmUtil.userName(), onClick = onUserNameClick)
                .takeIf { showUserNameRight },
            DesktopStatusBarItem.Text(JvmUtil.hostName(), onClick = onHostNameClick)
                .takeIf { showHostNameRight }
        )
    }

    @Composable
    fun getDesktopMenuItems(
        customActions: List<MenuItem>,
        menuLabel: String = "App",
        menuWindowLabel: String = "Window",
        menuDevLabel: String = "Dev",
        menuIcon: IconComposable? = Icons.Default.Apps.toIconComposable(),
        menuDevIcon: IconComposable? = Icons.Default.BugReport.toIconComposable(),
        labelResetWindowSize: String = "Reset Window Size",
        labelResetWindowPosition: String = "Reset Window Position",
        labelOpenDebugDrawer: String = "Open Debug Drawer",
        labelCloseDebugDrawer: String = "Close Debug Drawer",
        resetWindowSize: Boolean = true,
        resetWindowPosition: Boolean = true,
    ): List<MenuItem> {

        val jewelAppState = LocalJewelAppState.current
        val windowState = LocalJewelWindowState.current
        val density = LocalDensity.current

        val scope = rememberCoroutineScope()
        val setup = AppSetup.get()
        val resetWindowMenuItems = listOfNotNull(
            resetWindowSize.takeIf { it }?.let {
                MenuItem.Item(
                    labelResetWindowSize,
                    Icons.Default.Clear.toIconComposable()
                ) {
                    scope.launch { jewelAppState.windowState.resetWindowSize() }
                }
            },
            resetWindowPosition.takeIf { it }?.let {
                MenuItem.Item(
                    labelResetWindowPosition,
                    Icons.Default.Clear.toIconComposable()
                ) {
                    scope.launch { jewelAppState.windowState.resetWindowPosition(density, windowState) }
                }
            }
        )
        val drawerState = LocalDebugDrawerState.current
        val showDebugDrawer by setup.debugPrefs.showDebugDrawer.collectAsStateNotNull()
        val debugMenu = if (showDebugDrawer) {
            MenuItem.Group(
                text = menuDevLabel,
                icon = menuDevIcon,
                items = listOf(
                    MenuItem.Item(
                        icon = if (drawerState.drawerState.isOpen) Icons.Default.ChevronRight.toIconComposable() else Icons.Default.ChevronLeft.toIconComposable(),
                        text = if (drawerState.drawerState.isOpen) labelCloseDebugDrawer else labelOpenDebugDrawer
                    ) {
                        scope.launch {
                            if (drawerState.drawerState.isOpen)
                                drawerState.drawerState.close()
                            else
                                drawerState.drawerState.open()
                        }
                    }
                )
            )
        } else {
            null
        }

        val proVersionManager = ProVersionManager.setup
        val proVersion by proVersionManager.proState.collectAsState()

        // 1) Pro Version + Separator
        val itemsProVersion = if (proVersion == ProState.Yes) {
            emptyList()
        } else {
            listOfNotNull(
                (proVersionManager as? ProVersionSetup.Supported)?.action?.invoke()?.toMenuItem()
            )
        }

        // 2) custom actions

        val subItems = listOfNotNull(
            itemsProVersion,
            customActions,
            MenuItem.Separator().let { listOf(it) },
            MenuItem.Group(
                text = menuWindowLabel,
                icon = Icons.Default.WebAsset.toIconComposable(),
                items = resetWindowMenuItems
            ).takeIf { it.items.isNotEmpty() }?.let { listOf(it) }
        )
            .flatten()
            .removeConsecutiveSeparators()

        return listOfNotNull(
            MenuItem.Group(
                text = menuLabel,
                items = subItems,
                icon = menuIcon,
            ).takeIf { it.items.isNotEmpty() },
            debugMenu
        )
    }
}
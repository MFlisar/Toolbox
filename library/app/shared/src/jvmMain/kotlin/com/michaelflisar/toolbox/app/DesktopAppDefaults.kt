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
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.classes.DesktopAppSetup
import com.michaelflisar.toolbox.app.classes.resetWindowPosition
import com.michaelflisar.toolbox.app.classes.resetWindowSize
import com.michaelflisar.toolbox.app.features.appstate.LocalDesktopAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.menu.removeConsecutiveSeparators
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionSetup
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
                "App Version: ${setup.appData.versionName}",
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
        menuIcon: ImageVector? = Icons.Default.Apps,
        menuDevIcon: ImageVector? = Icons.Default.BugReport,
        labelResetWindowSize: String = "Reset Window Size",
        labelResetWindowPosition: String = "Reset Window Position",
        labelOpenDebugDrawer: String = "Open Debug Drawer",
        labelCloseDebugDrawer: String = "Close Debug Drawer",
        resetWindowSize: Boolean = DesktopAppSetup.get().rememberWindowState,
        resetWindowPosition: Boolean = DesktopAppSetup.get().rememberWindowState,
    ): List<MenuItem> {

        val jewelAppState = LocalDesktopAppState.current
        val composeWindow = LocalComposeWindow.current
        val density = LocalDensity.current

        val scope = rememberCoroutineScope()
        val setup = AppSetup.get()
        val resetWindowMenuItems = listOfNotNull(
            resetWindowSize.takeIf { it }?.let {
                MenuItem.item(
                    labelResetWindowSize,
                    Icons.Default.Clear
                ) {
                    scope.launch { jewelAppState.windowState.resetWindowSize() }
                }
            },
            resetWindowPosition.takeIf { it }?.let {
                MenuItem.item(
                    labelResetWindowPosition,
                    Icons.Default.Clear
                ) {
                    scope.launch {
                        jewelAppState.windowState.resetWindowPosition(
                            density = density,
                            window = composeWindow
                        )
                    }
                }
            }
        )
        val drawerState = LocalDebugDrawerState.current
        val showDebugDrawer by setup.debugPrefs.showDebugDrawer.collectAsStateNotNull()
        val debugMenu = if (showDebugDrawer) {
            MenuItem.group(
                text = menuDevLabel,
                icon = menuDevIcon,
                items = listOf(
                    MenuItem.item(
                        icon = if (drawerState.drawerState.isOpen) Icons.Default.ChevronRight else Icons.Default.ChevronLeft,
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
            listOf(MenuItem.Separator()),
            MenuItem.group(
                text = menuWindowLabel,
                icon = Icons.Default.WebAsset,
                items = resetWindowMenuItems
            ).takeIf { it.items.isNotEmpty() }?.let { listOf(it) }
        )
            .flatten()
            .removeConsecutiveSeparators()

        return listOfNotNull(
            MenuItem.group(
                text = menuLabel,
                items = subItems,
                icon = menuIcon,
            ).takeIf { it.items.isNotEmpty() },
            debugMenu
        )
    }
}
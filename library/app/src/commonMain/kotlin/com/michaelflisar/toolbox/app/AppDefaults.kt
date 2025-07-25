package com.michaelflisar.toolbox.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.WebAsset
import androidx.compose.material.icons.filled.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.michaelflisar.composechangelog.DefaultVersionFormatter
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemRegion
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer
import com.michaelflisar.toolbox.extensions.toIconComposable
import kotlinx.coroutines.launch

object AppDefaults {

    val CHANGELOG_PATH = "files/changelog.xml"
    val CHANGELOG_FORMATTER =
        DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)

    @Composable
    fun provideNavigationItems(
        pagesMain: List<ActionItem.Page>,
        pageSetting: ActionItem.Page,
        actionsMain: List<ActionItem.Action>,
        actionsMenu: List<ActionItem.Action>,
        mainPagesLabel: String = "Pages",
        mainActionsLabel: String = "Actions",
    ): List<INavItem> {
        return when (CurrentDevice.base) {
            BaseDevice.Desktop -> {
                listOf(NavItemRegion(mainPagesLabel)) +
                        pagesMain.map { it.toNavItem() } +
                        NavItemRegion(mainActionsLabel) +
                        actionsMain.map { it.toNavItem() } +
                        NavItemSpacer() +
                        pageSetting.toNavItem()

            }

            BaseDevice.Web -> {
                pagesMain.map { it.toNavItem() }
            }

            BaseDevice.Mobile -> {
                pagesMain.map { it.toNavItem() }
            }
        }
    }

    @Composable
    fun provideAppMenu(
        pagesMain: List<ActionItem.Page>,
        pageSetting: ActionItem.Page,
        actionsMain: List<ActionItem.Action>,
        actionsMenu: List<ActionItem.Action>,
        menuLabel: String = "App",
        menuWindowLabel: String = "Window",
        resetWindowSize: (suspend () -> Unit)? = null,
        resetWindowPosition: (suspend () -> Unit)? = null,
    ): List<MenuItem> {
        val scope = rememberCoroutineScope()
        val setup = CommonApp.setup
        return when (CurrentDevice.base) {
            BaseDevice.Desktop -> {
                val resetWindowMenuItems = listOfNotNull(
                    resetWindowSize?.let {
                        MenuItem.Item("Reset Window Size", Icons.Default.Clear.toIconComposable()) {
                            scope.launch {
                                resetWindowSize()
                            }
                        }
                    },
                    resetWindowPosition?.let {
                        MenuItem.Item(
                            "Reset Window Position",
                            Icons.Default.Clear.toIconComposable()
                        ) {
                            scope.launch {
                                resetWindowPosition()
                            }
                        }
                    }
                )
                val drawerState = LocalDebugDrawerState.current
                val showDebugDrawer by setup.debugPrefs.showDebugDrawer.collectAsStateNotNull()
                val debugMenu = if (showDebugDrawer) {
                    MenuItem.Group(
                        text = "Dev",
                        items = listOf(
                            MenuItem.Item(
                                icon = if (drawerState.drawerState.isOpen) Icons.Default.ChevronRight.toIconComposable() else Icons.Default.ChevronLeft.toIconComposable(),
                                text = if (drawerState.drawerState.isOpen) "Close Debug Drawer" else "Open Debug Drawer"
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
                listOfNotNull(
                    MenuItem.Group(
                        text = menuLabel,
                        items = (
                                actionsMenu.map { it.toMenuItem() } +
                                        MenuItem.Separator() +
                                        MenuItem.Group(
                                            text = menuWindowLabel,
                                            icon = Icons.Default.WebAsset.toIconComposable(),
                                            items = resetWindowMenuItems
                                        ).takeIf { it.items.isNotEmpty() }
                                ).filterNotNull()
                    ).takeIf { it.items.isNotEmpty() },
                    debugMenu
                )
            }

            BaseDevice.Web -> {
                // wir wollen keine Labels im root level hier
                listOfNotNull(
                    pageSetting.toMenuItem(
                        hideTitleIfIconIsAvailable = true
                    ),
                    MenuItem.Group(
                        icon = Icons.Default.MoreVert.toIconComposable(),
                        items = actionsMenu.map { it.toMenuItem() } +
                                actionsMain.map { it.toMenuItem() }

                    ).takeIf { it.items.isNotEmpty() }
                )
            }

            BaseDevice.Mobile -> {
                listOfNotNull(
                    MenuItem.Group(
                        icon = Icons.Default.MoreVert.toIconComposable(),
                        items = actionsMenu.map { it.toMenuItem() } +
                                actionsMain.map { it.toMenuItem() } +
                                MenuItem.Separator() +
                                pageSetting.toMenuItem()
                    ).takeIf { it.items.isNotEmpty() }
                )
            }
        }
    }

}
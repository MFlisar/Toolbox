package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.WebAsset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.proversion.ProState
import com.michaelflisar.toolbox.extensions.toIconComposable
import kotlinx.coroutines.launch

object NavigationUtil {

    @Composable
    fun getRailNavigationItems(
        definition: INavigationDefinition,
        regionLabelMainPages: String?,
        regionLabelMainActions: String?,
    ): List<INavItem> {

        val itemsCustomAction = definition.actionCustom().map { it.toNavItem() }

        val itemsSetting = definition.pageSetting.toActionItem().toNavItem().let { listOf(it) }

        return listOfNotNull(
            regionLabelMainPages?.let { NavItemRegion(it) }?.let { listOf(it) }.takeIf { definition.pagesMain.isNotEmpty() },
            definition.pagesMain.map { it.toActionItem().toNavItem() },
            regionLabelMainActions?.let { NavItemRegion(it) }?.let { listOf(it) }.takeIf { itemsCustomAction.isNotEmpty() },
            itemsCustomAction,
            listOf(NavItemSpacer()),
            itemsSetting
        ).flatten()
    }

    /**
     * nur main pages als Navigation Items anzeigen, der Rest wird im Menü angezeigt
     */
    @Composable
    fun getMobileNavigationItems(definition: INavigationDefinition): List<NavItem> {
        return definition.pagesMain.map { it.toActionItem().toNavItem() }
    }

    /**
     * nur main pages als Navigation Items anzeigen, der Rest wird im Menü angezeigt
     */
    @Composable
    fun getWebNavigationItems(definition: INavigationDefinition): List<NavItem> {
        return definition.pagesMain.map { it.toActionItem().toNavItem() }
    }

    /**
     * MenuIcon + Main Actions, dann Sub Actions, dann divider, dann current page actions, dann Divider und darunter die Settings Page
     */
    @Composable
    fun getMobileMenuItems(
        definition: INavigationDefinition,
        groupedInMoreItem: Boolean,
    ): List<MenuItem> {

        val setup = CommonApp.setup
        val proVersion by setup.proVersionManager.proState.collectAsState()

        // 1) Pro Version + Separator
        val itemsProVersion = if (proVersion == ProState.Yes) {
            emptyList()
        } else {
            val action = setup.proVersionManager.actionProVersion()?.toMenuItem()
            if (action == null) {
                emptyList()
            } else {
                listOf(
                    action,
                    MenuItem.Separator()
                )
            }
        }

        // 2) custom actions
        val itemsCustomAction = definition.actionCustom().map { it.toMenuItem() }

        // 3) Separator + current page actions
        val navigator = LocalNavigator.currentOrThrow
        val navScreen = navigator.lastNavItem
        val additionalMenu = listOf(MenuItem.Separator()) + navScreen.provideMenu()

        // 3) Separator + Settings
        val itemsSettings = listOf(
            MenuItem.Separator(),
            definition.pageSetting.toActionItem().toMenuItem()
        )

        val subItems = listOfNotNull(itemsProVersion + itemsCustomAction + additionalMenu + itemsSettings)
            .flatten()
            .let {
                removeConsecutiveSeparators(it)
            }

        return if (groupedInMoreItem) {
            listOfNotNull(
                MenuItem.Group(
                    icon = Icons.Default.MoreVert.toIconComposable(),
                    items = subItems
                ).takeIf { it.items.isNotEmpty() }
            )
        } else subItems
    }

    @Composable
    fun getDesktopMenuItems(
        definition: INavigationDefinition,
        menuLabel: String = "App",
        menuWindowLabel: String = "Window",
        labelResetWindowSize: String = "Reset Window Size",
        labelResetWindowPosition: String = "Reset Window Position",
        labelOpenDebugDrawer: String = "Open Debug Drawer",
        labelCloseDebugDrawer: String = "Close Debug Drawer",
        resetWindowSize: (suspend () -> Unit)? = null,
        resetWindowPosition: (suspend () -> Unit)? = null,
    ): List<MenuItem> {
        val scope = rememberCoroutineScope()
        val setup = CommonApp.setup
        val resetWindowMenuItems = listOfNotNull(
            resetWindowSize?.let {
                MenuItem.Item(
                    labelResetWindowSize,
                    Icons.Default.Clear.toIconComposable()
                ) {
                    scope.launch { resetWindowSize() }
                }
            },
            resetWindowPosition?.let {
                MenuItem.Item(
                    labelResetWindowPosition,
                    Icons.Default.Clear.toIconComposable()
                ) {
                    scope.launch { resetWindowPosition() }
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

        val proVersion by setup.proVersionManager.proState.collectAsState()

        // 1) Pro Version + Separator
        val itemsProVersion = if (proVersion == ProState.Yes) {
            emptyList()
        } else {
            listOfNotNull(setup.proVersionManager.actionProVersion()?.toMenuItem())
        }

        // 2) custom actions
        val itemsCustomAction = definition.actionCustom().map { it.toMenuItem() }

        val subItems = listOfNotNull(
            itemsProVersion,
            itemsCustomAction,
            MenuItem.Separator().let { listOf(it) },
            MenuItem.Group(
                text = menuWindowLabel,
                icon = Icons.Default.WebAsset.toIconComposable(),
                items = resetWindowMenuItems
            ).takeIf { it.items.isNotEmpty() }?.let { listOf(it) }
        )
            .flatten()

        return listOfNotNull(
            MenuItem.Group(
                text = menuLabel,
                items = subItems
            ).takeIf { it.items.isNotEmpty() },
            debugMenu
        )
    }

    /**
     * wir wollen keine Labels im root level hier
     */
    @Composable
    fun getWebMenuItems(
        definition: INavigationDefinition,
    ): List<MenuItem> {

        val setup = CommonApp.setup
        val proVersion by setup.proVersionManager.proState.collectAsState()

        // 1) Pro Version + Separator
        val itemsProVersion = if (proVersion == ProState.Yes) {
            null
        } else {
            setup.proVersionManager.actionProVersion()?.toMenuItem()
        }

        // 2) custom actions
        val itemsCustomAction = definition.actionCustom().map { it.toMenuItem() }

        return listOfNotNull(
            itemsProVersion,
            definition.pageSetting.toActionItem().toMenuItem(
                hideTitleIfIconIsAvailable = true
            ),
            MenuItem.Group(
                icon = Icons.Default.MoreVert.toIconComposable(),
                items = itemsCustomAction
            ).takeIf { it.items.isNotEmpty() }
        )
    }

    fun removeConsecutiveSeparators(items: List<MenuItem>): List<MenuItem> {
        return items.fold(mutableListOf<MenuItem>()) { acc, item ->
            if (item is MenuItem.Separator && acc.lastOrNull() is MenuItem.Separator) {
                acc // überspringen
            } else {
                acc.also { it.add(item) }
            }
        }
            .dropWhile { it is MenuItem.Separator }
            .dropLastWhile { it is MenuItem.Separator }
    }

}
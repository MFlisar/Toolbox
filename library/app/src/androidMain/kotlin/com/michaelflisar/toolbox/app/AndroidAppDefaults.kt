package com.michaelflisar.toolbox.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.menu.removeConsecutiveSeparators
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionSetup
import com.michaelflisar.toolbox.app.pages.PageSettings
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.features.proversion.ProState

object AndroidAppDefaults {

    @Composable
    fun getMobileMenuItems(
        pageSettings: PageSettings,
        customActions: List<MenuItem> = emptyList(),
        groupedInMoreItem: Boolean = true,
    ): List<MenuItem> {

        val proVersionManager = ProVersionManager.setup
        val proVersion by proVersionManager.proState.collectAsState()

        // 1) Pro Version + Separator
        val itemsProVersion = if (proVersion == ProState.Yes) {
            emptyList()
        } else {
            val action = (proVersionManager as? ProVersionSetup.Supported)?.action?.invoke()?.toMenuItem()
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
        val itemsCustomAction = customActions

        // 3) Separator + current page actions
        val navigator = LocalNavigator.currentOrThrow
        val navScreen = navigator.lastNavItem
        val additionalMenu = listOf(MenuItem.Separator()) + navScreen.provideMenu()

        // 3) Separator + Settings
        val itemsSettings = listOf(
            MenuItem.Separator(),
            pageSettings.toActionItem().toMenuItem()
        )

        val subItems = listOfNotNull(itemsProVersion + itemsCustomAction + additionalMenu + itemsSettings)
            .flatten()
            .removeConsecutiveSeparators()

        return if (groupedInMoreItem) {
            listOfNotNull(
                MenuItem.Group(
                    icon = Icons.Default.MoreVert.toIconComposable(),
                    items = subItems
                ).takeIf { it.items.isNotEmpty() }
            )
        } else subItems
    }
}
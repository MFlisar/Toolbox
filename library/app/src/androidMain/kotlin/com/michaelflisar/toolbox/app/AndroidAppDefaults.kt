package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionSetup
import com.michaelflisar.toolbox.app.features.toolbar.MainMenuItems
import com.michaelflisar.toolbox.app.pages.PageSettings
import com.michaelflisar.toolbox.features.proversion.ProState

object AndroidAppDefaults {

    @Composable
    fun getMobileMenuItems(
        pageSettings: PageSettings,
        customActions: List<MenuItem> = emptyList()
    ): MainMenuItems {

        val proVersionManager = ProVersionManager.setup
        val proVersion by proVersionManager.proState.collectAsState()

        val itemProVersion = if (proVersion == ProState.Yes) {
            null
        } else {
            (proVersionManager as? ProVersionSetup.Supported)?.action?.invoke()?.toMenuItem()
        }

        val itemSetting = pageSettings.toActionItem().toMenuItem()

        return remember(itemProVersion) { MainMenuItems(itemProVersion, itemSetting, customActions) }
    }
}
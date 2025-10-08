package com.michaelflisar.toolbox.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionSetup
import com.michaelflisar.toolbox.app.pages.PageSettings
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.features.proversion.ProState

object WasmAppDefaults {

    /**
     * wir wollen keine Labels im root level hier
     */
    @Composable
    fun getWebMenuItems(
        pageSettings: PageSettings,
        customActions: List<MenuItem> = emptyList()
    ): List<MenuItem> {

        val proVersionManager = ProVersionManager.setup
        val proVersion by proVersionManager.proState.collectAsState()

        // 1) Pro Version + Separator
        val itemsProVersion = if (proVersion == ProState.Yes) {
            null
        } else {
            (proVersionManager as? ProVersionSetup.Supported)?.action?.invoke()?.toMenuItem()
        }

        // 2) custom actions
        val itemsCustomAction = customActions

        return listOfNotNull(
            itemsProVersion,
            pageSettings.toActionItem().toMenuItem(
                hideTitleIfIconIsAvailable = true
            ),
            MenuItem.Group(
                icon = Icons.Default.MoreVert.toIconComposable(),
                items = itemsCustomAction
            ).takeIf { it.items.isNotEmpty() }
        )
    }
}
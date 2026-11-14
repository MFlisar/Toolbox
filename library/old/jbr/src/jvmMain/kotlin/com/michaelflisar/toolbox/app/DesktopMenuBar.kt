package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.jewel.JewelTitleBar
import com.michaelflisar.toolbox.app.jewel.JewelTitleMenu

@Composable
fun DesktopMenuBar(
    items: List<MenuItem>,
    setup: DesktopTitleBar.Setup = DesktopTitleBar.Setup(),
    actions: List<DesktopTitleBar.TitleAction> = emptyList(),
) {
    JewelTitleBar(
        setup = setup,
        actions = actions,
        menubar = {
            JewelTitleMenu(items = items)
        }
    )
}
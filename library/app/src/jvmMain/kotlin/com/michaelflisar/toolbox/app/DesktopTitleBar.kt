package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.jewel.JewelTitleBar
import com.michaelflisar.toolbox.app.jewel.JewelTitleMenu
import org.jetbrains.jewel.window.DecoratedWindowScope

class DesktopTitleBarSetup(
    val showAlwaysOnTop: Boolean = true,
    val showThemeSelector: Boolean = true,
)

class DesktopTitleAction(
    val title: String,
    val imageVector: ImageVector,
    val onClick: () -> Unit,
)

@Composable
fun DecoratedWindowScope.DesktopTitleBar(
    setup: DesktopTitleBarSetup = DesktopTitleBarSetup(
        showAlwaysOnTop = true,
        showThemeSelector = true
    ),
    iconItems: List<DesktopTitleAction> = emptyList(),
    menu: @Composable () -> Unit = { },
) {
    JewelTitleBar(
        setup = setup,
        iconItems = iconItems,
        menubar = {
            menu()
        }
    )
}

@Composable
fun DesktopTitleMenu(
    items: List<MenuItem>,
) {
    JewelTitleMenu(items = items)
}
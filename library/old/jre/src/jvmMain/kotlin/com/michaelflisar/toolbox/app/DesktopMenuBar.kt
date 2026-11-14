package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.j.JTitleMenu
import com.michaelflisar.toolbox.app.j.LocalFrameWindowScope

@Composable
fun DesktopMenuBar(
    items: List<MenuItem>,
) {
    with(LocalFrameWindowScope.current) {
        JTitleMenu(items = items)
    }
}
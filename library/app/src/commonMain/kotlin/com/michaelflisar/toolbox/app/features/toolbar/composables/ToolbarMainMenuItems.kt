package com.michaelflisar.toolbox.app.features.toolbar.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.michaelflisar.toolbox.app.features.menu.Menu
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.menu.removeConsecutiveSeparators
import com.michaelflisar.toolbox.extensions.toIconComposable

val LocalToolbarMainMenuItems = staticCompositionLocalOf { ToolbarMainMenuItems() }

@Stable
class ToolbarMainMenuItems(
    val itemProVersion: MenuItem? = null,
    val itemSettings: MenuItem? = null,
    val customActions: List<MenuItem> = emptyList(),
) {
    fun combineWith(
        items: List<MenuItem>,
        dividerAfterProVersion: Boolean = true,
        dividerBeforeSettings: Boolean = true,
    ): List<MenuItem> {
        val list = mutableListOf<MenuItem>()
        itemProVersion?.let {
            list.add(it)
            if (dividerAfterProVersion)
                list.add(MenuItem.Separator())
        }
        list.addAll(customActions)
        list.addAll(items)
        itemSettings?.let {
            if (dividerBeforeSettings)
                list.add(MenuItem.Separator())
            list.add(it)
        }
        return list.removeConsecutiveSeparators()
    }

    fun getAsOverflowMenuItems(
        additionalItems: List<MenuItem> = emptyList(),
        dividerAfterProVersion: Boolean = true,
        dividerBeforeSettings: Boolean = true,
    ): List<MenuItem> {
        val items = combineWith(
            items = additionalItems,
            dividerAfterProVersion = dividerAfterProVersion,
            dividerBeforeSettings = dividerBeforeSettings
        )
        if (items.isEmpty())
            return emptyList()
        return listOf(
            MenuItem.Group(
                icon = Icons.Default.MoreVert.toIconComposable(),
                items = items
            )
        )
    }
}

@Composable
fun ToolbarMainMenuItems(
    showInOverflow: Boolean,
    additionalItems: List<MenuItem> = emptyList(),
    dividerAfterProVersion: Boolean = true,
    dividerBeforeSettings: Boolean = true,
) {
    val mainMenuItems = LocalToolbarMainMenuItems.current
    val items = remember(
        additionalItems,
        mainMenuItems,
        showInOverflow,
        dividerAfterProVersion,
        dividerBeforeSettings
    ) {
        if (showInOverflow) {
            mainMenuItems.getAsOverflowMenuItems(
                additionalItems,
                dividerAfterProVersion,
                dividerBeforeSettings
            )
        } else {
            mainMenuItems.combineWith(
                additionalItems,
                dividerAfterProVersion,
                dividerBeforeSettings
            )
        }
    }
    if (!items.isEmpty()) {
        Menu(items)
    }
}
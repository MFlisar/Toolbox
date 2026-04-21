package com.michaelflisar.toolbox.app.features.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.features.actions.IBaseAction
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.navigation.NavItemRegion
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.extensions.Icon
import com.michaelflisar.toolbox.feature.menu.MenuCheckbox
import com.michaelflisar.toolbox.feature.menu.MenuItem
import com.michaelflisar.toolbox.feature.menu.MenuScope
import com.michaelflisar.toolbox.feature.menu.MenuSeparator
import com.michaelflisar.toolbox.feature.menu.MenuState
import com.michaelflisar.toolbox.feature.menu.MenuSubMenu
import com.michaelflisar.toolbox.feature.menu.PopupMenu
import com.michaelflisar.toolbox.feature.menu.rememberMenuState

sealed class MenuItem {

    class Item(
        val text: String,
        val icon: IconComposable? = null,
        val keyboardShortcut: Set<String> = emptySet(),
        val onClick: () -> Unit,
    ) : MenuItem(), IBaseAction {

        @Composable
        override fun toMenuItem(): MenuItem = this

        @Composable
        override fun toNavItem(): INavItem {
            return NavItemAction(
                title = text,
                icon = icon,
                action = onClick
            )
        }

        @Composable
        override fun PopupMenuItem(scope: MenuScope) {
            with(scope) {
                MenuItem(
                    text = { Text(text) },
                    icon = icon
                ) {
                    onClick()
                }
            }
        }
    }

    class Checkbox(
        val text: String,
        val icon: IconComposable? = null,
        val keyboardShortcut: Set<String> = emptySet(),
        val checked: MutableState<Boolean>,
    ) : MenuItem()

    class Group(
        val text: String = "",
        val icon: IconComposable? = null,
        val items: List<MenuItem>,
    ) : MenuItem()

    class Separator(
        val text: String = "",
    ) : MenuItem(), IBaseAction {

        @Composable
        override fun toMenuItem(): MenuItem = this

        @Composable
        override fun toNavItem(): INavItem {
            return NavItemRegion(title = text)
        }

        @Composable
        override fun PopupMenuItem(scope: MenuScope) {
            with(scope) {
                MenuSeparator(text = text)
            }
        }
    }
}

@Composable
fun Menu(
    items: List<MenuItem>,
) {
    if (items.isEmpty()) {
        return
    }
    items.forEach {
        when (it) {
            is MenuItem.Item -> {
                MenuButton(
                    text = it.text,
                    onClick = it.onClick,
                    icon = it.icon
                )
            }

            is MenuItem.Separator -> {
                VerticalDivider()
            }

            is MenuItem.Group -> {
                val menuStateOfGroup = rememberMenuState()
                MenuButton(
                    text = it.text,
                    onClick = { menuStateOfGroup.show() },
                    icon = it.icon
                ) {
                    PopupMenu(menuStateOfGroup) {
                        Menu(menuStateOfGroup, it.items)
                    }
                }
            }

            is MenuItem.Checkbox -> {
                MyCheckbox(
                    title = {
                        if (it.text.isEmpty() && it.icon != null) {
                            Icon(it.icon)
                        } else {
                            MyRow(itemSpacing = 4.dp) {
                                it.icon?.let { Icon(it) }
                                Text(it.text)
                            }
                        }
                    },
                    checked = it.checked
                )
            }
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: IconComposable?,
    onClick: () -> Unit,
    content: @Composable (() -> Unit) = { },
) {
    Box {
        if (icon != null) {
            if (text.isNotEmpty()) {

                Button(
                    onClick = onClick,
                    elevation = ButtonDefaults.buttonElevation(
                        hoveredElevation = 0.dp // same as IconButton
                    ),
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(icon)
                        Text(text)
                    }

                }

            } else {
                IconButton(
                    onClick = onClick
                ) {
                    Icon(icon)
                }
            }
        } else {
            Button(
                onClick = onClick,
                elevation = ButtonDefaults.buttonElevation(
                    hoveredElevation = 0.dp // same as IconButton
                )
            ) {
                Text(text)
            }
        }
        content()
    }
}

@Composable
private fun MenuScope.Menu(menuState: MenuState, items: List<MenuItem>) {
    items.forEach {
        when (it) {
            is MenuItem.Group -> {
                MenuSubMenu(
                    text = it.text,
                    icon = it.icon
                ) {
                    Menu(menuState, it.items)
                }
            }

            is MenuItem.Item -> {
                MenuItem(
                    text = {
                        if (it.text.isNotEmpty())
                            Text(it.text)
                    },
                    icon = it.icon
                ) {
                    it.onClick()
                }
            }

            is MenuItem.Separator -> {
                MenuSeparator(text = it.text)
            }

            is MenuItem.Checkbox -> {
                MenuCheckbox(
                    text = { Text(it.text) },
                    icon = it.icon,
                    checked = it.checked
                )
            }
        }
    }
}

fun List<MenuItem>.removeConsecutiveSeparators(): List<MenuItem> {
    return fold(mutableListOf<MenuItem>()) { acc, item ->
        if (item is MenuItem.Separator && acc.lastOrNull() is MenuItem.Separator) {
            acc // überspringen
        } else {
            acc.also { it.add(item) }
        }
    }
        .dropWhile { it is MenuItem.Separator }
        .dropLastWhile { it is MenuItem.Separator }
}
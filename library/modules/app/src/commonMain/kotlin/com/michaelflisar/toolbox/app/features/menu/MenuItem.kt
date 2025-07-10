package com.michaelflisar.toolbox.app.features.menu

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.feature.menu.MenuCheckbox
import com.michaelflisar.toolbox.feature.menu.MenuItem
import com.michaelflisar.toolbox.feature.menu.MenuSeparator
import com.michaelflisar.toolbox.feature.menu.MenuState
import com.michaelflisar.toolbox.feature.menu.MenuSubMenu
import com.michaelflisar.toolbox.feature.menu.PopupMenu
import com.michaelflisar.toolbox.feature.menu.PopupMenuScope
import com.michaelflisar.toolbox.feature.menu.rememberMenuState

sealed class MenuItem {

    class Item(
        val text: String,
        val imageVector: ImageVector? = null,
        val keyboardShortcut: Set<String> = emptySet(),
        val onClick: () -> Unit,
    ) : MenuItem()

    class Checkbox(
        val text: String,
        val imageVector: ImageVector? = null,
        val keyboardShortcut: Set<String> = emptySet(),
        val checked: MutableState<Boolean>,
    ) : MenuItem()

    class Group(
        val text: String = "",
        val imageVector: ImageVector? = null,
        val items: List<MenuItem>,
    ) : MenuItem()

    class Separator(
        val text: String = "",
    ) : MenuItem()
}

@Composable
fun BaseAppMenu(items: List<MenuItem>) {
    items.forEach {
        when (it) {
            is MenuItem.Item -> {
                MenuButton(
                    text = it.text,
                    onClick = it.onClick,
                    imageVector = it.imageVector
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
                    imageVector = it.imageVector
                )
                PopupMenu(menuStateOfGroup) {
                    Menu(menuStateOfGroup, it.items)
                }
            }

            is MenuItem.Checkbox -> {
                MyCheckbox(
                    title = {
                        if (it.text.isEmpty() && it.imageVector != null) {
                            Icon(it.imageVector, contentDescription = null)
                        } else {
                            MyRow(itemSpacing = 4.dp) {
                                if (it.imageVector != null)
                                    Icon(it.imageVector, contentDescription = null)
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
private fun MenuButton(text: String, imageVector: ImageVector?, onClick: () -> Unit) {
    if (text.isEmpty() && imageVector != null) {
        IconButton(
            onClick = onClick
        ) {
            Icon(imageVector, contentDescription = null)
        }
    } else {
        MyButton(
            text = text,
            onClick = onClick,
            icon = imageVector
        )
    }
}

@Composable
private fun PopupMenuScope.Menu(menuState: MenuState, items: List<MenuItem>) {
    items.forEach {
        when (it) {
            is MenuItem.Group -> {
                MenuSubMenu(
                    text = it.text,
                    icon = it.imageVector?.let { { Icon(it, null) } },
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
                    icon = it.imageVector?.let { { Icon(it, null) } }
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
                    icon = it.imageVector?.let { { Icon(it, null) } },
                    checked = it.checked
                )
            }
        }
    }
}
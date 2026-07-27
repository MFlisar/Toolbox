package com.michaelflisar.toolbox.app.j

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuBarScope
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.menu.removeConsecutiveSeparators
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.feature.menu.MenuCheckbox
import com.michaelflisar.toolbox.feature.menu.MenuItem
import com.michaelflisar.toolbox.feature.menu.MenuScope
import com.michaelflisar.toolbox.feature.menu.MenuSeparator
import com.michaelflisar.toolbox.feature.menu.PopupMenu
import com.michaelflisar.toolbox.feature.menu.rememberMenuState

fun MenuItem.KeyboardShortcut.toComposeKeyShortcut(): KeyShortcut =
    KeyShortcut(
        key = key,
        ctrl = ctrl,
        shift = shift,
        alt = alt,
        meta = system
    )

@Composable
internal fun FrameWindowScope.JTitleMenu(
    items: List<MenuItem>,
) {
    val items = remember(items) { items.removeConsecutiveSeparators() }
    MenuBar {
        items.forEach { item ->
            when (item) {
                is MenuItem.Group -> {
                    itemGroup(item)
                }

                is MenuItem.Separator -> {
                    // not supported at root level
                }

                is MenuItem.Item -> {
                    // workaround: single item in menu...
                    Menu(
                        text = item.text
                    ) {
                        Item(
                            text = item.text,
                            icon = null,
                            shortcut = item.keyboardShortcut?.toComposeKeyShortcut(),
                            mnemonic = item.mnemonic,
                            enabled = item.enabled,
                            onClick = item.onClick
                        )
                    }
                }

                is MenuItem.Checkbox -> {
                    Menu(
                        text = item.text,
                        mnemonic = item.mnemonic,
                        enabled = item.enabled
                    ) {
                        CheckboxItem(
                            text = item.text,
                            icon = null,
                            shortcut = item.keyboardShortcut?.toComposeKeyShortcut(),
                            checked = item.checked.value,
                            mnemonic = item.mnemonic,
                            enabled = item.enabled,
                            onCheckedChange = {
                                item.checked.value = it
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuBarScope.itemGroup(
    group: MenuItem.Group,
) {
    Menu(
        text = group.text + "   ", // ohne Keyboard Shortcut ist das end icon immer über Text... daher dieser Hack!
        mnemonic = group.mnemonic,
        enabled = group.enabled,
    ) {
        group.items.forEach { item ->
            when (item) {
                is MenuItem.Checkbox -> {
                    CheckboxItem(
                        text = item.text,
                        icon = item.fallbackIcon,
                        checked = item.checked.value,
                        shortcut = item.keyboardShortcut?.toComposeKeyShortcut(),
                        mnemonic = item.mnemonic,
                        enabled = item.enabled,
                        onCheckedChange = {
                            item.checked.value = it
                        }
                    )
                }

                is MenuItem.Group -> {
                    itemGroup(item)
                }

                is MenuItem.Item -> {
                    Item(
                        text = item.text,
                        icon = item.fallbackIcon,
                        shortcut = item.keyboardShortcut?.toComposeKeyShortcut(),
                        mnemonic = item.mnemonic,
                        enabled = item.enabled,
                        onClick = item.onClick
                    )
                }

                is MenuItem.Separator -> {
                    Separator()
                }
            }
        }
    }
}

@Composable
private fun MenuGroup(
    group: MenuItem.Group,
) {
    val popup = rememberMenuState()
    Box {
        MyButton(
            text = group.text,
            icon = group.icon,
            onClick = { popup.show() }
        )
        PopupMenu(
            state = popup
        ) {
            group.items.forEach {
                GroupContent(it)
            }
        }
    }
}

@Composable
private fun MenuScope.GroupContent(item: MenuItem) {
    when (item) {
        is MenuItem.Separator -> {
            MenuSeparator(text = item.text)
        }

        is MenuItem.Group -> {
            MenuGroup(item)
        }

        is MenuItem.Item -> {
            MenuItem(
                text = { Text(item.text) },
                icon = item.icon,
                onClick = item.onClick
            )
        }

        is MenuItem.Checkbox -> {
            MenuCheckbox(
                text = { Text(item.text) },
                icon = item.icon,
                checked = item.checked
            )
        }
    }
}
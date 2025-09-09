package com.michaelflisar.toolbox.app.jewel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.removeConsecutiveSeparators
import com.michaelflisar.toolbox.extensions.Render
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.ActionButton
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.MenuScope
import org.jetbrains.jewel.ui.component.PopupMenu
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.separator
import org.jetbrains.jewel.ui.theme.iconButtonStyle
import org.jetbrains.jewel.ui.theme.menuStyle
import org.jetbrains.jewel.window.defaultDecoratedWindowStyle
import org.jetbrains.jewel.window.defaultTitleBarStyle
import org.jetbrains.jewel.window.styling.LocalTitleBarStyle

@Composable
fun JewelTitleMenu(
    items: List<MenuItem>,
) {
    val items = remember(items) { items.removeConsecutiveSeparators() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach {
            when (it) {
                is MenuItem.Group -> {
                    val popup = remember { mutableStateOf(false) }
                    ActionButton(
                        modifier = Modifier.height(30.dp),
                        onClick = { popup.value = true }
                    ) {
                        TitleMenuItem(it.text, it.icon, TitleMenuItem.Dropdown(true))
                        if (popup.value) {
                            val foreground = JewelTheme.globalColors.text.normal
                            PopupMenu(
                                onDismissRequest = {
                                    popup.value = false
                                    true
                                },
                                horizontalAlignment = Alignment.Start
                            ) {
                                it.items.forEach {
                                    titleMenuContent(it, foreground)
                                }
                            }
                        }
                    }
                }

                is MenuItem.Item -> {
                    ActionButton(
                        modifier = Modifier.height(30.dp),
                        onClick = it.onClick
                    ) {
                        TitleMenuItem(it.text, it.icon, TitleMenuItem.Action(true))
                    }

                }

                is MenuItem.Separator -> {
                    VerticalDivider()
                }

                is MenuItem.Checkbox -> {
                    ActionButton(
                        modifier = Modifier.height(30.dp),
                        onClick = {
                            it.checked.value = !it.checked.value
                        }
                    ) {
                        TitleMenuItem(
                            it.text,
                            it.icon,
                            TitleMenuItem.Checkbox(true, it.checked.value)
                        )
                    }
                }
            }
        }
    }
}

private sealed class TitleMenuItem {
    abstract val root: Boolean

    class Action(override val root: Boolean) : TitleMenuItem()
    class Dropdown(override val root: Boolean) : TitleMenuItem()
    class Checkbox(override val root: Boolean, val checked: Boolean) : TitleMenuItem()
}

@Composable
private fun TitleMenuItem(
    title: String,
    icon: IconComposable?,
    type: TitleMenuItem,
) {

    val jewelTitleTheme = JewelTheme.defaultTitleBarStyle
    val contentColor = JewelTheme.contentColor
    val foreground = if (!type.root) contentColor else jewelTitleTheme.colors.content

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon.Render(
                modifier = Modifier.size(24.dp).padding(4.dp),
                tint = foreground
            )
        } else {
            if (!type.root) {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
        Text(title)
        if (type is TitleMenuItem.Checkbox) {
            Icon(
                modifier = Modifier.size(24.dp).padding(4.dp),
                imageVector = if (type.checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = foreground
            )
        }
        if (type is TitleMenuItem.Dropdown && type.root) {
            Icon(
                modifier = Modifier.size(24.dp).padding(4.dp),
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                tint = foreground
            )
        }
    }
}

private fun MenuScope.titleMenuContent(item: MenuItem, contentColor: Color) {
    when (item) {
        is MenuItem.Separator -> {
            if (item.text.isEmpty()) {
                separator()
            } else {
                passiveItem {
                    Text(
                        text = item.text,
                        modifier = Modifier.padding(8.dp),
                        color = contentColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        is MenuItem.Group -> {
            submenu(
                submenu = {
                    item.items.forEach {
                        titleMenuContent(it, contentColor)
                    }
                }
            ) {
                TitleMenuItem(item.text, item.icon, TitleMenuItem.Dropdown(false))
            }
        }

        is MenuItem.Item -> {
            selectableItem(
                selected = false,
                onClick = item.onClick,
                keybinding = item.keyboardShortcut
            ) {
                TitleMenuItem(item.text, item.icon, TitleMenuItem.Action(false))
            }
        }

        is MenuItem.Checkbox -> {
            selectableItem(
                selected = false,
                onClick = {
                    item.checked.value = !item.checked.value
                },
                keybinding = item.keyboardShortcut
            ) {
                TitleMenuItem(
                    item.text,
                    item.icon,
                    TitleMenuItem.Checkbox(false, item.checked.value)
                )
            }
        }
    }
}
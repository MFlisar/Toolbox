package com.michaelflisar.toolbox.windows.jewel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.disabled
import com.michaelflisar.toolbox.windows.classes.LocalAppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.michaelflisar.toolbox.windows.resources.Res
import com.michaelflisar.toolbox.windows.resources.keep
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.defaultTitleBarStyle
import org.jetbrains.jewel.window.newFullscreenControls

class JewelTitleAction(
    val title: String,
    val imageVector: ImageVector,
    val onClick: () -> Unit
)

sealed class JewelMenuBarItem {

    class Item(
        val title: String,
        val imageVector: ImageVector? = null,
        val keyboardShortcut: Set<String> = emptySet(),
        val onClick: () -> Unit
    ) : JewelMenuBarItem()

    class Group(
        val title: String,
        val imageVector: ImageVector? = null,
        val keyboardShortcut: Set<String> = emptySet(),
        val items: List<JewelMenuBarItem>
    ) : JewelMenuBarItem()

    data object Separator : JewelMenuBarItem()
}

object JewelTitleBar {
    class Setup(
        val showAlwaysOnTop: Boolean = true,
        val showThemeSelector: Boolean = true,
        val showMenuAlways: Boolean = true,
    )
}

@Composable
fun DecoratedWindowScope.JewelTitleBar(
    icon: Painter? = null,
    iconItems: List<JewelTitleAction> = emptyList(),
    menuBarSetup: JewelTitleBar.Setup = JewelTitleBar.Setup(),
    menuBarItems: List<JewelMenuBarItem> = emptyList()
) {
    val scope = rememberCoroutineScope()
    val appState = LocalAppState.current
    val theme by appState.prefs.jewelTheme.collectAsStateNotNull()

    val menuExpanded = remember { mutableStateOf(menuBarSetup.showMenuAlways) }

    val jewelTitleTheme = org.jetbrains.jewel.foundation.theme.JewelTheme.defaultTitleBarStyle
    val foreground = jewelTitleTheme.colors.content

    TitleBar(Modifier.newFullscreenControls()) {
        Row(
            modifier = Modifier.align(Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleAppIcon(icon)

            if (menuExpanded.value) {
                TitleMenu(menuBarItems, menuExpanded)
            } else {
                if (menuBarItems.isNotEmpty()) {
                    TitleIconButton(Icons.Default.Menu, "Main Menu") {
                        menuExpanded.value = true
                    }
                }

            }
        }
        if (menuBarSetup.showMenuAlways || !menuExpanded.value) {
            Text(title)
        }
        Row(
            Modifier.align(Alignment.End)
        ) {
            iconItems.forEach { item ->
                TitleIconButton(item.imageVector, item.title, onClick = item.onClick)
            }
            if (menuBarSetup.showThemeSelector) {
                val nextTheme = JewelTheme.entries[(theme.ordinal + 1) % JewelTheme.entries.size]
                val text = nextTheme.switchLabel
                TitleIconButton(theme.imageVector, text) {
                    scope.launch(Dispatchers.IO) {
                        appState.prefs.jewelTheme.update(nextTheme)
                    }
                }
            }
            if (menuBarSetup.showAlwaysOnTop) {
                val alwaysOnTop = appState.prefs.alwaysOnTop.collectAsStateNotNull()
                TitleIconButton(
                    painterResource(Res.drawable.keep),
                    "Always On Top",
                    if (alwaysOnTop.value) foreground else foreground.disabled()
                ) {
                    scope.launch(Dispatchers.IO) {
                        appState.prefs.alwaysOnTop.update(!alwaysOnTop.value)
                    }
                }
            }
        }
    }
}

@Composable
fun TitleMenu(items: List<JewelMenuBarItem>, menuExpanded: MutableState<Boolean>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach {
            when (it) {
                is JewelMenuBarItem.Group -> {
                    Dropdown(
                        modifier = Modifier.height(30.dp),
                        menuContent = {
                            titleMenuContent(it)
                        }
                    ) {
                        TitleMenuItem(it.title, it.imageVector, false)
                    }
                }

                is JewelMenuBarItem.Item -> {
                    ActionButton(
                        modifier = Modifier.height(30.dp),
                        onClick = it.onClick
                    ) {
                        TitleMenuItem(it.title, it.imageVector, false)
                    }

                }

                JewelMenuBarItem.Separator -> VerticalDivider()
            }
        }
    }
}

@Composable
private fun TitleMenuItem(title: String, imageVector: ImageVector?, popup: Boolean = true) {

    val jewelTitleTheme = org.jetbrains.jewel.foundation.theme.JewelTheme.defaultTitleBarStyle
    val contentColor = org.jetbrains.jewel.foundation.theme.JewelTheme.contentColor
    val foreground = if (popup) contentColor else jewelTitleTheme.colors.content

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (imageVector != null) {
            Icon(
                modifier = Modifier.size(24.dp).padding(4.dp),
                imageVector = imageVector,
                contentDescription = null,
                tint = foreground
            )
        } else {
            if (popup) {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
        Text(title)
    }
}

private fun MenuScope.titleMenuContent(item: JewelMenuBarItem) {
    when (item) {
        is JewelMenuBarItem.Separator -> {
            separator()
        }

        is JewelMenuBarItem.Group -> {
            submenu(
                submenu = {
                    item.items.forEach {
                        titleMenuContent(it)
                    }
                }
            ) {
                TitleMenuItem(item.title, item.imageVector)
            }
        }

        is JewelMenuBarItem.Item -> {
            selectableItem(
                selected = false,
                onClick = item.onClick,
                keybinding = item.keyboardShortcut
            ) {
                TitleMenuItem(item.title, item.imageVector)
            }
        }
    }

}

@Composable
private fun RowScope.TitleAppIcon(
    icon: Painter? = null,
) {
    if (icon != null) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp).size(30.dp).padding(4.dp)
        )
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun TitleIconButton(
    imageVector: ImageVector,
    title: String,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    TitleIconButton(rememberVectorPainter(imageVector), title, tint, onClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TitleIconButton(
    painter: Painter,
    title: String,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    val jewelTitleTheme = org.jetbrains.jewel.foundation.theme.JewelTheme.defaultTitleBarStyle
    val foreground = jewelTitleTheme.colors.content

    Tooltip(
        tooltip = { Text(title) }
    ) {
        IconButton(
            onClick = {
                onClick()
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                painter = painter,
                contentDescription = title,
                tint = tint.takeIf { it != Color.Unspecified } ?: foreground
            )
        }
    }
}
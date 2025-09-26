package com.michaelflisar.toolbox.app.jewel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.drawables.Keep
import com.michaelflisar.toolbox.extensions.disabled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.defaultTitleBarStyle
import org.jetbrains.jewel.window.newFullscreenControls

class JewelTitleBarSetup(
    val showAlwaysOnTop: Boolean = true,
    val showThemeSelector: Boolean = true
)

class JewelTitleAction(
    val title: String,
    val imageVector: ImageVector,
    val onClick: () -> Unit,
)

@Composable
fun DecoratedWindowScope.JewelTitleBar(
    setup: JewelTitleBarSetup,
    icon: Painter? = CommonApp.setup.icon(),
    iconItems: List<JewelTitleAction> = emptyList(),
    menubar: @Composable () -> Unit = {},
) {
    val prefs = DesktopApp.setup.prefs
    val scope = rememberCoroutineScope()
    val theme by prefs.jewelTheme.collectAsStateNotNull()

    val jewelTitleTheme = org.jetbrains.jewel.foundation.theme.JewelTheme.defaultTitleBarStyle
    val foreground = jewelTitleTheme.colors.content

    TitleBar(Modifier.newFullscreenControls()) {
        Row(
            modifier = Modifier.align(Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleAppIcon(icon)
            menubar()
        }
        Text(title)
        Row(
            Modifier.align(Alignment.End)
        ) {
            iconItems.forEach { item ->
                TitleIconButton(item.imageVector, item.title, onClick = item.onClick)
            }
            if (setup.showThemeSelector) {
                val nextTheme = JewelTheme.entries[(theme.ordinal + 1) % JewelTheme.entries.size]
                val text = nextTheme.switchLabel
                TitleIconButton(theme.imageVector, text) {
                    scope.launch(Dispatchers.IO) {
                        prefs.jewelTheme.update(nextTheme)
                    }
                }
            }
            if (setup.showAlwaysOnTop) {
                val alwaysOnTop = prefs.alwaysOnTop.collectAsStateNotNull()
                TitleIconButton(
                    rememberVectorPainter(Keep),
                    "Always On Top",
                    if (alwaysOnTop.value) foreground else foreground.disabled()
                ) {
                    scope.launch(Dispatchers.IO) {
                        prefs.alwaysOnTop.update(!alwaysOnTop.value)
                    }
                }
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
    onClick: () -> Unit,
) {
    TitleIconButton(rememberVectorPainter(imageVector), title, tint, onClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TitleIconButton(
    painter: Painter,
    title: String,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit,
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
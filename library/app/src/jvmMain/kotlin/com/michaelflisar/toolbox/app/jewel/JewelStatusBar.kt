package com.michaelflisar.toolbox.app.jewel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.app.DesktopStatusBarItem
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.window.defaultTitleBarStyle

@Composable
internal fun JewelStatusBar(
    left: List<DesktopStatusBarItem> = emptyList(),
    right: List<DesktopStatusBarItem> = emptyList(),
    foreground: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    content: @Composable (() -> Unit)? = null
) {
    val foreground = foreground.takeIf { it != Color.Unspecified }
        ?: JewelTheme.defaultTitleBarStyle.colors.content
    val background = background.takeIf { it != Color.Unspecified }
        ?: JewelTheme.defaultTitleBarStyle.colors.background

    CompositionLocalProvider(LocalContentColor provides foreground) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(background),
        ) {
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                left.forEach { item ->
                    when (item) {
                        is DesktopStatusBarItem.Custom -> StatusBarCustom(item.content)
                        is DesktopStatusBarItem.Text -> StatusBarText(
                            text = item.text,
                            onClick = item.onClick,
                            color = item.color
                        )
                    }

                    VerticalDivider()
                }
                if (content == null) {
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        content()
                    }
                }

                right.forEach { item ->
                    VerticalDivider()
                    when (item) {
                        is DesktopStatusBarItem.Custom -> StatusBarCustom(item.content)
                        is DesktopStatusBarItem.Text -> StatusBarText(
                            text = item.text,
                            onClick = item.onClick,
                            color = item.color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBarText(
    text: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier
            .then(onClick?.let { Modifier.clickable { it() } } ?: Modifier)
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
        style = style,
        text = text,
        fontWeight = FontWeight.Bold,
        maxLines = maxLines,
        color = color
    )
}

@Composable
private fun StatusBarCustom(
    content: @Composable () -> Unit
) {
    content()
}
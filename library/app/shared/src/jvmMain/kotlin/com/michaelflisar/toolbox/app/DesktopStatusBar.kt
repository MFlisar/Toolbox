package com.michaelflisar.toolbox.app

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
import com.michaelflisar.toolbox.app.DesktopAppDefaults.getDefaultStatusBarItemsLeft
import com.michaelflisar.toolbox.app.DesktopAppDefaults.getDefaultStatusBarItemsRight

@Composable
fun DesktopStatusBar(
    showAppVersionLeft: Boolean = true,
    showJavaVersionRight: Boolean = true,
    showUserNameRight: Boolean = true,
    showHostNameRight: Boolean = true,
    onAppVersionClick: (() -> Unit)? = null,
    onJavaVersionClick: (() -> Unit)? = null,
    onUserNameClick: (() -> Unit)? = null,
    onHostNameClick: (() -> Unit)? = null,
    foreground: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    content: @Composable (() -> Unit)? = null,
) {
    val statusBarLeft = getDefaultStatusBarItemsLeft(
        showAppVersionLeft,
        onAppVersionClick
    )
    val statusBarRight = getDefaultStatusBarItemsRight(
        showJavaVersionRight,
        showUserNameRight,
        showHostNameRight,
        onJavaVersionClick,
        onUserNameClick,
        onHostNameClick
    )
    if (statusBarLeft.isEmpty() && statusBarRight.isEmpty()) {
        return // nothing to show
    }
    StatusBar(
        left = statusBarLeft,
        right = statusBarRight,
        foreground = foreground,
        background = background,
        content = content
    )
}

@Composable
fun DesktopStatusBarCustom(
    left: List<DesktopStatusBarItem> = emptyList(),
    right: List<DesktopStatusBarItem> = emptyList(),
    foreground: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    content: @Composable (() -> Unit)? = null,
) {
    StatusBar(
        left = left,
        right = right,
        foreground = foreground,
        background = background,
        content = content
    )
}

@Composable
private fun StatusBar(
    left: List<DesktopStatusBarItem> = emptyList(),
    right: List<DesktopStatusBarItem> = emptyList(),
    foreground: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    content: @Composable (() -> Unit)? = null,
) {
    val foreground = foreground.takeIf { it != Color.Unspecified }
        ?: JvmImpl.instance.defaultStatusBarForegroundColor
    val background = background.takeIf { it != Color.Unspecified }
        ?: JvmImpl.instance.defaultStatusBarBackgroundColor

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
    color: Color = Color.Unspecified,
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
    content: @Composable () -> Unit,
) {
    content()
}

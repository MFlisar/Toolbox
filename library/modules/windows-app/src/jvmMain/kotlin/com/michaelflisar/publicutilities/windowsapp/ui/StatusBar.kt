package com.michaelflisar.publicutilities.windowsapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme
import com.michaelflisar.publicutilities.windowsapp.classes.LocalAppState
import com.michaelflisar.publicutilities.windowsapp.classes.Status
import com.michaelflisar.publicutilities.windowsapp.ui.todo.MyHorizontalDivider
import com.michaelflisar.publicutilities.windowsapp.ui.todo.MyVerticalDivider
import java.net.InetAddress

@Composable
fun StatusBar(
    javaVersion: Boolean = true,
    userName: Boolean = true,
    computerName: Boolean = true,
    content: @Composable (() -> Unit)? = null
) {
    val appState = LocalAppState.current
    val state = appState.state.value

    val data = listOfNotNull(
        "Java ${System.getProperty("java.version")}".takeIf { javaVersion },
        System.getenv("username").takeIf { userName },
        InetAddress.getLocalHost().hostName.takeIf { computerName }
    )

    val running = when (state) {
        Status.None -> null
        is Status.Running -> state
    }


    //AuroraDecorationArea(decorationAreaType = DecorationAreaType.Footer) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onBackground)
        ) {
            MyHorizontalDivider(color = LocalContentColor.current)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (content == null) {
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    Row(modifier = Modifier.weight(1f)) {
                        content()
                    }
                }

                data.forEach {
                    StatusBarDivider()
                    StatusBarText(text = it)
                }
            }
            AnimatedVisibility(running != null) {
                MyHorizontalDivider(color = LocalContentColor.current)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (running?.showProgress == true) {
                        LinearProgressIndicator(
                            modifier = Modifier.width(128.dp)
                                .padding(horizontal = AppTheme.ITEM_SPACING)
                        )
                    }
                    StatusBarText(
                        text = running?.label ?: "",
                        modifier = Modifier.weight(1f),
                        maxLines = if (running?.singleLine == true) 1 else Int.MAX_VALUE
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.StatusBarDivider() = MyVerticalDivider(color = LocalContentColor.current)

@Composable
fun StatusBarText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    Text(
        modifier = modifier.padding(
            horizontal = AppTheme.CONTENT_PADDING_SMALL,
            vertical = 2.dp
        ),
        style = MaterialTheme.typography.bodyMedium,
        text = text,
        fontWeight = FontWeight.Bold,
        maxLines = maxLines
    )
}
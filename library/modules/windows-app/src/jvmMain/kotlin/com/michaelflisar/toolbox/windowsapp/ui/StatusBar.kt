package com.michaelflisar.toolbox.windowsapp.ui

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
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
import com.michaelflisar.composecolors.material.MaterialColor
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.windowsapp.classes.LocalAppState
import com.michaelflisar.toolbox.windowsapp.classes.Status
import java.net.InetAddress

object StatusBar {

    object Data {
        fun javaVersion() = "Java ${System.getProperty("java.version")}"
        fun userName() = System.getenv("username")
        fun hostName() = InetAddress.getLocalHost().hostName


    }
    fun defaultData(
        javaVersion: Boolean = true,
        userName: Boolean = true,
        hostName: Boolean = true,
    ): List<String> {
        return listOfNotNull(
            Data.javaVersion().takeIf { javaVersion },
            Data.userName().takeIf { userName },
            Data.hostName().takeIf { hostName }
        )
    }
}

@Composable
fun StatusBar(
    data: List<String> = StatusBar.defaultData(),
    content: @Composable (RowScope.() -> Unit)? = null
) {
    val appState = LocalAppState.current
    val state = appState.state.value

    val running = when (state) {
        Status.None -> null
        is Status.Running -> state
    }


    //AuroraDecorationArea(decorationAreaType = DecorationAreaType.Footer) {
    CompositionLocalProvider(LocalContentColor provides Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialColor.Gray900),
        ) {
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                data.forEach {
                    StatusBarDivider()
                    StatusBarText(text = it)
                }
            }
            AnimatedVisibility(running != null) {
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (running?.showProgress == true) {
                        LinearProgressIndicator(
                            modifier = Modifier.width(128.dp)
                                .padding(horizontal = ToolboxDefaults.ITEM_SPACING)
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
fun StatusBarDivider() = VerticalDivider()

@Composable
fun StatusBarText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier.padding(
            horizontal = ToolboxDefaults.CONTENT_PADDING_SMALL,
            vertical = 2.dp
        ),
        style = style,
        text = text,
        fontWeight = FontWeight.Bold,
        maxLines = maxLines,
        color = color
    )
}
package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlin.time.Clock

object MyTextWithHelpDefaults {

    val icon: ImageVector = Icons.Outlined.Info
    val popupMaxWidth: Dp = 400.dp
    val popupXOffset: Dp = (-8).dp
    val popupYOffset: Dp = 8.dp
    val popupCardPadding: Dp = 4.dp
    val popupElevation: Dp = 8.dp
    val popupAlignment: Alignment = Alignment.TopEnd
    val iconPlacement: MyTextWithHelp.IconPlacement = MyTextWithHelp.IconPlacement.Inline
    val iconVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically

    val popupTitleSpacing: Dp = 4.dp
    val popupTitleStyle: TextStyle
        @Composable get() = MaterialTheme.typography.titleSmall
    val popupTextStyle: TextStyle
        @Composable get() = MaterialTheme.typography.bodySmall

    fun popupConfig(
        maxWidth: Dp = popupMaxWidth,
        xOffset: Dp = popupXOffset,
        yOffset: Dp = popupYOffset,
        cardPadding: Dp = popupCardPadding,
        elevation: Dp = popupElevation,
        alignment: Alignment = popupAlignment,
    ): MyTextWithHelp.PopupConfig = MyTextWithHelp.PopupConfig(
        maxWidth = maxWidth,
        xOffset = xOffset,
        yOffset = yOffset,
        cardPadding = cardPadding,
        elevation = elevation,
        alignment = alignment,
    )

    @Composable
    fun popupTextConfig(
        style: TextStyle = MyTextWithHelpDefaults.popupTextStyle,
        color: Color = Color.Unspecified,
        popupTitleStyle: TextStyle = MyTextWithHelpDefaults.popupTitleStyle,
        titleColor: Color = Color.Unspecified,
        popupTitleSpacing: Dp = MyTextWithHelpDefaults.popupTitleSpacing,
    ): MyTextWithHelp.PopupTextConfig = MyTextWithHelp.PopupTextConfig(
        style = style,
        color = color,
        titleStyle = popupTitleStyle,
        titleColor = titleColor,
        titleSpacing = popupTitleSpacing,
    )
}

object MyTextWithHelp {

    class PopupConfig internal constructor(
        val maxWidth: Dp,
        val xOffset: Dp,
        val yOffset: Dp,
        val cardPadding: Dp,
        val elevation: Dp,
        val alignment: Alignment,
    )

    class PopupTextConfig internal constructor(
        val style: TextStyle = TextStyle.Default,
        val color: Color = Color.Unspecified,
        val titleStyle: TextStyle = TextStyle.Default,
        val titleColor: Color = Color.Unspecified,
        val titleSpacing: Dp = 4.dp,
    )

    enum class IconPlacement {
        Inline,
        End,
    }
}

@Composable
fun MyTextWithHelp(
    text: String,
    help: String,
    helpTitle: String? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector = MyTextWithHelpDefaults.icon,
    iconSize: Dp? = null,
    iconPlacement: MyTextWithHelp.IconPlacement = MyTextWithHelpDefaults.iconPlacement,
    iconVerticalAlignment: Alignment.Vertical = MyTextWithHelpDefaults.iconVerticalAlignment,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    popupConfig: MyTextWithHelp.PopupConfig = MyTextWithHelpDefaults.popupConfig(),
    popupTextConfig: MyTextWithHelp.PopupTextConfig = MyTextWithHelpDefaults.popupTextConfig(),
) {
    MyTextWithHelp(
        text = text,
        modifier = modifier,
        icon = icon,
        iconSize = iconSize,
        iconPlacement = iconPlacement,
        iconVerticalAlignment = iconVerticalAlignment,
        style = style,
        color = color,
        popupConfig = popupConfig,
        popupContent = {
            if (helpTitle != null) {
                Text(
                    text = helpTitle,
                    style = popupTextConfig.titleStyle,
                    color = popupTextConfig.titleColor,
                )
                Spacer(Modifier.height(popupTextConfig.titleSpacing))
            }
            Text(
                text = help,
                style = popupTextConfig.style,
                color = popupTextConfig.color,
            )
        },
    )
}

@Composable
fun MyTextWithHelp(
    text: String,
    help: AnnotatedString,
    helpTitle: String? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector = MyTextWithHelpDefaults.icon,
    iconSize: Dp? = null,
    iconPlacement: MyTextWithHelp.IconPlacement = MyTextWithHelpDefaults.iconPlacement,
    iconVerticalAlignment: Alignment.Vertical = MyTextWithHelpDefaults.iconVerticalAlignment,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    popupConfig: MyTextWithHelp.PopupConfig = MyTextWithHelpDefaults.popupConfig(),
    popupTextConfig: MyTextWithHelp.PopupTextConfig = MyTextWithHelpDefaults.popupTextConfig(),
) {
    MyTextWithHelp(
        text = text,
        modifier = modifier,
        icon = icon,
        iconSize = iconSize,
        iconPlacement = iconPlacement,
        iconVerticalAlignment = iconVerticalAlignment,
        style = style,
        color = color,
        popupConfig = popupConfig,
        popupContent = {
            if (helpTitle != null) {
                Text(
                    text = helpTitle,
                    style = popupTextConfig.titleStyle,
                    color = popupTextConfig.titleColor,
                )
                Spacer(Modifier.height(popupTextConfig.titleSpacing))
            }
            Text(
                text = help,
                style = popupTextConfig.style,
                color = popupTextConfig.color,
            )
        },
    )
}

@Composable
fun MyTextWithHelp(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = MyTextWithHelpDefaults.icon,
    iconSize: Dp? = null,
    iconPlacement: MyTextWithHelp.IconPlacement = MyTextWithHelpDefaults.iconPlacement,
    iconVerticalAlignment: Alignment.Vertical = MyTextWithHelpDefaults.iconVerticalAlignment,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    popupConfig: MyTextWithHelp.PopupConfig = MyTextWithHelpDefaults.popupConfig(),
    popupContent: @Composable ColumnScope.() -> Unit,
) {
    val showInfo = remember { mutableStateOf(false) }
    val lastDismiss = remember { mutableLongStateOf(0L) }

    val density = LocalDensity.current
    val resolvedIconSize = iconSize ?: with(density) {
        LocalTextStyle.current.fontSize.toDp()
    }

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            val modifier = when (iconPlacement) {
                MyTextWithHelp.IconPlacement.Inline -> Modifier.weight(1f, false)
                MyTextWithHelp.IconPlacement.End -> Modifier.weight(1f)
            }
            val iconModifier = Modifier.align(iconVerticalAlignment)

            Text(
                modifier = modifier,
                text = text,
                style = style,
                color = color,
            )

            Spacer(Modifier.width(8.dp))

            HelpIconButton(
                modifier = iconModifier,
                icon = icon,
                iconSize = resolvedIconSize,
                showInfo = showInfo,
                lastDismiss = lastDismiss,
                popupConfig = popupConfig,
                popupContent = popupContent,
            )

        }
    }
}

@Composable
private fun HelpIconButton(
    modifier: Modifier,
    icon: ImageVector,
    iconSize: Dp,
    showInfo: MutableState<Boolean>,
    lastDismiss: MutableState<Long>,
    popupConfig: MyTextWithHelp.PopupConfig,
    popupContent: @Composable ColumnScope.() -> Unit,
) {
    IconButton(
        modifier = modifier.size(iconSize),
        onClick = {
            if (showInfo.value) {
                showInfo.value = false
            } else {
                if (Clock.System.now().toEpochMilliseconds() - lastDismiss.value < 200) {
                    return@IconButton
                }

                showInfo.value = true
            }
        },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        if (showInfo.value) {
            Popup(
                lastDismiss,
                showInfo,
                popupConfig.maxWidth,
                popupConfig.xOffset,
                popupConfig.yOffset,
                popupConfig.cardPadding,
                popupConfig.elevation,
                popupConfig.alignment,
                popupContent,
            )
        }
    }
}

@Composable
private fun Popup(
    lastDismiss: MutableState<Long>,
    showInfo: MutableState<Boolean>,
    popupMaxWidth: Dp,
    popupXOffset: Dp,
    popupYOffset: Dp,
    popupCardPadding: Dp,
    popupElevation: Dp,
    popupAlignment: Alignment,
    popupContent: @Composable (ColumnScope.() -> Unit),
) {
    val density = LocalDensity.current
    val xOffsetPx = with(density) { popupXOffset.toPx().toInt() }
    val yOffsetPx = with(density) { popupYOffset.toPx().toInt() }
    Popup(
        alignment = popupAlignment,
        offset = IntOffset(xOffsetPx, yOffsetPx),
        onDismissRequest = {
            lastDismiss.value = Clock.System.now().toEpochMilliseconds()
            showInfo.value = false
        },
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = popupMaxWidth)
                .padding(popupCardPadding),
            elevation = CardDefaults.cardElevation(
                popupElevation
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                popupContent()
            }
        }
    }
}
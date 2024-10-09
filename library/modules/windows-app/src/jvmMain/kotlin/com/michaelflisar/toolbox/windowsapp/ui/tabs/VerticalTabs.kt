package com.michaelflisar.toolbox.windowsapp.ui.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val LocalVerticalTabStyle =
    compositionLocalOf<VerticalTabStyle> { VerticalTabStyle.Stripe() }

sealed class VerticalTabStyle {
    enum class Side {
        Left,
        Right
    }

    data object None : VerticalTabStyle()
    class Stripe(val side: Side = Side.Left, val width: Dp = 16.dp) : VerticalTabStyle()

    class Highlight(
        val side: Side = Side.Left,
        val backgroundColor: Color,
        val contentColor: Color
    ) :
        VerticalTabStyle()
}

@Composable
fun VerticalTabs(
    modifier: Modifier = Modifier,
    verticalTabStyle: VerticalTabStyle = VerticalTabStyle.Stripe(),
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.background,
        LocalVerticalTabStyle provides verticalTabStyle
    ) {
        Column(
            modifier = modifier.background(MaterialTheme.colorScheme.onBackground)
        ) {
            content()
        }
    }
}

@Composable
fun VerticalTabItem(
    item: TabItem.Item,
    selectedTab: MutableState<Int>
) {
    val style = LocalVerticalTabStyle.current
    val selected = remember(item, selectedTab.value) {
        derivedStateOf {
            selectedTab.value == item.id
        }
    }
    Row(
        modifier = Modifier.height(IntrinsicSize.Min).fillMaxWidth()
    ) {
        Marker(
            style,
            VerticalTabStyle.Side.Left,
            selected
        )
        TabButton(modifier = Modifier.weight(1f), style, selected, item, selectedTab)
        Marker(
            style,
            VerticalTabStyle.Side.Right,
            selected
        )
    }
}

@Composable
fun VerticalTabIconItem(
    item: TabItem.Item,
    icon: Painter,
    painterIsIcon: Boolean,
    selectedTab: MutableState<Int>
) {
    val style = LocalVerticalTabStyle.current
    val selected = remember(item, selectedTab.value) {
        derivedStateOf {
            selectedTab.value == item.id
        }
    }
    Row(
        modifier = Modifier.height(IntrinsicSize.Min).fillMaxWidth()
    ) {
        Marker(
            style,
            VerticalTabStyle.Side.Left,
            selected
        )
        TabIconButton(
            modifier = Modifier.weight(1f),
            style,
            selected,
            icon,
            painterIsIcon,
            item,
            selectedTab
        )

        Marker(
            style,
            VerticalTabStyle.Side.Right,
            selected
        )
    }
}

@Composable
private fun Marker(
    style: VerticalTabStyle,
    side: VerticalTabStyle.Side,
    selected: State<Boolean>
) {
    if (style !is VerticalTabStyle.Stripe)
        return
    if (style.side != side)
        return
    val indicatorWidth by animateDpAsState(if (selected.value) 8.dp else 0.dp)
    Box(
        modifier = Modifier.width(indicatorWidth).fillMaxHeight()
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun TabButton(
    modifier: Modifier,
    style: VerticalTabStyle,
    selected: State<Boolean>,
    item: TabItem.Item,
    selectedTab: MutableState<Int>
) {
    when (style) {
        VerticalTabStyle.None,
        is VerticalTabStyle.Stripe -> {
            OutlinedButton(
                modifier = modifier.fillMaxHeight(),
                onClick = {
                    selectedTab.value = item.id
                },
                shape = RectangleShape
            ) {
                Text(
                    text = item.label,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }

        is VerticalTabStyle.Highlight -> {
            Box(
                modifier = modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Min)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = selected.value,
                    modifier = Modifier
                        .align(if (style.side == VerticalTabStyle.Side.Left) Alignment.CenterStart else Alignment.CenterEnd),
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally( )
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(style.backgroundColor)
                    )
                }
                val contentColor by animateColorAsState(
                    if (selected.value) {
                        style.contentColor
                    } else MaterialTheme.colorScheme.primary
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxHeight(),
                    onClick = {
                        selectedTab.value = item.id
                    },
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = contentColor
                    )
                ) {
                    Text(
                        text = item.label,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
private fun TabIconButton(
    modifier: Modifier,
    style: VerticalTabStyle,
    selected: State<Boolean>,
    icon: Painter,
    painterIsIcon: Boolean,
    item: TabItem.Item,
    selectedTab: MutableState<Int>
) {
    when (style) {
        VerticalTabStyle.None,
        is VerticalTabStyle.Stripe -> {
            IconButton(
                modifier = modifier.fillMaxHeight(),
                onClick = {
                    selectedTab.value = item.id
                }
            ) {
                TabIconButtonContent(icon, painterIsIcon, item.label)
            }
        }

        is VerticalTabStyle.Highlight -> {
            Box(
                modifier = modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Min)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = selected.value,
                    modifier = Modifier
                        .align(if (style.side == VerticalTabStyle.Side.Left) Alignment.CenterStart else Alignment.CenterEnd),
                    enter = expandHorizontally(
                        //expandFrom = if (markerStyle.side == VerticalTabStyle.Side.Left) Alignment.Start else Alignment.End
                    ),
                    exit = shrinkHorizontally(
                        //shrinkTowards = if (markerStyle.side == VerticalTabStyle.Side.Left) Alignment.Start else Alignment.End
                    )
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(style.backgroundColor)
                    )
                }
                val contentColor by animateColorAsState(
                    if (selected.value) {
                        style.takeIf { selected.value }?.let { it.contentColor }
                            ?: MaterialTheme.colorScheme.primary
                    } else MaterialTheme.colorScheme.primary
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxHeight(),
                    onClick = {
                        selectedTab.value = item.id
                    },
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = contentColor
                    )
                ) {
                    TabIconButtonContent(icon, painterIsIcon, item.label)
                }
            }
        }
    }
}

@Composable
private fun TabIconButtonContent(
    icon: Painter,
    painterIsIcon: Boolean,
    label: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (painterIsIcon) {
            Icon(
                modifier = if (label == null) Modifier else Modifier.size(24.dp)
                    .align(Alignment.CenterVertically),
                painter = icon,
                contentDescription = null
            )
        } else {
            Image(
                modifier = if (label == null) Modifier else Modifier.size(24.dp)
                    .align(Alignment.CenterVertically),
                painter = icon,
                contentDescription = null
            )
        }
        label?.let {
            Text(it)
        }
    }
}
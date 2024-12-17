package com.michaelflisar.toolbox.windows.jewel

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed interface IJewelNavigationItem

class JewelNavigationItem(
    val title: String,
    val icon: @Composable () -> Unit,
    val content: @Composable () -> Unit
) : IJewelNavigationItem {
    constructor(
        title: String,
        imageVector: ImageVector,
        content: @Composable () -> Unit
    ) : this(title, { Icon(imageVector, null) }, content)
}

class JewelNavigationItemSpacer(val weight: Float = 1f) : IJewelNavigationItem

data object JewelNavigationItemDivider : IJewelNavigationItem

object JewelNavigation {
    class Setup(
        val expanded: Boolean = false,
        val showExpand: Boolean = true,
        val minWidth: Dp = 0.dp
    )
}

@Composable
fun JewelNavigation(
    items: List<IJewelNavigationItem>,
    selected: MutableState<Int>,
    setup: JewelNavigation.Setup = JewelNavigation.Setup()
) {
    val expanded = remember { mutableStateOf(setup.expanded) }
    Rail(items, selected, expanded, setup)
}

@Composable
private fun Rail(
    items: List<IJewelNavigationItem>,
    selected: MutableState<Int>,
    expanded: MutableState<Boolean>,
    setup: JewelNavigation.Setup
) {
    Row {
        Column(
            modifier = Modifier.fillMaxHeight()
                .wrapContentWidth()
                .animateContentSize()
                .width(IntrinsicSize.Min)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (setup.showExpand) {
                NavExpandableItem {
                    expanded.value = !expanded.value
                }
            }
            items.forEach {
                when (it) {
                    is JewelNavigationItem -> {
                        NavItem(it, items, selected, expanded, setup)
                    }

                    JewelNavigationItemDivider -> {
                        HorizontalDivider()
                    }

                    is JewelNavigationItemSpacer -> {
                        Spacer(modifier = Modifier.weight(it.weight))
                    }
                }
            }
        }
        VerticalDivider()
    }
}

@Composable
private fun NavExpandableItem(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = remember { mutableStateOf(false) }
    LaunchedEffect(interactionSource, pressed) {
        interactionSource.interactions.collect {
            pressed.value = it is PressInteraction.Press
        }
    }
    val scale by animateFloatAsState(
        if (pressed.value) .6f else 1f,
        animationSpec = tween(100)
    )

    NavRow(onClick = onClick, interactionSource = interactionSource, fill = false) {
        SelectionIndicator(false)
        NavIcon {
            Icon(
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                },
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu"
            )
        }
    }
}

@Composable
private fun NavItem(
    item: JewelNavigationItem,
    items: List<IJewelNavigationItem>,
    selected: MutableState<Int>,
    expanded: MutableState<Boolean>,
    setup: JewelNavigation.Setup
) {
    val isSelected by remember {
        derivedStateOf {
            selected.value == items.indexOf(item)
        }
    }
    NavRow(
        onClick = {
            selected.value = items.indexOf(item)
        }
    ) {
        SelectionIndicator(isSelected)
        NavIcon(item.icon)
        NavText(expanded.value, item.title, setup)
    }
}

@Composable
private fun NavRow(
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    fill: Boolean = true,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(interactionSource, LocalIndication.current) {
                onClick()
            }
            .padding(all = 4.dp)
            .height(IntrinsicSize.Min)
            .then(if (fill) Modifier.fillMaxWidth() else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun NavIcon(icon: @Composable () -> Unit) {
    Box(modifier = Modifier.size(28.dp).padding(4.dp)) {
        icon()
    }
}

@Composable
private fun NavText(
    expanded: Boolean,
    title: String,
    setup: JewelNavigation.Setup
) {
    if (setup.showExpand) {
        if (expanded) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .widthIn(min = setup.minWidth),
                style = MaterialTheme.typography.titleSmall
            )
        }
    } else {
        Text(
            text = title,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .widthIn(min = setup.minWidth),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun SelectionIndicator(isSelected: Boolean) {
    val alpha by animateFloatAsState(if (isSelected) 1f else 0f)
    Spacer(
        modifier = Modifier
            .width(3.dp)
            .fillMaxHeight()
            .padding(vertical = 4.dp)
            .clip(MaterialTheme.shapes.small)
            .alpha(alpha)
            .background(MaterialTheme.colorScheme.primary)
    )
}
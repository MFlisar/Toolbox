package com.michaelflisar.toolbox.windowsapp.ui.pane

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.michaelflisar.toolbox.ToolboxDefaults

enum class PaneSide {
    Left, Right
}

@Composable
fun DesktopPaneSide(
    modifier: Modifier,
    side: PaneSide,
    label: String,
    expanded: MutableState<Boolean>,
    divider: Boolean = true,
    showLabelWhenCollapsed: Boolean = true,
    collapsible: Boolean = true,
    content: (@Composable ColumnScope.() -> Unit)
) {
    Row(modifier = modifier) {
        if (side == PaneSide.Right && divider) {
            VerticalDivider()
        }
        Column(
            modifier = Modifier.fillMaxHeight().weight(1f, fill = false),
            horizontalAlignment = if (side == PaneSide.Left) Alignment.Start else Alignment.End
        ) {
            // Header (Icon + Title)
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (side == PaneSide.Left) {
                    if (collapsible)
                        ExpandIcon(expanded)
                    Title(label, expanded)
                    //Spacer(Modifier.width(ToolboxDefaults.CONTENT_PADDING_SMALL))
                } else {
                    //Spacer(Modifier.width(ToolboxDefaults.CONTENT_PADDING_SMALL))
                    Title(label, expanded)
                    if (collapsible)
                        ExpandIcon(expanded)
                }
            }

            Box(
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterHorizontally)
            ) {

                // Content
                androidx.compose.animation.AnimatedVisibility(
                    modifier = Modifier.align(if (side == PaneSide.Left) Alignment.CenterStart else Alignment.CenterEnd),
                    visible = expanded.value,
                    enter = fadeIn() + expandHorizontally(
                        expandFrom = if (side == PaneSide.Left) Alignment.End else Alignment.Start
                    ),
                    exit = fadeOut() + shrinkHorizontally(
                        shrinkTowards = if (side == PaneSide.Left) Alignment.End else Alignment.Start
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(ToolboxDefaults.CONTENT_PADDING_SMALL)
                        //.verticalScroll(rememberScrollState())
                        ,
                        verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
                    ) {
                        content()
                    }
                }

                // Text
                if (showLabelWhenCollapsed) {
                    androidx.compose.animation.AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .zIndex(1f),
                        visible = !expanded.value,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        VerticalTitle(side, label) {
                            expanded.value = !expanded.value
                        }
                    }
                }
            }
        }
        if (side == PaneSide.Left && divider) {
            VerticalDivider()
        }
    }
}

@Composable
private fun ExpandIcon(
    expanded: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = {
            expanded.value = !expanded.value
        }) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null
        )
    }
}

@Composable
private fun Title(
    label: String,
    expanded: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = expanded.value,
        enter = fadeIn() + expandHorizontally(),
        exit = fadeOut() + shrinkHorizontally(),
    ) {
        Text(
            modifier = modifier.padding(ToolboxDefaults.ITEM_SPACING),
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun VerticalTitle(
    side: PaneSide,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier

            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .vertical()
                .rotate(if (side == PaneSide.Left) -90f else 90f),
            text = label,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

private fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }
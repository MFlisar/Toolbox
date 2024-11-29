package com.michaelflisar.toolbox.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ToolboxDefaults

object MyExpandableTitle {
    enum class IconPlacement {
        Left, Right, Hide
    }
}

@Composable
fun MyExpandableTitle(
    title: String,
    expanded: MutableState<Boolean> = remember { mutableStateOf(true) },
    info: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    iconPlacement: MyExpandableTitle.IconPlacement = MyExpandableTitle.IconPlacement.Left,
    content: @Composable ColumnScope.() -> Unit
) {
    MyExpandableTitle(
        title,
        expanded.value,
        info,
        { expanded.value = !expanded.value },
        modifier,
        color,
        iconPlacement,
        content
    )
}

@Composable
fun MyExpandableTitle(
    text: String,
    expanded: Boolean,
    info: (@Composable () -> Unit)? = null,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    iconPlacement: MyExpandableTitle.IconPlacement = MyExpandableTitle.IconPlacement.Left,
    content: @Composable ColumnScope.() -> Unit
) {
    val rotation by animateFloatAsState(if (expanded) (if (iconPlacement == MyExpandableTitle.IconPlacement.Left) -180f else 180f) else 0f)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onToggle()
                    }
                    .padding(
                        start = when (iconPlacement) {
                            MyExpandableTitle.IconPlacement.Left -> 0.dp
                            MyExpandableTitle.IconPlacement.Right,
                            MyExpandableTitle.IconPlacement.Hide -> 8.dp
                        },
                        bottom = 8.dp,
                        top = 8.dp,
                        end = when (iconPlacement) {
                            MyExpandableTitle.IconPlacement.Right -> 0.dp
                            MyExpandableTitle.IconPlacement.Left,
                            MyExpandableTitle.IconPlacement.Hide -> 8.dp
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (iconPlacement == MyExpandableTitle.IconPlacement.Left) {
                    Icon(rotation, color)
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = text,
                    //style = MaterialTheme.typography.titleSmall,
                    //fontWeight = FontWeight.Bold,
                    color = color
                )
                if (info != null) {
                    info()
                }
                if (iconPlacement == MyExpandableTitle.IconPlacement.Right) {
                    Icon(rotation, color)
                }
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun MyExpandableOutlinedTitle(
    title: String,
    expanded: MutableState<Boolean> = remember { mutableStateOf(true) },
    info: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    iconPlacement: MyExpandableTitle.IconPlacement = MyExpandableTitle.IconPlacement.Left,
    content: @Composable ColumnScope.() -> Unit
) {
    MyExpandableOutlinedTitle(
        title,
        expanded.value,
        info,
        { expanded.value = !expanded.value },
        modifier,
        color,
        iconPlacement,
        content
    )
}

@Composable
fun MyExpandableOutlinedTitle(
    text: String,
    expanded: Boolean,
    info: (@Composable () -> Unit)? = null,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    iconPlacement: MyExpandableTitle.IconPlacement = MyExpandableTitle.IconPlacement.Left,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard {
        MyExpandableTitle(text, expanded, info, onToggle, modifier, color, iconPlacement, content)
    }
}

@Composable
private fun Icon(rotation: Float, color: Color) {
    Icon(
        modifier = Modifier.rotate(rotation),
        imageVector = Icons.Default.ArrowDropDown,
        tint = color.takeIf { it != Color.Unspecified }
            ?: LocalContentColor.current,//.copy(alpha = LocalContentAlpha.current),
        contentDescription = null
    )
}
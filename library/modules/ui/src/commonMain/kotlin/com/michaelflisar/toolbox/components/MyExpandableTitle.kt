package com.michaelflisar.toolbox.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.michaelflisar.toolbox.classes.LocalStyle
import kotlin.math.exp

object MyExpandableTitle {
    enum class IconPlacement {
        Left, Right, Hide
    }
}

@Composable
fun MyExpandableTitle(
    title: String,
    expanded: MutableState<Boolean> = remember { mutableStateOf(true) },
    expandable: Boolean = true,
    hideIconIfNotExpandable: Boolean = true,
    info: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    iconPlacement: MyExpandableTitle.IconPlacement = MyExpandableTitle.IconPlacement.Left,
    content: @Composable () -> Unit
) {
    MyExpandableTitle(
        title,
        expanded.value,
        expandable,
        hideIconIfNotExpandable,
        info,
        { expanded.value = !expanded.value },
        modifier,
        color,
        background,
        iconPlacement,
        content
    )
}

@Composable
fun MyExpandableTitle(
    title: String,
    expanded: Boolean,
    expandable: Boolean = true,
    hideIconIfNotExpandable: Boolean = true,
    info: (@Composable () -> Unit)? = null,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    iconPlacement: MyExpandableTitle.IconPlacement = MyExpandableTitle.IconPlacement.Left,
    content: @Composable () -> Unit
) {
    val rotation by animateFloatAsState(if (expanded) (if (iconPlacement == MyExpandableTitle.IconPlacement.Left) -180f else 180f) else 0f)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingDefault)
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .then(if (background != Color.Unspecified) Modifier.background(background) else Modifier)
                    .clickable {
                        onToggle()
                    }
                    .padding(
                        start = when (iconPlacement) {
                            MyExpandableTitle.IconPlacement.Left -> 0.dp
                            MyExpandableTitle.IconPlacement.Right,
                            MyExpandableTitle.IconPlacement.Hide -> LocalStyle.current.paddingDefault
                        },
                        bottom = LocalStyle.current.paddingDefault,
                        top = LocalStyle.current.paddingDefault,
                        end = LocalStyle.current.paddingDefault
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (iconPlacement == MyExpandableTitle.IconPlacement.Left && (expandable || !hideIconIfNotExpandable)) {
                    Icon(rotation, color)
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    //style = MaterialTheme.typography.titleSmall,
                    //fontWeight = FontWeight.Bold,
                    color = color
                )
                if (info != null) {
                    info()
                }
                if (iconPlacement == MyExpandableTitle.IconPlacement.Right && (expandable || !hideIconIfNotExpandable)) {
                    Icon(rotation, color)
                }
            }
        }
        AnimatedVisibility(visible = expandable && expanded) {
            content()
        }
    }
}

@Composable
fun MyExpandableOutlinedTitle(
    title: String,
    expanded: MutableState<Boolean> = remember { mutableStateOf(true) },
    expandable: Boolean = true,
    hideIconIfNotExpandable: Boolean = true,
    info: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    iconPlacement: MyExpandableTitle.IconPlacement = MyExpandableTitle.IconPlacement.Left,
    content: @Composable () -> Unit
) {
    MyExpandableOutlinedTitle(
        title,
        expanded.value,
        expandable,
        hideIconIfNotExpandable,
        info,
        { expanded.value = !expanded.value },
        modifier,
        color,
        background,
        iconPlacement,
        content
    )
}

@Composable
fun MyExpandableOutlinedTitle(
    text: String,
    expanded: Boolean,
    expandable: Boolean = true,
    hideIconIfNotExpandable: Boolean = true,
    info: (@Composable () -> Unit)? = null,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    iconPlacement: MyExpandableTitle.IconPlacement = MyExpandableTitle.IconPlacement.Left,
    content: @Composable () -> Unit
) {
    OutlinedCard {
        MyExpandableTitle(text, expanded, expandable, hideIconIfNotExpandable, info, onToggle, modifier, color, background, iconPlacement, content)
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
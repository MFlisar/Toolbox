package com.michaelflisar.toolbox.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.LocalTheme
import com.michaelflisar.toolbox.components.MyExpandableTitle.IconPlacement

object MyExpandableTitle {
    enum class IconPlacement {
        Left, Right, Hide
    }

    class Style(
        val iconPlacement: IconPlacement,
        val hideIconIfNotExpandable: Boolean,
        val contentPadding: PaddingValues,
        val shape: MutableState<Shape>,
        val borderColor: MutableState<Color>,
        val containerColor: MutableState<Color>,
        val contentColor: MutableState<Color>,
        val applyColorsToTitle: Boolean,
        val applyColorsToContent: Boolean,
    ) {
        internal fun getRootContainerColor(): Color? {
            return if (applyColorsToTitle && applyColorsToContent) {
                containerColor.value.takeIf { it != Color.Unspecified }
            } else {
                null
            }
        }

        internal fun getRootContentColor(): Color? {
            return if (applyColorsToTitle && applyColorsToContent) {
                contentColor.value.takeIf { it != Color.Unspecified }
            } else {
                null
            }
        }

        internal fun getTitleContainerColor(): Color? {
            return if (applyColorsToTitle) {
                containerColor.value.takeIf { it != Color.Unspecified }
            } else {
                null
            }
        }

        internal fun getContentContainerColor(): Color? {
            return if (applyColorsToContent) {
                containerColor.value.takeIf { it != Color.Unspecified }
            } else {
                null
            }
        }

        internal fun getTitleContentColor(): Color? {
            return if (applyColorsToTitle) {
                contentColor.value.takeIf { it != Color.Unspecified }
            } else {
                null
            }
        }

        internal fun getContentContentColor(): Color? {
            return if (applyColorsToContent) {
                contentColor.value.takeIf { it != Color.Unspecified }
            } else {
                null
            }
        }
    }
}

@Composable
fun rememberMyExpandableTitleStyle(
    iconPlacement: IconPlacement = IconPlacement.Left,
    hideIconIfNotExpandable: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(all = 8.dp),
    shape: Shape = MaterialTheme.shapes.small,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    applyColorsToTitle: Boolean = true,
    applyColorsToContent: Boolean = false,
): MyExpandableTitle.Style {
    return MyExpandableTitle.Style(
        iconPlacement = iconPlacement,
        hideIconIfNotExpandable = hideIconIfNotExpandable,
        contentPadding = contentPadding,
        shape = remember(shape) { mutableStateOf(shape) },
        borderColor = remember(borderColor) { mutableStateOf(borderColor) },
        containerColor = remember(containerColor) { mutableStateOf(containerColor) },
        contentColor = remember(contentColor) { mutableStateOf(contentColor) },
        applyColorsToTitle = applyColorsToTitle,
        applyColorsToContent = applyColorsToContent
    )
}

@Composable
fun MyExpandableTitle(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: MyExpandableTitle.Style = rememberMyExpandableTitleStyle(),
    expanded: MutableState<Boolean> = remember { mutableStateOf(true) },
    expandable: Boolean = true,
    info: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    MyExpandableTitle(
        title,
        modifier,
        style,
        expanded.value,
        expandable,
        info,
        { expanded.value = !expanded.value },
        content
    )
}

@Composable
fun MyExpandableTitle(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: MyExpandableTitle.Style = rememberMyExpandableTitleStyle(),
    expanded: Boolean = true,
    expandable: Boolean = true,
    info: (@Composable () -> Unit)? = null,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    val rotation by animateFloatAsState(if (expanded) (if (style.iconPlacement == IconPlacement.Left) -180f else 180f) else 0f)
    Column(
        modifier = modifier
            .then(
                if (style.borderColor.value != Color.Unspecified) {
                    Modifier.border(1.dp, style.borderColor.value, style.shape.value)
                } else {
                    Modifier
                }
            )
            .clip(style.shape.value)
            .then(
                style.getRootContainerColor()?.let {
                    Modifier.background(it)
                } ?: Modifier
            )
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (style.borderColor.value != Color.Unspecified) {
                            Modifier
                        } else {
                            Modifier .clip(style.shape.value)
                        }
                    )
                    .then(
                        style.getTitleContainerColor()?.let {
                            Modifier.background(it)
                        } ?: Modifier
                    )
                    .clickable(expandable) {
                        onToggle()
                    }
                    .padding(
                        start = when (style.iconPlacement) {
                            IconPlacement.Left -> 0.dp
                            IconPlacement.Right,
                            IconPlacement.Hide,
                                -> LocalTheme.current.padding.default
                        },
                        bottom = LocalTheme.current.padding.default,
                        top = LocalTheme.current.padding.default,
                        end = LocalTheme.current.padding.default
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (style.iconPlacement == IconPlacement.Left && (expandable || !style.hideIconIfNotExpandable)) {
                    Icon(rotation, style.getTitleContentColor())
                }

                CompositionLocalProvider(
                    LocalContentColor provides  (style.getTitleContentColor() ?: LocalContentColor.current)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        title()
                    }
                    if (info != null) {
                        info()
                    }
                }

                if (style.iconPlacement == IconPlacement.Right && (expandable || !style.hideIconIfNotExpandable)) {
                    Icon(rotation, style.getTitleContentColor())
                }
            }
        }
        AnimatedVisibility(
            visible = expandable && expanded,
            modifier = Modifier
                .then(
                    style.getContentContainerColor()?.let {
                        Modifier.background(it)
                    } ?: Modifier
                )
        ) {
            CompositionLocalProvider(
                LocalContentColor provides (style.getContentContentColor() ?: LocalContentColor.current)
            ) {
                Column(
                    modifier = Modifier.padding(style.contentPadding)
                ) {
                    content()
                }

            }
        }
    }
}
/*
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
    iconPlacement: IconPlacement = IconPlacement.Left,
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
}*/

@Composable
private fun Icon(rotation: Float, color: Color?) {
    Icon(
        modifier = Modifier.rotate(rotation),
        imageVector = Icons.Default.ArrowDropDown,
        tint = color?.takeIf { it != Color.Unspecified }
            ?: LocalContentColor.current,//.copy(alpha = LocalContentAlpha.current),
        contentDescription = null
    )
}
package com.michaelflisar.toolbox.ui.adaptive.implementations.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ui.adaptive.ProvideCustomStyledContentLocals
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme
import com.michaelflisar.toolbox.ui.adaptive.runtime.InternalComposeStyledApi
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledContentColor

object StyledExpandableTitle {
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
fun rememberStyledExpandableTitleStyle(
    iconPlacement: StyledExpandableTitle.IconPlacement = StyledExpandableTitle.IconPlacement.Left,
    hideIconIfNotExpandable: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(all = StyledTheme.padding.medium),
    shape: Shape = StyledTheme.shapes.small,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    applyColorsToTitle: Boolean = true,
    applyColorsToContent: Boolean = false,
): StyledExpandableTitle.Style {
    return StyledExpandableTitle.Style(
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
fun StyledExpandableTitle(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: StyledExpandableTitle.Style = rememberStyledExpandableTitleStyle(),
    expanded: MutableState<Boolean> = remember { mutableStateOf(true) },
    canToggle: Boolean = true,
    info: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    StyledExpandableTitleImpl(
        title = title,
        modifier = modifier,
        style = style,
        expanded = expanded.value,
        canToggle = canToggle,
        info = info,
        onToggle = { expanded.value = !expanded.value },
        content = content
    )
}

@Composable
fun StyledExpandableTitle(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: StyledExpandableTitle.Style = rememberStyledExpandableTitleStyle(),
    expanded: Boolean = true,
    canToggle: Boolean = true,
    info: (@Composable () -> Unit)? = null,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    StyledExpandableTitleImpl(
        title = title,
        modifier = modifier,
        style = style,
        expanded = expanded,
        canToggle = canToggle,
        info = info,
        onToggle = onToggle,
        content = content
    )
}

@OptIn(InternalComposeStyledApi::class)
@Composable
private fun StyledExpandableTitleImpl(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: StyledExpandableTitle.Style = rememberStyledExpandableTitleStyle(),
    expanded: Boolean = true,
    canToggle: Boolean = true,
    info: (@Composable () -> Unit)? = null,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
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
        ProvideCustomStyledContentLocals(
            textStyle = StyledTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (style.borderColor.value != Color.Unspecified) {
                            Modifier
                        } else {
                            Modifier.clip(style.shape.value)
                        }
                    )
                    .then(
                        style.getTitleContainerColor()?.let {
                            Modifier.background(it)
                        } ?: Modifier
                    )
                    .clickable(canToggle) {
                        onToggle()
                    }
                    .padding(
                        start = 0.dp,
                        bottom = StyledTheme.padding.medium,
                        top = StyledTheme.padding.medium,
                        end = 0.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (style.iconPlacement == StyledExpandableTitle.IconPlacement.Left && (canToggle || !style.hideIconIfNotExpandable)) {
                    StyledExpandIcon(
                        expanded,
                        sideIsLeft = true,
                        color = style.getTitleContentColor() ?: LocalStyledContentColor.current
                    )
                } else {
                    Spacer(modifier = Modifier.width(StyledTheme.padding.medium))
                }

                ProvideCustomStyledContentLocals(
                    contentColor = style.getTitleContentColor()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        title()
                    }
                    if (info != null) {
                        info()
                    }
                }

                if (style.iconPlacement == StyledExpandableTitle.IconPlacement.Right && (canToggle || !style.hideIconIfNotExpandable)) {
                    StyledExpandIcon(
                        expanded = expanded,
                        sideIsLeft = false,
                        color = style.getTitleContentColor() ?: LocalStyledContentColor.current
                    )
                } else {
                    Spacer(modifier = Modifier.width(StyledTheme.padding.medium))
                }
            }
        }
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier
                .then(
                    style.getContentContainerColor()?.let {
                        Modifier.background(it)
                    } ?: Modifier
                )
        ) {
            ProvideCustomStyledContentLocals(
                contentColor = style.getContentContentColor()
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
package com.michaelflisar.toolbox.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.util.lerp
import com.michaelflisar.toolbox.theme.LocalComponentOptions
import com.michaelflisar.toolbox.theme.LocalComponentStyles
import com.michaelflisar.toolbox.theme.MyThemeType
import kotlin.math.floor
import kotlin.math.max

@Composable
fun MyCheckbox(
    title: String,
    checked: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = 1,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = LocalComponentOptions.current.checkbox.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MyCheckbox(
        modifier = modifier,
        title = if (title.isNotEmpty()) {
            { Text(title, maxLines = maxLines) }
        } else null,
        checked = checked.value,
        enabled = enabled,
        colors = colors,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MyCheckbox(
    title: @Composable () -> Unit,
    checked: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = LocalComponentOptions.current.checkbox.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MyCheckbox(
        modifier = modifier,
        title = title,
        checked = checked.value,
        enabled = enabled,
        colors = colors,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MyCheckbox(
    title: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = 1,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = LocalComponentOptions.current.checkbox.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MyCheckbox(
        modifier = modifier,
        title = if (title.isNotEmpty()) {
            { Text(title, maxLines = maxLines) }
        } else null,
        checked = checked,
        enabled = enabled,
        colors = colors,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly,
        onCheckedChange = onCheckedChange
    )
}

@Composable
fun MyCheckbox(
    title: @Composable () -> Unit,
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    style: TextStyle = LocalTextStyle.current,
    toggleOnIconClickOnly: Boolean = LocalComponentOptions.current.checkbox.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MyCheckbox(
        modifier = modifier,
        title = title,
        checked = checked,
        enabled = enabled,
        colors = colors,
        style = style,
        toggleOnIconClickOnly = toggleOnIconClickOnly,
        onCheckedChange = onCheckedChange
    )
}

@Composable
fun MyCheckbox(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)?,
    checked: Boolean,
    enabled: Boolean,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    style: TextStyle,
    toggleOnIconClickOnly: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    if (toggleOnIconClickOnly) {
        MyBaseCheckableItemWrapperOnly(
            modifier = modifier,
            title = title,
            style = style
        ) {
            MyCheckboxImpl(
                enabled = enabled,
                onCheckedChange = onCheckedChange,
                checked = checked,
                colors = colors,
            )

        }
    } else {
        MyBaseCheckableItem(
            modifier = modifier,
            title = title,
            checked = checked,
            enabled = enabled,
            style = style,
            onCheckedChange = onCheckedChange
        ) {
            MyCheckboxImpl(
                enabled = enabled,
                onCheckedChange = null,
                checked = checked,
                colors = colors,
            )
        }
    }
}

// -------------------------
// internal API
// -------------------------

@Composable
private fun MyCheckboxImpl(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val componentType = LocalComponentStyles.current.type
    when (componentType) {
        MyThemeType.Custom -> {
            CustomCheckbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = modifier.minimumInteractiveComponentSize(),
                enabled = enabled,
                colors = colors,
                interactionSource = interactionSource
            )
        }

        MyThemeType.Material3 -> {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = modifier.minimumInteractiveComponentSize(),
                enabled = enabled,
                colors = colors,
                interactionSource = interactionSource
            )
        }
    }
}

// -------------------------
// Custom Composables
// -------------------------

@Composable
private fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val strokeWidthPx = with(LocalDensity.current) { floor(CheckboxDefaults.StrokeWidth.toPx()) }
    TriStateCheckbox(
        state = ToggleableState(checked),
        onClick =
            if (onCheckedChange != null) {
                { onCheckedChange(!checked) }
            } else {
                null
            },
        checkmarkStroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Square),
        outlineStroke = Stroke(width = strokeWidthPx),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Composable
private fun TriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    checkmarkStroke: Stroke,
    outlineStroke: Stroke,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val toggleableModifier =
        if (onClick != null) {
            Modifier.triStateToggleable(
                state = state,
                onClick = onClick,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = false,
                    radius = LocalComponentStyles.current.checkbox.layerSize / 2
                ),
            )
        } else {
            Modifier
        }
    CheckboxImpl(
        enabled = enabled,
        value = state,
        modifier =
            modifier
                .then(
                    if (onClick != null) {
                        Modifier.minimumInteractiveComponentSize()
                    } else {
                        Modifier
                    }
                )
                .then(toggleableModifier)
                .padding(LocalComponentStyles.current.checkbox.padding),
        colors = colors,
        checkmarkStroke = checkmarkStroke,
        outlineStroke = outlineStroke,

        )
}

private val SnapAnimationDelay = 100

@Composable
private fun CheckboxImpl(
    enabled: Boolean,
    value: ToggleableState,
    modifier: Modifier,
    colors: CheckboxColors,
    checkmarkStroke: Stroke,
    outlineStroke: Stroke,
) {
    val transition = updateTransition(value)
    val defaultAnimationSpec =
        spring<Float>() // MotionSchemeKeyTokens.DefaultSpatial.value<Float>()
    val checkDrawFraction =
        transition.animateFloat(
            transitionSpec = {
                when {
                    // TODO Load the motionScheme tokens from the component tokens file
                    initialState == ToggleableState.Off -> defaultAnimationSpec
                    targetState == ToggleableState.Off -> snap(delayMillis = SnapAnimationDelay)
                    else -> defaultAnimationSpec
                }
            }
        ) {
            when (it) {
                ToggleableState.On -> 1f
                ToggleableState.Off -> 0f
                ToggleableState.Indeterminate -> 1f
            }
        }

    val checkCenterGravitationShiftFraction =
        transition.animateFloat(
            transitionSpec = {
                when {
                    // TODO Load the motionScheme tokens from the component tokens file
                    initialState == ToggleableState.Off -> snap()
                    targetState == ToggleableState.Off -> snap(delayMillis = SnapAnimationDelay)
                    else -> defaultAnimationSpec
                }
            }
        ) {
            when (it) {
                ToggleableState.On -> 0f
                ToggleableState.Off -> 0f
                ToggleableState.Indeterminate -> 1f
            }
        }
    val checkCache = remember { CheckDrawingCache() }
    val checkColor = colors.checkmarkColor(value)
    val boxColor = colors.boxColor(enabled, value)
    val borderColor = colors.borderColor(enabled, value)
    val radiusSize = LocalComponentStyles.current.checkbox.radiusSize
    Canvas(
        modifier.wrapContentSize(Alignment.Center)
            .requiredSize(LocalComponentStyles.current.checkbox.checkboxSize)
    ) {
        drawBox(
            boxColor = boxColor.value,
            borderColor = borderColor.value,
            radius = radiusSize.toPx(),
            stroke = outlineStroke,
        )
        drawCheck(
            checkColor = checkColor.value,
            checkFraction = checkDrawFraction.value,
            crossCenterGravitation = checkCenterGravitationShiftFraction.value,
            stroke = checkmarkStroke,
            drawingCache = checkCache,
        )
    }
}

private fun DrawScope.drawBox(boxColor: Color, borderColor: Color, radius: Float, stroke: Stroke) {
    val halfStrokeWidth = stroke.width / 2.0f
    val checkboxSize = size.width
    if (boxColor == borderColor) {
        drawRoundRect(
            boxColor,
            size = Size(checkboxSize, checkboxSize),
            cornerRadius = CornerRadius(radius),
            style = Fill,
        )
    } else {
        drawRoundRect(
            boxColor,
            topLeft = Offset(stroke.width, stroke.width),
            size = Size(checkboxSize - stroke.width * 2, checkboxSize - stroke.width * 2),
            cornerRadius = CornerRadius(max(0f, radius - stroke.width)),
            style = Fill,
        )
        drawRoundRect(
            borderColor,
            topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
            size = Size(checkboxSize - stroke.width, checkboxSize - stroke.width),
            cornerRadius = CornerRadius(radius - halfStrokeWidth),
            style = stroke,
        )
    }
}

@Composable
private fun CheckboxColors.checkmarkColor(state: ToggleableState): State<Color> {
    val target =
        if (state == ToggleableState.Off) {
            uncheckedCheckmarkColor
        } else {
            checkedCheckmarkColor
        }

    //return animateColorAsState(target, colorAnimationSpecForState(state))
    return animateColorAsState(target)
}

@Composable
private fun CheckboxColors.boxColor(enabled: Boolean, state: ToggleableState): State<Color> {
    val target =
        if (enabled) {
            when (state) {
                ToggleableState.On,
                ToggleableState.Indeterminate,
                    -> checkedBoxColor

                ToggleableState.Off -> uncheckedBoxColor
            }
        } else {
            when (state) {
                ToggleableState.On -> disabledCheckedBoxColor
                ToggleableState.Indeterminate -> disabledIndeterminateBoxColor
                ToggleableState.Off -> disabledUncheckedBoxColor
            }
        }

    // If not enabled 'snap' to the disabled state, as there should be no animations between
    // enabled / disabled.
    return if (enabled) {
        animateColorAsState(target) //, colorAnimationSpecForState(state))
    } else {
        rememberUpdatedState(target)
    }
}

/**
 * Represents the color used for the border of the checkbox, depending on [enabled] and [state].
 *
 * @param enabled whether the checkbox is enabled or not
 * @param state the [ToggleableState] of the checkbox
 */
@Composable
private fun CheckboxColors.borderColor(enabled: Boolean, state: ToggleableState): State<Color> {
    val target =
        if (enabled) {
            when (state) {
                ToggleableState.On,
                ToggleableState.Indeterminate,
                    -> checkedBorderColor

                ToggleableState.Off -> uncheckedBorderColor
            }
        } else {
            when (state) {
                ToggleableState.Indeterminate -> disabledIndeterminateBorderColor
                ToggleableState.On -> disabledBorderColor
                ToggleableState.Off -> disabledUncheckedBorderColor
            }
        }

    // If not enabled 'snap' to the disabled state, as there should be no animations between
    // enabled / disabled.
    return if (enabled) {
        animateColorAsState(target) //, colorAnimationSpecForState(state))
    } else {
        rememberUpdatedState(target)
    }
}

private fun DrawScope.drawCheck(
    checkColor: Color,
    checkFraction: Float,
    crossCenterGravitation: Float,
    stroke: Stroke,
    drawingCache: CheckDrawingCache,
) {
    val width = size.width
    val checkCrossX = 0.4f
    val checkCrossY = 0.7f
    val leftX = 0.2f
    val leftY = 0.5f
    val rightX = 0.8f
    val rightY = 0.3f

    val gravitatedCrossX = lerp(checkCrossX, 0.5f, crossCenterGravitation)
    val gravitatedCrossY = lerp(checkCrossY, 0.5f, crossCenterGravitation)
    // gravitate only Y for end to achieve center line
    val gravitatedLeftY = lerp(leftY, 0.5f, crossCenterGravitation)
    val gravitatedRightY = lerp(rightY, 0.5f, crossCenterGravitation)

    with(drawingCache) {
        checkPath.rewind()
        checkPath.moveTo(width * leftX, width * gravitatedLeftY)
        checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
        checkPath.lineTo(width * rightX, width * gravitatedRightY)
        // TODO: replace with proper declarative non-android alternative when ready (b/158188351)
        pathMeasure.setPath(checkPath, false)
        pathToDraw.rewind()
        pathMeasure.getSegment(0f, pathMeasure.length * checkFraction, pathToDraw, true)
    }
    drawPath(drawingCache.pathToDraw, checkColor, style = stroke)
}

@Immutable
private class CheckDrawingCache(
    val checkPath: Path = Path(),
    val pathMeasure: PathMeasure = PathMeasure(),
    val pathToDraw: Path = Path(),
)
package com.michaelflisar.toolbox.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import com.michaelflisar.toolbox.theme.ComponentStyles
import com.michaelflisar.toolbox.theme.LocalComponentOptions
import com.michaelflisar.toolbox.theme.LocalComponentStyles
import com.michaelflisar.toolbox.theme.MyThemeType
import kotlinx.coroutines.launch

@Composable
fun MySwitch(
    checked: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    colors: SwitchColors = SwitchDefaults.colors(),
    toggleOnIconClickOnly: Boolean = LocalComponentOptions.current.switch.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MySwitch(
        modifier = modifier,
        title = { Text(title, maxLines = maxLines) },
        checked = checked.value,
        enabled = enabled,
        style = style,
        colors = colors,
        toggleOnIconClickOnly = toggleOnIconClickOnly
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MySwitch(
    checked: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    colors: SwitchColors = SwitchDefaults.colors(),
    toggleOnIconClickOnly: Boolean = LocalComponentOptions.current.switch.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MySwitch(
        modifier = modifier,
        title = title,
        checked = checked.value,
        enabled = enabled,
        style = style,
        colors = colors,
        toggleOnIconClickOnly = toggleOnIconClickOnly
    ) {
        checked.value = it
        onCheckedChange(it)
    }
}

@Composable
fun MySwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    title: String = "",
    enabled: Boolean = true,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    colors: SwitchColors = SwitchDefaults.colors(),
    toggleOnIconClickOnly: Boolean = LocalComponentOptions.current.switch.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    MySwitch(
        modifier = modifier,
        title = if (title.isNotEmpty()) {
            { Text(title, maxLines = maxLines) }
        } else null,
        checked = checked,
        enabled = enabled,
        style = style,
        colors = colors,
        toggleOnIconClickOnly = toggleOnIconClickOnly,
        onCheckedChange = onCheckedChange
    )
}

@Composable
fun MySwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    colors: SwitchColors = SwitchDefaults.colors(),
    toggleOnIconClickOnly: Boolean = LocalComponentOptions.current.switch.toggleOnIconClickOnly,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    if (toggleOnIconClickOnly) {
        MyBaseCheckableItemWrapperOnly(
            modifier = modifier,
            title = title,
            style = style
        ) {
            MySwitchImpl(
                checked = checked,
                enabled = enabled,
                colors = colors,
                onCheckedChange = onCheckedChange
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
            MySwitchImpl(
                checked = checked,
                enabled = enabled,
                colors = colors,
                onCheckedChange = null
            )
        }
    }

}

// -------------------------
// internal API
// -------------------------

@Composable
private fun MySwitchImpl(
    checked: Boolean,
    enabled: Boolean,
    colors: SwitchColors,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    val componentType = LocalComponentStyles.current.type
    when (componentType) {
        MyThemeType.Material3 -> {
            Switch(
                modifier = Modifier.minimumInteractiveComponentSize(),
                checked = checked,
                enabled = enabled,
                colors = colors,
                onCheckedChange = onCheckedChange
            )
        }

        MyThemeType.Custom -> {
            CustomSwitch(
                modifier = Modifier.minimumInteractiveComponentSize(),
                checked = checked,
                enabled = enabled,
                colors = colors,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

// -------------------------
// Custom Composables
// -------------------------

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    // TODO: Add Swipeable modifier b/223797571
    val toggleableModifier =
        if (onCheckedChange != null) {
            Modifier.minimumInteractiveComponentSize()
                .toggleable(
                    value = checked,
                    onValueChange = onCheckedChange,
                    enabled = enabled,
                    role = Role.Switch,
                    interactionSource = interactionSource,
                    indication = null,
                )
        } else {
            Modifier
        }

    SwitchImpl(
        modifier =
            modifier
                .then(toggleableModifier)
                .wrapContentSize(Alignment.Center)
                .requiredSize(
                    LocalComponentStyles.current.switch.trackWidth,
                    LocalComponentStyles.current.switch.trackHeight
                ),
        checked = checked,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        thumbShape = LocalComponentStyles.current.switch.thumbShape, //SwitchTokens.HandleShape.value,
        thumbContent = thumbContent,
    )
}

@Composable
@Suppress("ComposableLambdaParameterNaming", "ComposableLambdaParameterPosition")
private fun SwitchImpl(
    modifier: Modifier,
    checked: Boolean,
    enabled: Boolean,
    colors: SwitchColors,
    thumbContent: (@Composable () -> Unit)?,
    interactionSource: InteractionSource,
    thumbShape: Shape,
) {
    val trackColor = colors.trackColor(enabled, checked)
    val resolvedThumbColor = colors.thumbColor(enabled, checked)
    val trackShape = LocalComponentStyles.current.switch.trackShape

    Box(
        modifier
            .border(
                LocalComponentStyles.current.switch.trackOutlinedWidth,
                colors.borderColor(enabled, checked),
                trackShape
            )
            .background(trackColor, trackShape)
    ) {
        Box(
            modifier =
                Modifier.align(Alignment.CenterStart)
                    .then(
                        ThumbElement(
                            interactionSource = interactionSource,
                            checked = checked,
                            // TODO Load the motionScheme tokens from the component tokens file
                            animationSpec = spring(),//MotionSchemeKeyTokens.FastSpatial.value(),
                            style = LocalComponentStyles.current.switch
                        )
                    )
                    .indication(
                        interactionSource = interactionSource,
                        indication =
                            ripple(
                                bounded = false,
                                radius = LocalComponentStyles.current.switch.layerSize / 2
                            ),
                    )
                    .background(resolvedThumbColor, thumbShape),
            contentAlignment = Alignment.Center,
        ) {
            if (thumbContent != null) {
                val iconColor = colors.iconColor(enabled, checked)
                CompositionLocalProvider(
                    LocalContentColor provides iconColor,
                    content = thumbContent,
                )
            }
        }
    }
}

@Stable
private fun SwitchColors.trackColor(enabled: Boolean, checked: Boolean): Color =
    if (enabled) {
        if (checked) checkedTrackColor else uncheckedTrackColor
    } else {
        if (checked) disabledCheckedTrackColor else disabledUncheckedTrackColor
    }

@Stable
private fun SwitchColors.thumbColor(enabled: Boolean, checked: Boolean): Color =
    if (enabled) {
        if (checked) checkedThumbColor else uncheckedThumbColor
    } else {
        if (checked) disabledCheckedThumbColor else disabledUncheckedThumbColor
    }

@Stable
private fun SwitchColors.iconColor(enabled: Boolean, checked: Boolean): Color =
    if (enabled) {
        if (checked) checkedIconColor else uncheckedIconColor
    } else {
        if (checked) disabledCheckedIconColor else disabledUncheckedIconColor
    }

@Stable
private fun SwitchColors.borderColor(enabled: Boolean, checked: Boolean): Color =
    if (enabled) {
        if (checked) checkedBorderColor else uncheckedBorderColor
    } else {
        if (checked) disabledCheckedBorderColor else disabledUncheckedBorderColor
    }


private data class ThumbElement(
    val interactionSource: InteractionSource,
    val checked: Boolean,
    val animationSpec: FiniteAnimationSpec<Float>,
    val style: ComponentStyles.Switch,
) : ModifierNodeElement<ThumbNode>() {
    override fun create() = ThumbNode(interactionSource, checked, animationSpec, style)

    override fun update(node: ThumbNode) {
        node.interactionSource = interactionSource
        if (node.checked != checked) {
            node.invalidateMeasurement()
        }
        node.checked = checked
        node.animationSpec = animationSpec
        node.update()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "switchThumb"
        properties["interactionSource"] = interactionSource
        properties["checked"] = checked
        properties["animationSpec"] = animationSpec
    }
}

private class ThumbNode(
    var interactionSource: InteractionSource,
    var checked: Boolean,
    var animationSpec: FiniteAnimationSpec<Float>,
    val style: ComponentStyles.Switch,
) : Modifier.Node(), LayoutModifierNode {

    override val shouldAutoInvalidate: Boolean
        get() = false

    private var isPressed = false
    private var offsetAnim: Animatable<Float, AnimationVector1D>? = null
    private var sizeAnim: Animatable<Float, AnimationVector1D>? = null
    private var initialOffset: Float = Float.NaN
    private var initialSize: Float = Float.NaN

    override fun onAttach() {
        coroutineScope.launch {
            var pressCount = 0
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> pressCount++
                    is PressInteraction.Release -> pressCount--
                    is PressInteraction.Cancel -> pressCount--
                }
                val pressed = pressCount > 0
                if (isPressed != pressed) {
                    isPressed = pressed
                    invalidateMeasurement()
                }
            }
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val hasContent =
            measurable.maxIntrinsicHeight(constraints.maxWidth) != 0 &&
                    measurable.maxIntrinsicWidth(constraints.maxHeight) != 0
        val size =
            when {
                isPressed -> style.pressedHandleWidth
                hasContent || checked -> style.thumbDiameter
                else -> style.uncheckedThumbDiameter
            }.toPx()

        val actualSize = (sizeAnim?.value ?: size).toInt()
        val placeable = measurable.measure(Constraints.fixed(actualSize, actualSize))
        val thumbPaddingStart = (style.trackHeight - size.toDp()) / 2f
        val minBound = thumbPaddingStart.toPx()
        val thumbPathLength =
            (style.trackWidth - style.thumbDiameter) - (style.trackHeight - style.thumbDiameter) / 2
        val maxBound = thumbPathLength.toPx()
        val offset =
            when {
                isPressed && checked -> maxBound - style.trackOutlinedWidth.toPx()
                isPressed && !checked -> style.trackOutlinedWidth.toPx()
                checked -> maxBound
                else -> minBound
            }

        if (sizeAnim?.targetValue != size) {
            coroutineScope.launch {
                sizeAnim?.animateTo(
                    size,
                    animationSpec
                )//if (isPressed) SnapSpec else animationSpec)
            }
        }

        if (offsetAnim?.targetValue != offset) {
            coroutineScope.launch {
                offsetAnim?.animateTo(
                    offset,
                    animationSpec
                )//if (isPressed) SnapSpec else animationSpec)
            }
        }

        if (initialSize.isNaN() && initialOffset.isNaN()) {
            initialSize = size
            initialOffset = offset
        }

        return layout(actualSize, actualSize) {
            placeable.placeRelative(offsetAnim?.value?.toInt() ?: offset.toInt(), 0)
        }
    }

    fun update() {
        if (sizeAnim == null && !initialSize.isNaN()) {
            sizeAnim = Animatable(initialSize)
        }

        if (offsetAnim == null && !initialOffset.isNaN()) offsetAnim = Animatable(initialOffset)
    }
}
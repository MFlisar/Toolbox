package com.michaelflisar.toolbox.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.error_invalid_input
import com.michaelflisar.toolbox.core.resources.error_invalid_length
import com.michaelflisar.toolbox.core.resources.error_too_high
import com.michaelflisar.toolbox.core.resources.error_too_low
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T : Number> MyNumberPicker(
    modifier: Modifier = Modifier,
    modifierInnerPicker: Modifier = Modifier,
    validator: MyNumberValidator<T>,
    parser: MyNumberParser<T>,
    value: MutableState<T>,
    label: String = "",
    enabled: Boolean = true,
    showButtons: Boolean = true,
    overrun: Pair<T, T>? = null,
    allowManualInput: Boolean = true,
    clearFocusOnButtonPress: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
    focused: Boolean = false,
    onCurrentValueIsInvalid: (text: String) -> Unit = {}
) {
    MyNumberPicker(
        modifier,
        modifierInnerPicker,
        validator,
        parser,
        value.value,
        label,
        enabled,
        showButtons,
        overrun,
        allowManualInput,
        clearFocusOnButtonPress,
        textStyle,
        focused,
        onCurrentValueIsInvalid
    ) {
        value.value = it
    }
}

@Composable
fun <T : Number> MyNumberPicker(
    modifier: Modifier = Modifier,
    modifierInnerPicker: Modifier = Modifier,
    validator: MyNumberValidator<T>,
    parser: MyNumberParser<T>,
    value: T,
    label: String = "",
    enabled: Boolean = true,
    showButtons: Boolean = true,
    overrun: Pair<T, T>? = null,
    allowManualInput: Boolean = true,
    clearFocusOnButtonPress: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
    focused: Boolean = false,
    onCurrentValueIsInvalid: (text: String) -> Unit = {},
    onValueChanged: (T) -> Unit = {}
) {
    val stateValue = remember(value) { mutableStateOf(value) }
    val stateText = remember(value) { mutableStateOf(value.toString()) }

    val focusManager = LocalFocusManager.current
    val lines = 1

    var buttonDecrease: @Composable (() -> Unit)? = null
    var buttonIncrease: @Composable (() -> Unit)? = null
    if (showButtons) {
        buttonDecrease = @Composable {
            RepeatingIconButton(
                enabled && (overrun != null || validator.hasValidPrev(stateValue, parser)),
                Icons.Default.Remove
            ) {
                val next =
                    if (overrun != null && !validator.hasValidPrev(stateValue, parser)) {
                        overrun.first
                    } else parser.getPrevValue(stateValue.value)
                stateValue.value = next
                stateText.value = next.toString()
                onValueChanged(stateValue.value)
                if (clearFocusOnButtonPress) {
                    focusManager.clearFocus()
                }
            }
        }
        buttonIncrease = @Composable {
            RepeatingIconButton(
                enabled && (overrun != null || validator.hasValidNext(stateValue, parser)),
                Icons.Default.Add
            ) {
                val next =
                    if (overrun != null && !validator.hasValidNext(stateValue, parser)) {
                        overrun.second
                    } else parser.getNextValue(stateValue.value)
                stateValue.value = next
                stateText.value = next.toString()
                onValueChanged(stateValue.value)
                if (clearFocusOnButtonPress) {
                    focusManager.clearFocus()
                }
            }
        }
    }

    val custom = true

    if (custom) {

        val isFocused = remember(focused) { mutableStateOf(focused) }
        // isFocused.value = focused

        //L.d { "[$label] isFocused = ${isFocused.value} | focused = $focused" }

        val paddingValuesTextField = OutlinedTextFieldDefaults.contentPadding(
            start = 0.dp,
            end = 0.dp,
            bottom = 4.dp
        )

        // Input in Box and Card with Custom Label and Error Texts and custom focus handling
        MyLabeledBox(
            modifier = modifier,
            modifierInner = modifierInnerPicker,
            label,
            enabled,
            validator.error.value,
            isFocused.value
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                buttonDecrease?.invoke()
                val isError = !validator.isValid()
                OutlinedNumberTextField(
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged {
                            if (allowManualInput)
                                isFocused.value = it.isFocused
                        },
                    value = stateText.value,
                    enabled = enabled && allowManualInput,
                    readOnly = !allowManualInput,
                    onValueChange = {
                        stateText.value = it
                        val value = parser.parse(it)
                        validator.check(value, true)
                        if (validator.isValid()) {
                            stateValue.value = value!!
                            onValueChanged(value)
                        } else {
                            onCurrentValueIsInvalid(it)
                        }
                    },
                    isError = isError,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    label = null,
                    placeholder = { Text(text = label) },
                    minLines = lines,
                    maxLines = lines,
                    singleLine = lines == 1,
                    textStyle = textStyle,
                    supportingText = null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                    ),
                    cursorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                    paddingValues = paddingValuesTextField
                )
                buttonIncrease?.invoke()
            }
        }
    } else {

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = modifier,
                value = stateText.value,
                enabled = enabled,
                readOnly = !allowManualInput,
                onValueChange = {
                    stateText.value = it
                    val value = parser.parse(it)
                    validator.check(value, true)
                    if (validator.isValid()) {
                        stateValue.value = value!!
                        onValueChanged(value)
                    } else {
                        onCurrentValueIsInvalid(it)
                    }
                },
                isError = !validator.isValid(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = if (label.isNotEmpty()) {
                    @Composable { Text(text = label) }
                } else null,
                placeholder = { Text(text = label) },
                minLines = lines,
                maxLines = lines,
                singleLine = lines == 1,
                textStyle = textStyle,
                supportingText = {
                    val error = validator.error.value
                    AnimatedVisibility(visible = error != null && enabled) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = error ?: "",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                //paddingValues = TextFieldDefaults.outlinedTextFieldPadding(
                //    //start = 4.dp,
                //    //end = 4.dp
                //),
                leadingIcon = buttonDecrease,
                trailingIcon = buttonIncrease
            )
        }
    }
}

@Composable
fun <T : Number> rememberMyNumberValidatorAlwaysValid(): MyNumberValidator<T> {
    return MyNumberValidator(
        error = remember { mutableStateOf(null) },
        validate = { null }
    )
}

fun <T : Number> createNumberParser(stepSize: T): MyNumberParser<T> {
    return when (stepSize) {
        is Int -> createNumberParserInt(stepSize)
        is Double -> createNumberParserDouble(stepSize)
        is Long -> createNumberParserLong(stepSize)
        is Float -> createNumberParserFloat(stepSize)
        else -> throw RuntimeException("Class ${stepSize::class.simpleName} not supported!")
    } as MyNumberParser<T>
}

private fun createNumberParserDouble(stepSize: Double = 1.0): MyNumberParser<Double> {
    return MyNumberParser(
        parse = { it.toDoubleOrNull() },
        getPrevValue = { it - stepSize },
        getNextValue = { it + stepSize }
    )
}

private fun createNumberParserInt(stepSize: Int = 1): MyNumberParser<Int> {
    return MyNumberParser(
        parse = { it.toIntOrNull() },
        getPrevValue = { it - stepSize },
        getNextValue = { it + stepSize }
    )
}

private fun createNumberParserFloat(stepSize: Float = 1f): MyNumberParser<Float> {
    return MyNumberParser(
        parse = { it.toFloatOrNull() },
        getPrevValue = { it - stepSize },
        getNextValue = { it + stepSize }
    )
}

private fun createNumberParserLong(stepSize: Long = 1): MyNumberParser<Long> {
    return MyNumberParser(
        parse = { it.toLongOrNull() },
        getPrevValue = { it - stepSize },
        getNextValue = { it + stepSize }
    )
}

@Composable
fun <T : Number> rememberMyNumberValidator(
    value: T,
    validate: (value: T?) -> String?
): MyNumberValidator<T> {
    val error = remember(value) { mutableStateOf(validate(value)) }
    return MyNumberValidator(error, validate)
}

@Composable
fun rememberMyNumberPickerIntClasses(
    value: Int,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    stepSize: Int = 1
): Pair<MyNumberValidator<Int>, MyNumberParser<Int>> {
    val stringInvalid = stringResource(Res.string.error_invalid_input)
    val stringTooLow = stringResource(Res.string.error_too_low)
    val stringTooHigh = stringResource(Res.string.error_too_high)
    val validator = rememberMyNumberValidator(value) { value ->
        if (value == null) {
            stringInvalid
        } else if (value < min) {
            stringTooLow
        } else if (value > max) {
            stringTooHigh
        } else null
    }
    val parser = createNumberParserInt(stepSize)
    return Pair(validator, parser)
}

@Composable
fun rememberMyNumberPickerIntLengthClasses(
    value: Int,
    length: Int,
    stepSize: Int
): Pair<MyNumberValidator<Int>, MyNumberParser<Int>> {
    val stringInvalid = stringResource(Res.string.error_invalid_input)
    val stringInvalidLength = stringResource(Res.string.error_invalid_length)
    val validator = rememberMyNumberValidator(value) { value ->
        if (value == null) {
            stringInvalid
        } else if (length != value.toString().length) {
            stringInvalidLength
        } else null
    }
    val parser = MyNumberParser(
        parse = { it.toIntOrNull() },
        getPrevValue = { it - stepSize },
        getNextValue = { it + stepSize }
    )
    return Pair(validator, parser)
}

@Composable
fun rememberMyNumberPickerDoubleClasses(
    value: Double,
    min: Double = Double.MIN_VALUE,
    max: Double = Double.MAX_VALUE,
    stepSize: Double = 1.0
): Pair<MyNumberValidator<Double>, MyNumberParser<Double>> {
    val stringInvalid = stringResource(Res.string.error_invalid_input)
    val stringTooLow = stringResource(Res.string.error_too_low)
    val stringTooHigh = stringResource(Res.string.error_too_high)
    val validator = rememberMyNumberValidator(value) { value ->
        if (value == null) {
            stringInvalid
        } else if (value < min) {
            stringTooLow
        } else if (value > max) {
            stringTooHigh
        } else null
    }
    val parser = createNumberParserDouble(stepSize)
    return Pair(validator, parser)
}

class MyNumberParser<T : Number>(
    val parse: (value: String) -> T?,
    val getPrevValue: (value: T) -> T,
    val getNextValue: (value: T) -> T
)

class MyNumberValidator<T : Number>(
    val error: MutableState<String?> = mutableStateOf(null),
    val validate: (value: T?) -> String? = { _ -> null }
) {
    fun isValid() = error.value == null

    fun check(value: T?, setError: Boolean): Boolean {
        val error = validate(value)
        if (setError)
            this.error.value = error
        return error == null
    }

    fun hasValidPrev(value: MutableState<T>, adjuster: MyNumberParser<T>): Boolean {
        val current = value.value
        val next = adjuster.getPrevValue(current)
        if (next == current)
            return false
        return check(next, false)
    }

    fun hasValidNext(value: MutableState<T>, adjuster: MyNumberParser<T>): Boolean {
        val current = value.value
        val next = adjuster.getNextValue(current)
        if (next == current)
            return false
        return check(next, false)
    }
}

@Composable
private fun RepeatingIconButton(
    enabled: Boolean,
    icon: ImageVector,
    initialDelayMillis: Long = 200,
    maxDelayMillis: Long = 200,
    minDelayMillis: Long = 5,
    delayDecayFactor: Float = .20f,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier.repeatingClickable(
            interactionSource = interactionSource,
            enabled = enabled,
            initialDelayMillis = initialDelayMillis,
            maxDelayMillis = maxDelayMillis,
            minDelayMillis = minDelayMillis,
            delayDecayFactor = delayDecayFactor
        ) { onClick() },
        onClick = {
            onClick()
        },
        enabled = enabled,
        interactionSource = interactionSource,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = ""
        )
    }
}

@Composable
private fun Modifier.repeatingClickable(
    interactionSource: InteractionSource,
    enabled: Boolean,
    initialDelayMillis: Long,
    maxDelayMillis: Long,
    minDelayMillis: Long,
    delayDecayFactor: Float,
    onClick: () -> Unit
): Modifier {
    val currentClickListener by rememberUpdatedState(onClick)
    return this then Modifier.pointerInput(interactionSource, enabled) {
        coroutineScope {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val heldButtonJob = launch {
                    var currentDelayMillis = maxDelayMillis
                    delay(initialDelayMillis)
                    //if (enabled && down.pressed)
                    //    currentClickListener()
                    while (enabled && down.pressed) {
                        delay(currentDelayMillis)
                        currentClickListener()
                        val nextMillis =
                            currentDelayMillis - (currentDelayMillis * delayDecayFactor)
                        currentDelayMillis = nextMillis.toLong().coerceAtLeast(minDelayMillis)
                    }
                }
                waitForUpOrCancellation()
                heldButtonJob.cancel()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OutlinedNumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    cursorColor: Color? = null,
    paddingValues: PaddingValues = OutlinedTextFieldDefaults.contentPadding()
) {
    val textColor = textStyle.color.takeOrElse {
        MaterialTheme.colorScheme.onSurface//colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    //CompositionLocalProvider(LocalTextSelectionColors provides colors.selectionColors) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = cursorColor?.let { SolidColor(it) }
            ?: SolidColor(if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground),// SolidColor(colors.cursorColor(isError).value),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = innerTextField,
            enabled = enabled,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            isError = isError,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            colors = colors,
            contentPadding = paddingValues,
            container = {
                OutlinedTextFieldDefaults.ContainerBox(enabled, isError, interactionSource, colors)
            },
        )
    }
    //}
}
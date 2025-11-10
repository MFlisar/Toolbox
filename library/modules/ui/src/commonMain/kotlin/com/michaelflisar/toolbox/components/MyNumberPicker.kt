package com.michaelflisar.toolbox.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.FocusInteraction
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
import androidx.compose.material3.OutlinedTextFieldDefaults.FocusedBorderThickness
import androidx.compose.material3.OutlinedTextFieldDefaults.UnfocusedBorderThickness
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.numbers.MyNumberParser
import com.michaelflisar.toolbox.numbers.MyNumberValidator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ------------------
// Overloads
// ------------------

@Composable
fun <T : Number> MyNumberPicker(
    value: MutableState<T>,
    validator: MyNumberValidator<T>,
    parser: MyNumberParser<T>,
    modifier: Modifier = Modifier,
    modifierInnerPicker: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    showButtons: Boolean = true,
    overrun: Pair<T, T>? = null,
    allowManualInput: Boolean = true,
    clearFocusOnButtonPress: Boolean = true,
    selectAllOnFocus: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
    focused: Boolean = false,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onCurrentValueIsInvalid: (text: String) -> Unit = {}
) {
    MyNumberPicker(
        value = value.value,
        validator = validator,
        parser = parser,
        modifier = modifier,
        modifierInnerPicker = modifierInnerPicker,
        label = label,
        enabled = enabled,
        showButtons = showButtons,
        overrun = overrun,
        allowManualInput = allowManualInput,
        clearFocusOnButtonPress = clearFocusOnButtonPress,
        selectAllOnFocus = selectAllOnFocus,
        textStyle = textStyle,
        focused = focused,
        prefix = prefix,
        suffix = suffix,
        onCurrentValueIsInvalid = onCurrentValueIsInvalid
    ) {
        value.value = it
    }
}

// ------------------
// Implementierung
// ------------------

@Composable
fun <T : Number> MyNumberPicker(
    value: T,
    validator: MyNumberValidator<T>,
    parser: MyNumberParser<T>,
    modifier: Modifier = Modifier,
    modifierInnerPicker: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    showButtons: Boolean = true,
    overrun: Pair<T, T>? = null,
    allowManualInput: Boolean = true,
    clearFocusOnButtonPress: Boolean = true,
    selectAllOnFocus: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
    focused: Boolean = false,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onCurrentValueIsInvalid: (text: String) -> Unit = {},
    onValueChanged: (T) -> Unit = {}
) {
    val value = parser.correct(value)

    val initialValue = rememberSaveable(value) { value }
    val stateValue = rememberSaveable(value) { mutableStateOf(value) }
    var stateText by rememberSaveable(value, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(parser.formatValue(value)))
    }

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
                stateText = stateText.copy(text = parser.formatValue(next))
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
                stateText = stateText.copy(text = parser.formatValue(next))
                onValueChanged(stateValue.value)
                if (clearFocusOnButtonPress) {
                    focusManager.clearFocus()
                }
            }
        }
    }

    val custom = true

    if (custom) {

        val isFocused = remember { mutableStateOf(focused) }
        // isFocused.value = focused

        //L.d { "[$label] isFocused = ${isFocused.value} | focused = $focused" }

        val paddingValuesTextField = OutlinedTextFieldDefaults.contentPadding(
            start = 0.dp,
            end = 0.dp,
            bottom = 4.dp
        )

        val isError = !validator.isValid.value
        val errorText = validator.error.value


        // Input in Box and Card with Custom Label and Error Texts and custom focus handling
        MyLabeledBox(
            modifier = modifier,
            modifierInner = modifierInnerPicker,
            label = label,
            enabled = enabled,
            error = errorText,
            focused = isFocused.value
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                buttonDecrease?.invoke()

                val mutableInteractionSource = remember { MutableInteractionSource() }

                if (selectAllOnFocus) {
                    LaunchedEffect(Unit) {
                        mutableInteractionSource.interactions
                            .collect { interaction ->
                                if (interaction is FocusInteraction.Focus) {
                                    delay(100)
                                    stateText = stateText.copy(selection = TextRange(0, stateText.text.length))
                                    //L.tag("SELECT").d { "interactionSource: text = '${stateText.text}' | selection = ${stateText.selection}" }
                                } else if (interaction is FocusInteraction.Unfocus) {
                                    stateText = stateText.copy(selection = TextRange.Zero)
                                    //L.tag("SELECT").d { "interactionSource: focus lost => selection cleared | selection = ${stateText.selection}" }
                                }
                            }
                    }
                }

                OutlinedNumberTextField(
                    interactionSource = mutableInteractionSource,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged {
                            if (allowManualInput) {
                                isFocused.value = it.isFocused
                            }
                            //if (it.isFocused && selectAllOnFocus) {
                            //    L.tag("SELECT").d { "setting selectAll to true..." }
                            //    stateText = stateText.copy(selection = TextRange(0, stateText.text.length))
                            //}
                        },
                    value = stateText,
                    enabled = enabled && allowManualInput,
                    readOnly = !allowManualInput,
                    onValueChange = {

                        if (it == stateText) {
                            // skip
                            L.tag("SELECT").d { "onValueChange skipped" }

                        } else {

                            L.tag("SELECT").d { "onValueChange: $it | stateText = $stateText" }

                            stateText = it


                            val newValue = parser.parse(it.text)?.let { parser.correct(it) }
                            val valid = validator.check(newValue, true)

                            if (valid) {
                                stateValue.value = newValue!!
                                onValueChanged(newValue)
                            } else {
                                // zurückfallen auf initial wert
                                stateValue.value = initialValue
                                onValueChanged(initialValue)
                                // fehler danach melden
                                onCurrentValueIsInvalid(it.text)
                            }
                        }
                    },
                    isError= isError,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    label = null,
                    //placeholder = { Text(text = label) },
                    minLines = lines,
                    maxLines = lines,
                    singleLine = lines == 1,
                    textStyle = textStyle,
                    prefix = prefix,
                    suffix = suffix,
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

        var selectAll by remember { mutableStateOf(false) }

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged {
                        if (it.isFocused && selectAllOnFocus) {
                            selectAll = true

                        }
                    },
                value = stateText,
                enabled = enabled && allowManualInput,
                readOnly = !allowManualInput,
                onValueChange = {
                    if (selectAll) {
                        // select all once
                        stateText = it.copy(selection = TextRange(0, it.text.length))
                        selectAll = false
                    } else {
                        stateText = it
                    }

                    val newValue = parser.parse(it.text)?.let { parser.correct(it) }
                    val valid = validator.check(newValue, true)

                    if (valid) {
                        stateValue.value = newValue!!
                        onValueChanged(newValue)
                    } else {
                        // zurückfallen auf initial wert
                        stateValue.value = initialValue
                        onValueChanged(initialValue)
                        // fehler danach melden
                        onCurrentValueIsInvalid(it.text)
                    }
                },
                isError = !validator.isValid.value,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = if (label.isNotEmpty()) {
                    @Composable { Text(text = label) }
                } else null,
                //placeholder = { Text(text = label) },
                minLines = lines,
                maxLines = lines,
                singleLine = lines == 1,
                textStyle = textStyle,
                prefix = prefix,
                suffix = suffix,
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

// ------------------
// Sub components
// ------------------

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
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
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
            value = value.text,
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
                OutlinedTextFieldDefaults.Container(
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    shape = OutlinedTextFieldDefaults.shape,
                    focusedBorderThickness = FocusedBorderThickness,
                    unfocusedBorderThickness = UnfocusedBorderThickness,
                )
            },
        )
    }
    //}
}
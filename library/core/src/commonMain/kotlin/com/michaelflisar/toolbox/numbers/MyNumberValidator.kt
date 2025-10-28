package com.michaelflisar.toolbox.numbers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.error_invalid_input
import com.michaelflisar.toolbox.core.resources.error_invalid_length
import com.michaelflisar.toolbox.core.resources.error_too_high
import com.michaelflisar.toolbox.core.resources.error_too_low
import org.jetbrains.compose.resources.stringResource

class MyNumberValidator<T : Number>(
    val error: MutableState<String?> = mutableStateOf(null),
    val validate: (value: T?) -> String? = { _ -> null }
) {
    val isValid = derivedStateOf { error.value == null }

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
fun <T : Number> rememberMyNumberValidatorAlwaysValid(): MyNumberValidator<T> {
    return MyNumberValidator(
        error = remember { mutableStateOf(null) },
        validate = { null }
    )
}

@Composable
fun <T : Number> rememberMyNumberValidator(
    value: T,
    validate: (value: T?) -> String?
): MyNumberValidator<T> {
    val error = remember { mutableStateOf(validate(value)) }
    return MyNumberValidator(error, validate)
}

@Composable
fun <T : Number> rememberMyNumberValidator(
    value: T,
    min: T = NumberUtil.min(value),
    max: T = NumberUtil.max(value)
): MyNumberValidator<T> {
    val stringInvalid = stringResource(Res.string.error_invalid_input)
    val stringTooLow = stringResource(Res.string.error_too_low)
    val stringTooHigh = stringResource(Res.string.error_too_high)
    val validator = rememberMyNumberValidator(value) { value ->
        if (value == null) {
            stringInvalid
        } else if (NumberUtil.lower(value, min)) {
            stringTooLow
        } else if (NumberUtil.higher(value, max)) {
            stringTooHigh
        } else null
    }

    return validator
}

@Composable
fun <T : Number> rememberMyNumberValidator(
    value: T,
    length: Int
): MyNumberValidator<T> {
    val stringInvalid = stringResource(Res.string.error_invalid_input)
    val stringInvalidLength = stringResource(Res.string.error_invalid_length)
    val validator = rememberMyNumberValidator(value) { value ->
        if (value == null) {
            stringInvalid
        } else if (length != value.toString().length) {
            stringInvalidLength
        } else null
    }
    return validator
}
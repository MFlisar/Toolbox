package com.michaelflisar.toolbox.form2

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.components.MyNumberPicker
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.numbers.NumberUtil
import com.michaelflisar.toolbox.numbers.rememberMyNumberParser
import com.michaelflisar.toolbox.numbers.rememberMyNumberValidator

@Composable
fun <T : Number> FormNumberInput(
    title: String,
    value: T,
    modifier: Modifier = Modifier.fillMaxWidth(),
    min: T = NumberUtil.min(value),
    max: T = NumberUtil.max(value),
    stepSize: T = NumberUtil.one(value),
    commas: Int? = null,
    selectAllOnFocus: Boolean = false,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onValueChanged: (value: T) -> Unit,
) {
    val value = remember { mutableStateOf(value) }
    LaunchedEffect(Unit) {
        snapshotFlow { value.value }.collect { onValueChanged(it) }
    }
    FormNumberInput(
        title = title,
        value = value,
        modifier = modifier,
        min = min,
        max = max,
        stepSize = stepSize,
        commas = commas,
        selectAllOnFocus = selectAllOnFocus,
        prefix = prefix,
        suffix = suffix
    )
}

@Composable
fun <T : Number> FormNumberInput(
    title: String,
    value: MutableState<T>,
    modifier: Modifier = Modifier.fillMaxWidth(),
    min: T = NumberUtil.min(value.value),
    max: T = NumberUtil.max(value.value),
    stepSize: T = NumberUtil.one(value.value),
    commas: Int? = NumberUtil.defaultCommas(value.value),
    selectAllOnFocus: Boolean = false,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
) {
    MyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val validator = rememberMyNumberValidator(
            value = value.value,
            min = min,
            max = max
        )
        val parser = rememberMyNumberParser(instance = value.value, stepSize = stepSize, commas = commas)
        MyNumberPicker(
            modifier = Modifier.weight(1f),
            validator = validator,
            parser = parser,
            label = title,
            value = value,
            selectAllOnFocus = selectAllOnFocus,
            prefix = prefix,
            suffix = suffix
        )
    }
}

/*
@PreviewCurrent
@Composable
fun PreviewFormNumberInput() {
    IronPreviewTheme {
        val selectedGoalValue = remember { mutableIntStateOf(3) }
        FormContainer {
            FormNumberInput(
                title = "Sätze",
                min = 1,
                max = 100,
                value = selectedGoalValue
            )
        }
    }
}*/
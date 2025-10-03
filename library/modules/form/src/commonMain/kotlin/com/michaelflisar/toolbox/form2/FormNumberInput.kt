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
import com.michaelflisar.toolbox.components.rememberMyNumberPickerIntClasses

@Composable
fun FormScope.FormNumberInput(
    title: String,
    min: Int,
    max: Int,
    value: Int,
    stepSize: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onValueChanged: (value: Int) -> Unit,
) {
    val value = remember { mutableStateOf(value) }
    LaunchedEffect(Unit) {
        snapshotFlow { value.value }.collect { onValueChanged(it) }
    }
    FormNumberInput(title, min, max, stepSize, value, modifier)
}

@Composable
fun FormScope.FormNumberInput(
    title: String,
    min: Int,
    max: Int,
    stepSize: Int,
    value: MutableState<Int>,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    MyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (validator1, parser1) = rememberMyNumberPickerIntClasses(
            value.value,
            min,
            max,
            stepSize
        )
        MyNumberPicker(
            modifier = Modifier.weight(1f),
            //modifierInnerPicker = Modifier.fillMaxWidth(),
            validator = validator1,
            parser = parser1,
            label = title,
            value = value
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
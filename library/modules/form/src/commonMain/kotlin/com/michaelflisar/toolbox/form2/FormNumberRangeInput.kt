package com.michaelflisar.toolbox.form2

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.components.MyNumberPicker
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.from
import com.michaelflisar.toolbox.core.resources.to
import com.michaelflisar.toolbox.numbers.rememberMyNumberParser
import com.michaelflisar.toolbox.numbers.rememberMyNumberValidator
import org.jetbrains.compose.resources.stringResource

@Composable
fun FormNumberRangeInput(
    valueFrom: Int,
    valueTo: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    step: Int = 1,
    title: String? = null,
    titleFrom: String = stringResource(Res.string.from),
    titleTo: String = stringResource(Res.string.to),
    onValueChanged: (from: Int, to: Int) -> Unit,
) {
    val valueFrom = remember { mutableIntStateOf(valueFrom) }
    val valueTo = remember { mutableIntStateOf(valueTo) }
    LaunchedEffect(Unit) {
        snapshotFlow { valueFrom.intValue }.collect { onValueChanged(it, valueTo.intValue) }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { valueTo.intValue }.collect { onValueChanged(valueFrom.intValue, it) }
    }
    FormNumberRangeInput(valueFrom, valueTo, modifier, min, max, step, title, titleFrom, titleTo)
}

@Composable
fun FormNumberRangeInput(
    valueFrom: MutableIntState,
    valueTo: MutableIntState,
    modifier: Modifier = Modifier.fillMaxWidth(),
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    step: Int = 1,
    title: String? = null,
    titleFrom: String = stringResource(Res.string.from),
    titleTo: String = stringResource(Res.string.to),
) {
    MyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val validator1 = rememberMyNumberValidator(
            value = valueFrom.intValue,
            min = min,
            max = valueTo.intValue - 1
        )
        val parser1 = rememberMyNumberParser(valueFrom.intValue, step, 0)

        val validator2 = rememberMyNumberValidator(
            value = valueTo.intValue,
            min = valueFrom.intValue + 1,
            max = max
        )
        val parser2 = rememberMyNumberParser(valueTo.intValue, step, 0)

        if (title != null) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
        }
        MyNumberPicker(
            modifier = Modifier.weight(2f),
            validator = validator1,
            parser = parser1,
            label = titleFrom,
            value = valueFrom
        )
        MyNumberPicker(
            modifier = Modifier.weight(2f),
            validator = validator2,
            parser = parser2,
            label = titleTo,
            value = valueTo
        )
    }
}

/*
@PreviewCurrent
@Composable
fun PreviewFormNumberRangeInput() {
    IronPreviewTheme {
        val selectedGoalValueFrom = remember { mutableIntStateOf(3) }
        val selectedGoalValueTo = remember { mutableIntStateOf(4) }
        FormContainer {
            FormNumberRangeInput(
                min = 1,
                max = 100,
                valueFrom = selectedGoalValueFrom.intValue,
                valueTo = selectedGoalValueTo.intValue
            ) { from, to ->
                selectedGoalValueFrom.intValue = from
                selectedGoalValueTo.intValue = to
                // TODO: persist...
            }
        }
    }
}*/
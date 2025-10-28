package com.michaelflisar.toolbox.numbers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class MyNumberParser<T : Number>(
    val parse: (value: String) -> T?,
    val correct: (value: T) -> T,
    val format: ((value: T) -> String)?,
    val getPrevValue: (value: T) -> T,
    val getNextValue: (value: T) -> T
) {
    fun formatValue(value: T): String {
        return format?.invoke(value) ?: value.toString()
    }
}

@Composable
fun <T : Number> rememberMyNumberParser(instance: T, stepSize: T, commas: Int? = NumberUtil.defaultCommas(instance)): MyNumberParser<T> {
    return remember {
        MyNumberParser(
            parse = { NumberUtil.parse(instance, it, commas) },
            correct = { if (commas != null) NumberUtil.round(it, commas) else it },
            format = { if (commas != null) NumberUtil.roundToString(it, commas) else it.toString() },
            getPrevValue = { NumberUtil.minus(it, stepSize) },
            getNextValue = { NumberUtil.plus(it, stepSize) }
        )
    }
}

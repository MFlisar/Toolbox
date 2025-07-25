package com.michaelflisar.toolbox.extensions

import com.michaelflisar.toolbox.utils.ByteUtil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.text.padStart
import kotlin.text.trimEnd

fun Double.toRoundedString(commas: Int): String {
    require(commas >= 0) { "decimalPlaces must be non-negative" }

    val scale = 10.0.pow(commas.toDouble()).toLong()
    val scaledNumber = (this * scale).roundToLong()
    val integerPart = scaledNumber / scale
    val fractionalPart = scaledNumber % scale

    return if (fractionalPart == 0L) {
        integerPart.toString()
    } else {
        val fractionalPartString = fractionalPart.toString().padStart(commas, '0')
        "$integerPart.${fractionalPartString.trimEnd('0')}"
    }
}

fun Number.prettyNumber(commas: Int): String {
    val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val numValue = this.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value < 3) {
        // Zahl < 1000
        toDouble().toRoundedString(commas)
    } else if (base < suffix.size) {
        // Zahl > 1000 && klein genug für definierte suffixes
        (numValue / 10.0.pow((base * 3).toDouble())).toRoundedString(commas) + suffix[base]
    } else {
        // Zahl größer als die definierten suffixes
        toDouble().toRoundedString(commas)
    }
}

fun Long.toHumanReadableBinary(commas: Int = 1) = ByteUtil.toHumanReadableBinaryPrefixes(this, commas)
fun Long.toHumanReadableSI(commas: Int = 1) = ByteUtil.toHumanReadableSIPrefixes(this, commas)

fun Float.roundToRange(from: Float, to: Float, epsilon: Float = 1e-6f): Float {
    return when {
        this < (from + epsilon) -> from
        this > (to - epsilon) -> 1f
        else -> this
    }
}

fun Float.sameSign(other: Float): Boolean {
    return this < 0 && other < 0 || this >= 0 && other >= 0
}
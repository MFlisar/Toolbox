package com.michaelflisar.toolbox.numbers

import com.michaelflisar.toolbox.extensions.toRoundedString

object NumberUtil {

    private val DECIMAL_SEPARATORS = listOf('.', ',')

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> zero(): T {
        return when (T::class) {
            Int::class -> 0 as T
            Double::class -> 0.0 as T
            Float::class -> 0f as T
            Long::class -> 0L as T
            Short::class -> 0.toShort() as T
            Byte::class -> 0.toByte() as T
            else -> throw RuntimeException("Type not handled!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> zero(value: T): T {
        return when (value::class) {
            Int::class -> 0 as T
            Double::class -> 0.0 as T
            Float::class -> 0f as T
            Long::class -> 0L as T
            Short::class -> 0.toShort() as T
            Byte::class -> 0.toByte() as T
            else -> throw RuntimeException("Type not handled!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> one(value: T): T {
        return when (value::class) {
            Int::class -> 1 as T
            Double::class -> 1.0 as T
            Float::class -> 1f as T
            Long::class -> 1L as T
            Short::class -> 1.toShort() as T
            Byte::class -> 1.toByte() as T
            else -> throw RuntimeException("Type not handled!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> min(value: T): T {
        return when (value::class) {
            Int::class -> Int.MIN_VALUE as T
            Double::class -> Double.MIN_VALUE as T
            Float::class -> Float.MIN_VALUE as T
            Long::class -> Long.MIN_VALUE as T
            Short::class -> Short.MIN_VALUE as T
            Byte::class -> Byte.MIN_VALUE as T
            else -> throw RuntimeException("Type not handled!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> max(value: T): T {
        return when (value::class) {
            Int::class -> Int.MAX_VALUE as T
            Double::class -> Double.MAX_VALUE as T
            Float::class -> Float.MAX_VALUE as T
            Long::class -> Long.MAX_VALUE as T
            Short::class -> Short.MAX_VALUE as T
            Byte::class -> Byte.MAX_VALUE as T
            else -> throw RuntimeException("Type not handled!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> lower(value1: T, value2: T): Boolean {
        return when (value1::class) {
            Int::class -> (value1 as Int) < (value2 as Int)
            Double::class -> (value1 as Double) < (value2 as Double)
            Float::class -> (value1 as Float) < (value2 as Float)
            Long::class -> (value1 as Long) < (value2 as Long)
            Short::class -> (value1 as Short) < (value2 as Short)
            Byte::class -> (value1 as Byte) < (value2 as Byte)
            else -> throw RuntimeException("Type not handled!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> higher(value1: T, value2: T): Boolean {
        return when (value1::class) {
            Int::class -> (value1 as Int) > (value2 as Int)
            Double::class -> (value1 as Double) > (value2 as Double)
            Float::class -> (value1 as Float) > (value2 as Float)
            Long::class -> (value1 as Long) > (value2 as Long)
            Short::class -> (value1 as Short) > (value2 as Short)
            Byte::class -> (value1 as Byte) > (value2 as Byte)
            else -> throw RuntimeException("Type not handled!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> plus(value1: T, value2: T): T {
        return when (value1::class) {
            Int::class -> (value1 as Int) + (value2 as Int)
            Double::class -> (value1 as Double) + (value2 as Double)
            Float::class -> (value1 as Float) + (value2 as Float)
            Long::class -> (value1 as Long) + (value2 as Long)
            Short::class -> (value1 as Short) + (value2 as Short)
            Byte::class -> (value1 as Byte) + (value2 as Byte)
            else -> throw RuntimeException("Type not handled!")
        } as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> minus(value1: T, value2: T): T {
        return when (value1::class) {
            Int::class -> (value1 as Int) - (value2 as Int)
            Double::class -> (value1 as Double) - (value2 as Double)
            Float::class -> (value1 as Float) - (value2 as Float)
            Long::class -> (value1 as Long) - (value2 as Long)
            Short::class -> (value1 as Short) - (value2 as Short)
            Byte::class -> (value1 as Byte) - (value2 as Byte)
            else -> throw RuntimeException("Type not handled!")
        } as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> parse(instance: T, value: String, commas: Int? = null): T? {
        val cleaned = value.replace(',', '.')
        return when (instance) {
            is Int -> cleaned.toIntOrNull()
            is Double -> cleaned.toDoubleOrNull()
                ?.let { if (commas != null) it.toRoundedString(commas).toDouble() else it }

            is Float -> cleaned.toFloatOrNull()
            is Long -> cleaned.toLongOrNull()
            is Short -> cleaned.toShortOrNull()
            is Byte -> cleaned.toByteOrNull()
            else -> throw RuntimeException("Type not handled!")
        } as T?
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> round(value: T, commas: Int): T {
        return when (value) {
            is Int -> value
            is Double -> value.toRoundedString(commas).toDouble()
            is Float -> value.toRoundedString(commas).toFloat()
            is Long -> value
            is Short -> value
            is Byte -> value
            else -> throw RuntimeException("Type not handled!")
        } as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> roundToString(value: T, commas: Int): String {
        return when (value) {
            is Int -> value.toString()
            is Double -> value.toRoundedString(commas)
            is Float -> value.toRoundedString(commas)
            is Long -> value.toString()
            is Short -> value.toString()
            is Byte -> value.toString()
            else -> throw RuntimeException("Type not handled!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> defaultCommas(value: T): Int {
        return when (value::class) {
            Double::class,
            Float::class -> 2

            else -> 0
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> supportsDecimal(value: T): Boolean {
        return when (value::class) {
            Double::class,
            Float::class -> true

            else -> false
        }
    }

    fun clearText(text: String, supportsDecimal: Boolean): String {
        return text
            .filter { c -> c.isDigit() || (supportsDecimal && c in DECIMAL_SEPARATORS) }
            .let { str ->
                // Nur das erste Vorkommen eines Dezimaltrennzeichens behalten
                if (supportsDecimal) {
                    val firstSeparatorIndex = str.indexOfFirst { it in DECIMAL_SEPARATORS }
                    if (firstSeparatorIndex != -1) {
                        val sep = str[firstSeparatorIndex]
                        str.substring(0, firstSeparatorIndex + 1) +
                                str.substring(firstSeparatorIndex + 1)
                                    .filter { it != sep && it !in DECIMAL_SEPARATORS }
                    } else str
                } else str
            }
    }
}
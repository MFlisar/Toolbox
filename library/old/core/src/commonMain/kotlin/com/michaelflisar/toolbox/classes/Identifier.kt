package com.michaelflisar.toolbox.classes

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class Identifier<T : Any>(val value: String) : Comparable<Identifier<T>> {
    override fun compareTo(other: Identifier<T>): Int = value.compareTo(other.value)
    override fun toString(): String {
        return value
    }
}
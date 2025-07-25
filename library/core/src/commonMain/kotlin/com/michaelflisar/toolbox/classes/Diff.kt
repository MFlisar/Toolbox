package com.michaelflisar.toolbox.classes

class Diff<T>(
    private val object1: T?,
    private val object2: T?
) {
    fun <R> changed(vararg memberAccess: T.() -> R): Boolean {
        if (object1 == null && object2 == null) {
            return false
        }
        if (object1 == null || object2 == null) {
            return true
        }
        for (ma in memberAccess) {
            if (changed(ma))
                return true
        }
        return false
    }

    fun <R> changed(memberAccess: T.() -> R): Boolean {
        if (object1 == null && object2 == null) {
            return false
        }
        if (object1 == null || object2 == null) {
            return true
        }
        val currentValue = object1.memberAccess()
        val previousValue = object2.memberAccess()
        return currentValue != previousValue
    }
}
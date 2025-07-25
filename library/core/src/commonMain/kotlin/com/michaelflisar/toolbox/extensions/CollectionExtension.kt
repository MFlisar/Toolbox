package com.michaelflisar.toolbox.extensions

fun <T> List<T>.toggle(item: T): MutableList<T> {
    return toMutableList().also {
        if (this.contains(item)) {
            it.remove(item)
        } else {
            it.add(item)
        }
    }
}

fun <T> Set<T>.toggle(item: T, forceSingle: Boolean = false): MutableSet<T> {
    return toMutableSet().also {
        if (this.contains(item)) {
            it.remove(item)
            if (forceSingle) {
                it.clear()
            }
        } else {
            if (forceSingle) {
                it.clear()
            }
            it.add(item)
        }
    }
}
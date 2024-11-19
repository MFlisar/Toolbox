package com.michaelflisar.toolbox

// TODO: HACK to avoid java.lang.NoSuchMethodError: No virtual method removeLast()Ljava/lang/Object; in class Landroidx/compose/runtime/snapshots/SnapshotStateList; or its super classes
// issue only happens on android!
// this is a target API 35 problem that makes issues on all android devices using API <= 34

fun <T> MutableList<T>.removeLastSave() : T {
    return removeAt(size - 1)
}

fun <T> MutableList<T>.removeFirstSave() : T {
    return removeAt(0)
}
package com.michaelflisar.toolbox

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

// TODO: HACK to avoid java.lang.NoSuchMethodError: No virtual method removeLast()Ljava/lang/Object; in class Landroidx/compose/runtime/snapshots/SnapshotStateList; or its super classes
// issue only happens on android!
// this is a target API 35 problem that makes issues on all android devices using API <= 34

fun <T> MutableList<T>.removeLastSave(): T {
    return removeAt(size - 1)
}

fun <T> MutableList<T>.removeFirstSave(): T {
    return removeAt(0)
}

@Composable
fun Int.toIconComposable(tint: Color = Color.Unspecified): IconComposable {
    return this.let {
        @Composable { contentDescription, modifier, tint2 ->
            Icon(
                painterResource(this),
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint.takeIf { it != Color.Unspecified } ?: tint2
            )
        }
    }
}
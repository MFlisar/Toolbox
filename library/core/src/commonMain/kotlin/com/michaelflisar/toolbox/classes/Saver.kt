package com.michaelflisar.toolbox.classes

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap

fun <T> saverMutableStateList(): Saver<MutableState<List<T>>, Any> {
    return listSaverKeepEmpty(
        save = { it.value },
        restore = { mutableStateOf(it) }
    )
}

fun <T> saverMutableStateNullaleList(): Saver<MutableState<List<T>?>, Any> {
    return listSaverKeepEmptyNullable(
        save = { it.value },
        restore = { mutableStateOf(it) }
    )
}

fun <T> saverMutableStateSet(): Saver<MutableState<Set<T>>, Any> {
    return listSaverKeepEmpty(
        save = { it.value.toList() },
        restore = { mutableStateOf(it.toSet()) }
    )
}

fun <T> saverSnapshotStateList(): Saver<SnapshotStateList<T>, Any> {
    return listSaverKeepEmpty(
        save = { it.toList() },
        restore = { it.toMutableStateList<T>() }
    )
}

fun <K : Any, T : Any> saverSnapshotStateMap(): Saver<SnapshotStateMap<K, T>, Any> {
    return listSaverKeepEmpty(
        save = { it.toList() },
        restore = { (it as? List<Pair<K, T>>)?.toMutableStateMap() ?: mutableStateMapOf<K, T>() }
    )
}

// ----------------------------
// private functions
// ----------------------------

@Suppress("UNCHECKED_CAST")
private fun <Original, Saveable> listSaverKeepEmpty(
    save: SaverScope.(value: Original) -> List<Saveable>,
    restore: (list: List<Saveable>) -> Original?,
): Saver<Original, Any> = Saver(
    save = {
        val list = save(it)
        for (index in list.indices) {
            val item = list[index]
            if (item != null) {
                require(canBeSaved(item)) { "item can't be saved" }
            }
        }
        ArrayList(list)
    },
    restore = restore as (Any) -> Original?
)

@Suppress("UNCHECKED_CAST")
private fun <Original : Any, Saveable> listSaverKeepEmptyNullable(
    save: SaverScope.(value: Original) -> List<Saveable>?,
    restore: (list: List<Saveable>?) -> Original?,
): Saver<Original, Any> = Saver(
    save = {
        val list = save(it)
        if (list == null)
            null
        else {
            for (index in list.indices) {
                val item = list[index]
                if (item != null) {
                    require(canBeSaved(item)) { "item can't be saved" }
                }
            }
            ArrayList(list)
        }
    },
    restore = restore as (Any?) -> Original?
)
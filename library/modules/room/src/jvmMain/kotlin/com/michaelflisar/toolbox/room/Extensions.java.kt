package com.michaelflisar.toolbox.room

import androidx.room.Room
import androidx.room.RoomDatabase
import com.michaelflisar.toolbox.utils.JvmFolderUtil
import java.io.File

actual inline fun <reified DB : RoomDatabase> RoomUtil.createDatabaseBuilder(
    databasePath: String,
    noinline apply: RoomDatabase.Builder<DB>.() -> Unit
): RoomDatabase.Builder<DB> {
    val folder: String = JvmFolderUtil.getApplicationPath()
    val dbFile = File(folder, databasePath)
    return Room.databaseBuilder<DB>(
        name = dbFile.absolutePath,
    ).apply(apply)
}

inline fun <reified DB : RoomDatabase> RoomUtil.createDatabaseBuilder(
    folder: String,
    databasePath: String,
    noinline apply: RoomDatabase.Builder<DB>.() -> Unit
): RoomDatabase.Builder<DB> {
    val dbFile = File(folder, databasePath)
    return Room.databaseBuilder<DB>(
        name = dbFile.absolutePath,
    ).apply(apply)
}
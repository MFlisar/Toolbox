package com.michaelflisar.toolbox.room

import androidx.room.Room
import androidx.room.RoomDatabase
import com.michaelflisar.toolbox.AppContext
import kotlin.apply

actual inline fun <reified DB : RoomDatabase> RoomUtil.createDatabaseBuilder(
    databasePath: String,
    noinline apply: RoomDatabase.Builder<DB>.() -> Unit,
): RoomDatabase.Builder<DB> {
    val appContext = AppContext.context()
    val dbFile = appContext.getDatabasePath(databasePath)
    return Room.databaseBuilder<DB>(
        context = appContext,
        name = dbFile.absolutePath
    ).apply(apply)
}
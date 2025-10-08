package com.michaelflisar.toolbox.room

import androidx.room.Room
import androidx.room.RoomDatabase
import com.michaelflisar.toolbox.AppContext
import kotlin.apply

inline fun <reified DB : RoomDatabase> RoomUtil.createDatabaseBuilder(
    fileName: String = DEFAULT_DB_FILE,
    noinline apply: RoomDatabase.Builder<DB>.() -> Unit = {}
): RoomDatabase.Builder<DB> {
    val appContext = AppContext.context()
    val dbFile = appContext.getDatabasePath(fileName)
    return Room.databaseBuilder<DB>(
        context = appContext,
        name = dbFile.absolutePath
    ).apply(apply)
}
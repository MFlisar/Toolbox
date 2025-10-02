package com.michaelflisar.toolbox.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.util.findAndInstantiateDatabaseImpl
import androidx.sqlite.db.SupportSQLiteDatabase

fun SQLLiteVersion.Companion.load(dao: SupportSQLiteDatabase): SQLLiteVersion {
    val cursor = dao.query("SELECT sqlite_version()")
    cursor.moveToFirst()
    val version = cursor.getString(0) // z.B. "3.28.0"
    cursor.close()
    return create(version)
}

inline fun <reified DB: RoomDatabase> RoomUtil.createDatabaseBuilder(
    context: Context,
    databasePath: String = "data.db",
    noinline apply: RoomDatabase.Builder<DB>.() -> Unit = { },
): RoomDatabase.Builder<DB> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(databasePath)
    return Room.databaseBuilder<DB>(
        context = appContext,
        name = dbFile.absolutePath
    ).apply(apply)
}
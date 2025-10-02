package com.michaelflisar.toolbox.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

inline fun <reified DB: RoomDatabase> RoomUtil.createDatabaseBuilder(databasePath: String = "data.db") : RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = documentDirectory() + "/" + databasePath
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
    )
}

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
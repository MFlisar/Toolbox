package com.michaelflisar.toolbox.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

actual inline fun <reified DB: RoomDatabase> RoomUtil.createDatabaseBuilder(
    fileName: String,
    noinline apply: RoomDatabase.Builder<DB>.() -> Unit
) : RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = documentDirectory() + "/" + fileName
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
    ).apply(apply)
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
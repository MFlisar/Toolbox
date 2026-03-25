package com.michaelflisar.toolbox.room

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
inline fun <reified DB : RoomDatabase> RoomUtil.createDatabaseBuilder(
    fileName: String = DEFAULT_DB_FILE,
    noinline apply: RoomDatabase.Builder<DB>.() -> Unit = {},
): RoomDatabase.Builder<DB> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    val docPath = requireNotNull(documentDirectory?.path)
    val dbFilePath = "$docPath/$fileName"
    return Room.databaseBuilder<DB>(
        name = dbFilePath,
    ).apply(apply)
}
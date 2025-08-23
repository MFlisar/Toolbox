package com.michaelflisar.toolbox.room

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SQLLiteVersionDao {
    @Query("SELECT sqlite_version()")
    suspend fun getSQLiteVersion(): String
}
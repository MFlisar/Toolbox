package com.michaelflisar.toolbox.room

import androidx.sqlite.db.SupportSQLiteDatabase

fun SQLLiteVersion.Companion.load(dao: SupportSQLiteDatabase): SQLLiteVersion {
    val cursor = dao.query("SELECT sqlite_version()")
    cursor.moveToFirst()
    val version = cursor.getString(0) // z.B. "3.28.0"
    cursor.close()
    return create(version)
}
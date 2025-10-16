package com.michaelflisar.toolbox.room

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

fun SQLiteConnection.dropTable(name: String) =
    execSQL("DROP TABLE IF EXISTS $name")

fun SQLiteConnection.createTable(
    name: String,
    createSqls: (tableName: String) -> List<String>,
) {
    createSqls(name).forEach {
        execSQL(it)
    }
}

fun SQLiteConnection.dropColumn(
    tableName: String,
    columnName: String
) {
    execSQL("ALTER TABLE $tableName DROP COLUMN $columnName")
}
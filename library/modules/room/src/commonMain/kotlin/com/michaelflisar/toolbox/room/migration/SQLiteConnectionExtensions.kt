package com.michaelflisar.toolbox.room.migration

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
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
    columnName: String,
) {
    execSQL("ALTER TABLE $tableName DROP COLUMN $columnName")
}

fun SQLiteConnection.dropIndex(
    indexName: String,
) {
    execSQL("DROP INDEX IF EXISTS $indexName")
}

fun SQLiteConnection.addColumn(
    tableName: String,
    columnName: String,
    columnDefinition: String
) {
    execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnDefinition")
}

/**
 * requires SQLite 3.25.0 (Android API 29)
 */
fun SQLiteConnection.renameColumn(
    tableName: String,
    oldName: String,
    newName: String
) {
    execSQL("ALTER TABLE $tableName RENAME COLUMN $oldName TO $newName")
}

/**
 * requires SQLite 3.25.0 (Android API 29)
 */
fun <ID, DataOld, DataNew> SQLiteConnection.convertColumn(
    tableName: String,
    columnNameId: String,
    columnNameData: String,
    newColumnDefinition: String,
    readId: (statement: SQLiteStatement, index: Int) -> ID,
    readValue: (statement: SQLiteStatement, index: Int) -> DataOld?,
    convertData: (oldData: DataOld?) -> DataNew?,
    tempColumnName: String = "${columnNameData}_temp",
) {
    addColumn(tableName, tempColumnName, newColumnDefinition)

    val values = mutableListOf<Pair<ID, DataNew?>>()
    val statement = prepare("SELECT $columnNameId, $columnNameData FROM $tableName")
    while (statement.step()) {
        val id = readId(statement, 0)
        val value = readValue(statement, 1)
        val valueNew = convertData(value)
        values.add(id to valueNew)
    }
    statement.close()

    values.forEach { (id, newValue) ->
        execSQL("UPDATE $tableName SET $tempColumnName = $newValue WHERE $columnNameId = $id")
    }

    dropColumn(tableName, columnNameData)
    renameColumn(tableName, tempColumnName, columnNameData)
}
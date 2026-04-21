package com.michaelflisar.toolbox.room

import androidx.room.RoomRawQuery
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement

private fun String.asRawQuery(): RoomRawQuery = RoomRawQuery(this)

object RoomQueryUtil {

    // -------------------
    // string query
    // -------------------

    inline fun <reified T> executeQuery(connection: SQLiteConnection, query: String): T {
        val statement = connection.prepare(query)
        try {
            check(statement.step()) { "$query returned no row!" }
            return when (T::class) {
                String::class -> statement.getText(0) as T
                Int::class -> statement.getInt(0) as T
                Double::class -> statement.getDouble(0) as T
                Float::class -> statement.getFloat(0) as T
                Long::class -> statement.getLong(0) as T
                Boolean::class -> statement.getBoolean(0) as T
                else -> throw IllegalArgumentException("Unsupported type ${T::class}")
            }
        } finally {
            statement.close()
        }
    }

    // -------------------
    // count
    // -------------------

    fun count(tableName: String) = "SELECT count(*) FROM $tableName".asRawQuery()

    fun <T> select(
        connection: SQLiteConnection,
        query: String,
        mapper: (statement: SQLiteStatement) -> T,
    ): List<T> {
        val statement = connection.prepare(query)
        val items = mutableListOf<T>()
        while (statement.step()) {
            items.add(mapper(statement))
        }
        statement.close()
        return items
    }

    // -------------------
    // load
    // -------------------

    fun loadAll(tableName: String, sort: String? = null) = if (sort != null) {
        "SELECT * FROM $tableName ORDER BY $sort"
    } else {
        "SELECT * FROM $tableName"
    }.asRawQuery()


    fun <ID : Number> load(tableName: String, columnId: String, id: ID) =
        "SELECT * FROM $tableName WHERE $columnId = $id".asRawQuery()

    fun <ID : Number> loadAllIn(tableName: String, columnId: String, ids: List<ID>) =
        "SELECT * FROM $tableName WHERE $columnId IN (${ids.joinToString(",")})".asRawQuery()

    // -------------------
    // delete - raw queries können nur lesen!
    // -------------------

    fun <ID : Number> delete(tableName: String, columnId: String, id: ID) =
        "DELETE FROM $tableName WHERE $columnId = $id".asRawQuery()

    fun <ID : Number> deleteAllIn(tableName: String, columnId: String, ids: List<ID>) =
        "DELETE FROM $tableName WHERE $columnId IN (${ids.joinToString(",")})".asRawQuery()

    fun deleteAll(tableName: String) =
        "DELETE FROM $tableName".asRawQuery()
}
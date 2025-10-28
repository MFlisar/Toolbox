package com.michaelflisar.toolbox.room

import androidx.room.RoomRawQuery

private fun String.asRawQuery(): RoomRawQuery = RoomRawQuery(this)

object RoomQueryUtil {

    // -------------------
    // count
    // -------------------

    fun count(tableName: String) = "SELECT count(*) FROM $tableName".asRawQuery()

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
    // delete
    // -------------------

    fun <ID : Number> delete(tableName: String, columnId: String, id: ID) =
        "DELETE FROM $tableName WHERE $columnId = $id".asRawQuery()

    fun <ID : Number> deleteAllIn(tableName: String, columnId: String, ids: List<ID>) =
        "DELETE FROM $tableName WHERE $columnId IN (${ids.joinToString(",")})".asRawQuery()
}
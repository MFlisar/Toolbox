package com.michaelflisar.toolbox.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import com.michaelflisar.toolbox.room.RoomQueryUtil
import com.michaelflisar.toolbox.room.RoomUtil

@Dao
abstract class BaseDaoCrossRef<ID : Number, T : Any>(
    val tableName: String,
    val columnLeftId: String,
    val columnRightId: String,
) {

    private fun queryLeft(id: ID) =
        RoomRawQuery("SELECT * FROM $tableName where $columnLeftId = $id")

    private fun queryRight(id: ID) =
        RoomRawQuery("SELECT * FROM $tableName where $columnRightId = $id")

    private fun queryDelete(left: ID, right: ID) =
        RoomRawQuery("DELETE FROM $tableName where $columnLeftId = $left and $columnRightId = $right")

    private fun queryDeleteLeft(ids: List<ID>) =
        RoomRawQuery("DELETE FROM $tableName where $columnLeftId IN (${ids.joinToString(",")})")

    private fun queryDeleteRight(ids: List<ID>) =
        RoomRawQuery("DELETE FROM $tableName where $columnRightId IN (${ids.joinToString(",")})")

    private fun queryAllLeft(ids: List<ID>) =
        RoomRawQuery("SELECT * FROM $tableName where $columnLeftId IN (${ids.joinToString(",")})")

    private fun queryAllRight(ids: List<ID>) =
        RoomRawQuery("SELECT * FROM $tableName where $columnRightId IN (${ids.joinToString(",")})")


    // -----------------
    // GET
    // -----------------

    suspend fun getAllLeft(id: ID) = _rawQueryList(queryLeft(id))

    suspend fun getAllRight(id: ID) = _rawQueryList(queryRight(id))

    suspend fun getAllLeft(ids: List<ID>): List<T> {
        val items = ArrayList<T>()
        RoomUtil.runQueryInChunks(ids) {
            items += _rawQueryList(queryAllLeft(it))
            it
        }
        return items
    }

    suspend fun getAllRight(ids: List<ID>): List<T> {
        val items = ArrayList<T>()
        RoomUtil.runQueryInChunks(ids) {
            items += _rawQueryList(queryAllRight(it))
            it
        }
        return items
    }

    // -------------------------
    // delete / insertOrUpdate
    // -------------------------

    suspend fun _deleteLeftIds(ids: List<ID>) = _rawQueryListIDs(queryDeleteLeft(ids))

    @Transaction
    open suspend fun deleteLeftIds(ids: List<ID>): Int {
        var count = 0
        RoomUtil.runQueryInChunks(ids) {
            count += _deleteLeftIds(it)
            it
        }
        return count
    }

    suspend fun _deleteRightIds(ids: List<ID>) = _rawQueryListIDs(queryDeleteRight(ids))

    @Transaction
    open suspend fun deleteRightIds(ids: List<ID>): Int {
        var count = 0
        RoomUtil.runQueryInChunks(ids) {
            count += _deleteRightIds(it)
            it
        }
        return count
    }

    suspend fun deleteLeftRight(leftId: ID, rightId: ID) =
        _rawQueryListIDs(queryDelete(leftId, rightId))

    @Delete
    abstract suspend fun _delete(items: List<T>): Int

    @Delete
    abstract suspend fun delete(item: T): Int

    @Transaction
    open suspend fun delete(items: List<T>): Int {
        var count = 0
        RoomUtil.runQueryInChunks(items) {
            count += _delete(it)
            it
        }
        return count
    }

    suspend fun deleteAll() = _rawInt(RoomQueryUtil.deleteAll(tableName))

    // -----------------
    // INSERT / UPDATE
    // -----------------

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    abstract suspend fun insertOrUpdate(items: List<T>): List<Long>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    abstract suspend fun insertOrUpdate(item: T): Long

    // -----------------
    // RAW
    // -----------------

    @RawQuery
    abstract suspend fun _rawQueryList(query: RoomRawQuery): List<T>

    @RawQuery
    abstract suspend fun _rawQueryListIDs(query: RoomRawQuery): Int

    @RawQuery
    abstract suspend fun _rawInt(query: RoomRawQuery): Int

}
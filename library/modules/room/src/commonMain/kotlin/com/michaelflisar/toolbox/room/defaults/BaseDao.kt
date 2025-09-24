package com.michaelflisar.toolbox.room.defaults

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import com.michaelflisar.toolbox.room.RoomUtil
import com.michaelflisar.toolbox.room.interfaces.IRoomEntity
import kotlin.reflect.KClass

@Dao
abstract class BaseDao<ID : Number, T : IRoomEntity<ID, T>>(
    val classID: KClass<ID>,
    val tableName: String,
    val queryAllWhereClause: String?,
    val columnId: String,
) {

    @Suppress("UNCHECKED_CAST")
    private fun convertId(id: Long): ID {
        return when (classID) {
            Int::class -> id.toInt()
            Long::class -> id
            else -> throw RuntimeException("ID class not supported: $classID")
        } as ID
    }

    val queryAll: RoomRawQuery
        get() = RoomRawQuery("SELECT * FROM $tableName " + if (queryAllWhereClause?.isNotEmpty() == true) " WHERE $queryAllWhereClause" else "")

    fun querySingle(id: ID): RoomRawQuery {
        return RoomRawQuery("SELECT * FROM $tableName WHERE ($columnId = $id or ($columnId is null and $id IS NULL))")
    }

    fun queryAllIn(ids: List<ID>): RoomRawQuery {
        return RoomRawQuery("SELECT * FROM $tableName WHERE $columnId IN (${ids.joinToString(",")})")
    }

    // -----------------
    // GET
    // -----------------

    suspend fun getAll() = rawQueryList(queryAll)

    suspend fun getSingle(id: ID) = rawQuerySingle(querySingle(id))
    suspend fun getSingleNullable(id: ID) = rawQuerySingleNullable(querySingle(id))

    suspend fun _getAllIn(ids: List<ID>) = rawQueryList(queryAllIn(ids))

    suspend fun getAllIn(ids: List<ID>): List<T> {
        return RoomUtil.runQueryInChunks(ids) {
            _getAllIn(it)
        }
    }

    // -----------------
    // DELETE
    // -----------------

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

    // -----------------
    // INSERT / UPDATE
    // -----------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun _insertOrUpdate(items: List<T>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun _insertOrUpdate(item: T): Long

    suspend fun insertOrUpdate(items: List<T>): List<T> {
        return RoomUtil.runQueryInChunks(items) { items ->
            val newIds = _insertOrUpdate(items)
            newIds.mapIndexed { index, newId ->
                items[index].copyWithId(convertId(newId))
            }
        }
    }

    suspend fun insertOrUpdate(item: T): T {
        val newId = _insertOrUpdate(item)
        return item.copyWithId(convertId(newId))
    }

    // -----------------
    // RAW
    // -----------------

    @RawQuery
    abstract suspend fun rawQueryList(query: RoomRawQuery): List<T>

    @RawQuery
    abstract suspend fun rawQuerySingle(query: RoomRawQuery): T

    @RawQuery
    abstract suspend fun rawQuerySingleNullable(query: RoomRawQuery): T?

    @RawQuery
    abstract suspend fun rawQueryCount(query: RoomRawQuery): Int
}
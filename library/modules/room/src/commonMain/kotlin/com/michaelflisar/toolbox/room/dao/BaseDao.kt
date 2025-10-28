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
import com.michaelflisar.toolbox.room.interfaces.IRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BaseDaoLong<T : IRoomEntity<Long, T>>(
    tableName: String,
    columnId: String,
    sortAll: String? = null
) : BaseDao<Long, T>(0L, tableName, columnId, sortAll)

@Dao
abstract class BaseDaoInt<T : IRoomEntity<Int, T>>(
    tableName: String,
    columnId: String,
    sortAll: String? = null
) : BaseDao<Int, T>(0, tableName, columnId, sortAll)

@Dao
abstract class BaseDao<ID : Number, T : IRoomEntity<ID, T>> internal constructor(
    private val classIDInstance: ID,
    val tableName: String,
    val columnId: String,
    val sortAll: String?
) {
    // -----------------
    // private functions
    // -----------------

    @Suppress("UNCHECKED_CAST")
    private fun convertId(id: Long): ID {
        return when (classIDInstance) {
            Int::class -> id.toInt()
            Long::class -> id
            else -> throw RuntimeException("ID class not supported: $classIDInstance")
        } as ID
    }

    // -----------------
    // flows ("private")
    // -----------------

    abstract fun _flowList(query: RoomRawQuery): Flow<List<T>>
    abstract fun _flowInt(query: RoomRawQuery): Flow<Int>
    abstract fun _flowItem(query: RoomRawQuery): Flow<T>
    //abstract fun _flowItemOrNull(query: RoomRawQuery): Flow<T?>

    // ---------------
    // Flows
    // ---------------

    // count
    fun flowCount() = _flowInt(RoomQueryUtil.count(tableName))

    // load
    fun flowAll() = _flowList(RoomQueryUtil.loadAll(tableName, sortAll))
    fun flow(id: ID) = _flowItem(RoomQueryUtil.load(tableName, columnId, id))
    fun flowAllIn(ids: List<ID>) = _flowList(RoomQueryUtil.loadAllIn(tableName, columnId, ids))

    // -----------------
    // Count
    // -----------------

    suspend fun count() = rawInt(RoomQueryUtil.count(tableName))

    // -----------------
    // GET
    // -----------------

    suspend fun loadAll() = rawList(RoomQueryUtil.loadAll(tableName, sortAll))

    suspend fun load(id: ID): T =
        rawItem(RoomQueryUtil.load(tableName, columnId, id))

    suspend fun tryLoad(id: ID): T? =
        rawItemOrNull(RoomQueryUtil.load(tableName, columnId, id))

    suspend fun _loadAllIn(ids: List<ID>): List<T> =
        rawList(RoomQueryUtil.loadAllIn(tableName, columnId, ids))

    suspend fun loadAllIn(ids: List<ID>): List<T> =
        RoomUtil.runQueryInChunks(ids) { _loadAllIn(it) }

    // -----------------
    // DELETE
    // -----------------

    @Delete
    abstract suspend fun _delete(items: List<T>): Int

    @Delete
    abstract suspend fun delete(item: T): Int

    @Transaction
    open suspend fun delete(items: List<T>): Int =
        RoomUtil.runQueryInChunksAndReturnCount(items) { _delete(it) }

    suspend fun deleteById(id: ID): Int =
        rawInt(RoomQueryUtil.delete(tableName, columnId, id))

    @Transaction
    open suspend fun _deleteById(ids: List<ID>): Int =
        rawInt(RoomQueryUtil.deleteAllIn(tableName, columnId, ids))

    @Transaction
    open suspend fun deleteById(ids: List<ID>): Int =
        RoomUtil.runQueryInChunksAndReturnCount(ids) { _deleteById(it) }

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

    suspend fun insertOrUpdate(item: T): T = item.copyWithId(convertId(_insertOrUpdate(item)))

    // -----------------
    // RAW
    // -----------------

    @RawQuery
    abstract suspend fun rawList(query: RoomRawQuery): List<T>

    @RawQuery
    abstract suspend fun rawItem(query: RoomRawQuery): T

    @RawQuery
    abstract suspend fun rawItemOrNull(query: RoomRawQuery): T?

    @RawQuery
    abstract suspend fun rawInt(query: RoomRawQuery): Int
}
package com.michaelflisar.toolbox.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.execSQL
import com.michaelflisar.toolbox.numbers.NumberUtil
import com.michaelflisar.toolbox.room.RoomQueryUtil
import com.michaelflisar.toolbox.room.RoomUtil
import com.michaelflisar.toolbox.room.interfaces.IRoomEntity
import kotlinx.coroutines.flow.Flow

interface IBaseDao<ID : Number, Entity : IRoomEntity<ID, Entity>> {

    val classIDInstance: ID
    val tableName: String
    val columnId: String
    val sortAll: String?

    // -----------------
    // "private" functions
    // -----------------

    @Suppress("UNCHECKED_CAST")
    fun convertId(id: Long): ID {
        return when (classIDInstance::class) {
            Int::class -> id.toInt()
            Long::class -> id
            else -> throw RuntimeException("ID class not supported: ${classIDInstance::class}")
        } as ID
    }

    // -----------------
    // flows ("private")
    // -----------------

    fun _flowList(query: RoomRawQuery): Flow<List<Entity>>
    fun _flowInt(query: RoomRawQuery): Flow<Int>
    fun _flowItem(query: RoomRawQuery): Flow<Entity>
    // fun _flowItemOrNull(query: RoomRawQuery): Flow<T?>

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

    suspend fun load(id: ID): Entity =
        rawItem(RoomQueryUtil.load(tableName, columnId, id))

    suspend fun tryLoad(id: ID): Entity? =
        rawItemOrNull(RoomQueryUtil.load(tableName, columnId, id))

    suspend fun _loadAllIn(ids: List<ID>): List<Entity> =
        rawList(RoomQueryUtil.loadAllIn(tableName, columnId, ids))

    suspend fun loadAllIn(ids: List<ID>): List<Entity> =
        RoomUtil.runQueryInChunks(ids) { _loadAllIn(it) }

    // -----------------
    // DELETE
    // -----------------

    @Delete
    suspend fun _delete(items: List<Entity>): Int

    @Delete
    suspend fun delete(item: Entity): Int

    @Transaction
    suspend fun delete(items: List<Entity>): Int =
        RoomUtil.runQueryInChunksAndReturnCount(items) { _delete(it) }

    suspend fun deleteById(database: RoomDatabase, id: ID): Int {
        return RoomUtil.runInTransaction(database) { transactor ->
            val query = RoomQueryUtil.delete(tableName, columnId, id)
            transactor.execSQL(query.sql)
            RoomUtil.selectChanges(transactor)
        }
    }

    suspend fun deleteByIds(database: RoomDatabase, ids: List<ID>): Int {
        return RoomUtil.runInTransaction(database) { transactor ->
            RoomUtil.runQueryInChunksAndReturnCount(ids) {
                val query = RoomQueryUtil.deleteAllIn(tableName, columnId, it)
                transactor.execSQL(query.sql)
                RoomUtil.selectChanges(transactor)
            }
        }
    }

    // -----------------
    // INSERT / UPDATE
    // -----------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertOrUpdate(items: List<Entity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertOrUpdate(item: Entity): Long

    suspend fun insertOrUpdate(items: List<Entity>): List<Entity> {
        return RoomUtil.runQueryInChunks(items) { items ->
            val newIds = _insertOrUpdate(items)
            newIds.mapIndexed { index, newId ->
                items[index].copyWithId(convertId(newId))
            }
        }
    }

    suspend fun insertOrUpdate(item: Entity): Entity = item.copyWithId(convertId(_insertOrUpdate(item)))

    // -----------------
    // RAW
    // -----------------

    @RawQuery
    suspend fun rawList(query: RoomRawQuery): List<Entity>

    @RawQuery
    suspend fun rawItem(query: RoomRawQuery): Entity

    @RawQuery
    suspend fun rawItemOrNull(query: RoomRawQuery): Entity?

    @RawQuery
    suspend fun rawInt(query: RoomRawQuery): Int
}
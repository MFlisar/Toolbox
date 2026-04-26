package com.michaelflisar.toolbox.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.execSQL
import com.michaelflisar.toolbox.room.RoomQueryUtil
import com.michaelflisar.toolbox.room.RoomUtil
import com.michaelflisar.toolbox.room.interfaces.IRoomEntity

/**
 * public because of @RawQuery functions, but should not be used directly
 */
abstract class BaseDaoInternal<ID : Number, Entity : IRoomEntity<ID, Entity>> {

    @Delete
    abstract suspend fun _delete(items: List<Entity>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun _insertOrUpdate(items: List<Entity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun _insertOrUpdate(item: Entity): Long

    @RawQuery
    abstract suspend fun _rawList(query: RoomRawQuery): List<Entity>

    @RawQuery
    abstract suspend fun _rawItem(query: RoomRawQuery): Entity

    @RawQuery
    abstract suspend fun _rawItemOrNull(query: RoomRawQuery): Entity?

    @RawQuery
    abstract suspend fun _rawInt(query: RoomRawQuery): Int

}

abstract class BaseDao<ID : Number, Entity : IRoomEntity<ID, Entity>>(
    val id: DaoID<ID>,
    val tableName: String,
    val columnId: String,
    val sortAll: String? = null
) : BaseDaoInternal<ID, Entity>() {

    // -----------------
    // Count
    // -----------------

    suspend fun count() = _rawInt(RoomQueryUtil.count(tableName))

    // -----------------
    // GET
    // -----------------

    suspend fun loadAll() = _rawList(RoomQueryUtil.loadAll(tableName, sortAll))

    suspend fun load(id: ID): Entity =
        _rawItem(RoomQueryUtil.load(tableName, columnId, id))

    suspend fun tryLoad(id: ID): Entity? =
        _rawItemOrNull(RoomQueryUtil.load(tableName, columnId, id))

    // -----------------
    // DELETE
    // -----------------

    @Delete
    abstract suspend fun delete(item: Entity): Int

    @Transaction
    open suspend fun delete(items: List<Entity>): Int =
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

    suspend fun deleteAll() = _rawInt(RoomQueryUtil.deleteAll(tableName))

    // -----------------
    // INSERT / UPDATE
    // -----------------

    suspend fun persist(items: List<Entity>): List<Entity> {
        return RoomUtil.runQueryInChunks(items) { items ->
            val newIds = _insertOrUpdate(items)
            newIds.mapIndexed { index, newId ->
                items[index].copyWithId(id.fromRowId(newId))
            }
        }
    }

    suspend fun persist(item: Entity): Entity =
        item.copyWithId(id.fromRowId(_insertOrUpdate(item)))
}